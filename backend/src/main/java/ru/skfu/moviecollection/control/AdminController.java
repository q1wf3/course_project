package ru.skfu.moviecollection.control;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.AdminStatsDto;
import ru.skfu.moviecollection.control.dto.AdminUserDto;
import ru.skfu.moviecollection.control.dto.ComplaintDto;
import ru.skfu.moviecollection.control.dto.MovieDto;
import ru.skfu.moviecollection.control.dto.UpdateComplaintStatusCommand;
import ru.skfu.moviecollection.entity.ComplaintStatus;
import ru.skfu.moviecollection.entity.Role;
import ru.skfu.moviecollection.foundation.CollectionItemRepository;
import ru.skfu.moviecollection.foundation.MovieMapper;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;
import ru.skfu.moviecollection.foundation.ComplaintRepository;
import ru.skfu.moviecollection.mediator.ComplaintService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CollectionItemRepository collectionItemRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintService complaintService;
    private final MovieMapper movieMapper;

    public AdminController(
            JwtService jwtService,
            UserRepository userRepository,
            MovieRepository movieRepository,
            CollectionItemRepository collectionItemRepository,
            ComplaintRepository complaintRepository,
            ComplaintService complaintService,
            MovieMapper movieMapper
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.collectionItemRepository = collectionItemRepository;
        this.complaintRepository = complaintRepository;
        this.complaintService = complaintService;
        this.movieMapper = movieMapper;
    }

    @GetMapping("/stats")
    public AdminStatsDto stats(@RequestHeader("Authorization") String authorization) {
        jwtService.requireAdmin(authorization);
        return new AdminStatsDto(
                userRepository.count(),
                movieRepository.count(),
                collectionItemRepository.count(),
                complaintRepository.countByStatusIn(List.of(ComplaintStatus.NEW, ComplaintStatus.IN_PROGRESS))
        );
    }

    @GetMapping("/users")
    public List<AdminUserDto> users(@RequestHeader("Authorization") String authorization) {
        jwtService.requireAdmin(authorization);
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(user -> user.getEmail().toLowerCase()))
                .map(user -> new AdminUserDto(
                        user.getId(),
                        user.getEmail(),
                        user.getRole(),
                        collectionItemRepository.countByOwnerId(user.getId())
                ))
                .toList();
    }

    @GetMapping("/users/{userId}/movies")
    public List<MovieDto> userMovies(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID userId
    ) {
        jwtService.requireAdmin(authorization);
        return collectionItemRepository.findByOwnerIdOrderByMovieTitle(userId).stream()
                .map(movieMapper::toDto)
                .toList();
    }

    @PutMapping("/users/{userId}/role")
    @Transactional
    public AdminUserDto changeRole(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID userId,
            @RequestParam Role role
    ) {
        var admin = jwtService.requireAdmin(authorization);
        if (admin.userId().equals(userId) && role != Role.ADMIN) {
            throw new IllegalArgumentException("Нельзя снять роль ADMIN с текущего администратора");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.changeRole(role);
        var savedUser = userRepository.save(user);
        return new AdminUserDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                collectionItemRepository.countByOwnerId(savedUser.getId())
        );
    }

    @GetMapping("/complaints")
    public List<ComplaintDto> complaints(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) ComplaintStatus status
    ) {
        jwtService.requireAdmin(authorization);
        return complaintService.getComplaints(status);
    }

    @PutMapping("/complaints/{complaintId}/status")
    public ComplaintDto updateComplaintStatus(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID complaintId,
            @Valid @RequestBody UpdateComplaintStatusCommand command
    ) {
        jwtService.requireAdmin(authorization);
        return complaintService.updateComplaintStatus(complaintId, command);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteUser(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID userId
    ) {
        var admin = jwtService.requireAdmin(authorization);
        if (admin.userId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя удалить текущего администратора");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        collectionItemRepository.deleteByOwnerId(user.getId());
        complaintRepository.deleteByReporterId(user.getId());
        userRepository.delete(user);
    }
}
