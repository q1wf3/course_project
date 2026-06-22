package ru.skfu.moviecollection.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.ComplaintDto;
import ru.skfu.moviecollection.control.dto.UpdateComplaintStatusCommand;
import ru.skfu.moviecollection.entity.CollectionItem;
import ru.skfu.moviecollection.entity.Movie;
import ru.skfu.moviecollection.entity.ComplaintStatus;
import ru.skfu.moviecollection.entity.Role;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.entity.WatchStatus;
import ru.skfu.moviecollection.foundation.CollectionItemRepository;
import ru.skfu.moviecollection.foundation.ComplaintRepository;
import ru.skfu.moviecollection.foundation.MovieMapper;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;
import ru.skfu.moviecollection.mediator.ComplaintService;

class AdminControllerTest {
    private final JwtService jwtService = mock(JwtService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final CollectionItemRepository collectionItemRepository = mock(CollectionItemRepository.class);
    private final ComplaintRepository complaintRepository = mock(ComplaintRepository.class);
    private final ComplaintService complaintService = mock(ComplaintService.class);
    private final MovieMapper movieMapper = new MovieMapper();
    private final AdminController controller = new AdminController(
            jwtService,
            userRepository,
            movieRepository,
            collectionItemRepository,
            complaintRepository,
            complaintService,
            movieMapper
    );

    @Test
    void statsReturnsRepositoryCounters() {
        var authorization = "Bearer admin";
        when(userRepository.count()).thenReturn(2L);
        when(movieRepository.count()).thenReturn(5L);
        when(collectionItemRepository.count()).thenReturn(7L);
        when(complaintRepository.countByStatusIn(List.of(ComplaintStatus.NEW, ComplaintStatus.IN_PROGRESS))).thenReturn(3L);

        var result = controller.stats(authorization);

        assertEquals(2L, result.usersCount());
        assertEquals(5L, result.moviesCount());
        assertEquals(7L, result.collectionItemsCount());
        assertEquals(3L, result.openComplaintsCount());
        verify(jwtService).requireAdmin(authorization);
    }

    @Test
    void usersAreSortedByEmailAndIncludeMovieCount() {
        var admin = new User("admin@movie.local", "hash", Role.ADMIN);
        var user = new User("test@yandex.ru", "hash", Role.USER);
        when(userRepository.findAll()).thenReturn(List.of(user, admin));
        when(collectionItemRepository.countByOwnerId(admin.getId())).thenReturn(1L);
        when(collectionItemRepository.countByOwnerId(user.getId())).thenReturn(20L);

        var result = controller.users("Bearer admin");

        assertEquals("admin@movie.local", result.get(0).email());
        assertEquals(1L, result.get(0).moviesCount());
        assertEquals("test@yandex.ru", result.get(1).email());
        assertEquals(20L, result.get(1).moviesCount());
    }

    @Test
    void userMoviesMapsCollectionItemsToDto() {
        var owner = new User("test@yandex.ru", "hash");
        var movie = new Movie("Брат", 1997, "Алексей Балабанов", 100, null, null, "Драма");
        var item = new CollectionItem(owner, movie, WatchStatus.WATCHED);
        when(collectionItemRepository.findByOwnerIdOrderByMovieTitle(owner.getId())).thenReturn(List.of(item));

        var result = controller.userMovies("Bearer admin", owner.getId());

        assertEquals(1, result.size());
        assertEquals("Брат", result.get(0).title());
        assertEquals(WatchStatus.WATCHED, result.get(0).status());
    }

    @Test
    void changeRoleRejectsRemovingCurrentAdminRole() {
        var adminId = UUID.randomUUID();
        when(jwtService.requireAdmin("Bearer admin"))
                .thenReturn(new JwtService.JwtClaims(adminId, "admin@movie.local", Role.ADMIN));

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.changeRole("Bearer admin", adminId, Role.USER)
        );
    }

    @Test
    void changeRoleSavesSelectedUser() {
        var currentAdminId = UUID.randomUUID();
        var user = new User("test@yandex.ru", "hash");
        when(jwtService.requireAdmin("Bearer admin"))
                .thenReturn(new JwtService.JwtClaims(currentAdminId, "admin@movie.local", Role.ADMIN));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(collectionItemRepository.countByOwnerId(user.getId())).thenReturn(3L);

        var result = controller.changeRole("Bearer admin", user.getId(), Role.ADMIN);

        assertEquals(Role.ADMIN, result.role());
        assertEquals(3L, result.moviesCount());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserRejectsCurrentAdminAndDeletesAnotherUser() {
        var currentAdminId = UUID.randomUUID();
        var user = new User("test@yandex.ru", "hash");
        when(jwtService.requireAdmin("Bearer admin"))
                .thenReturn(new JwtService.JwtClaims(currentAdminId, "admin@movie.local", Role.ADMIN));

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.deleteUser("Bearer admin", currentAdminId)
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        controller.deleteUser("Bearer admin", user.getId());

        verify(collectionItemRepository).deleteByOwnerId(user.getId());
        verify(complaintRepository).deleteByReporterId(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void complaintsRequireAdminAndDelegateToService() {
        var complaint = new ComplaintDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "test@yandex.ru",
                UUID.randomUUID(),
                "Брат",
                "Ошибка в карточке",
                "Неверный год",
                ComplaintStatus.NEW,
                null,
                null,
                null
        );
        when(complaintService.getComplaints(ComplaintStatus.NEW)).thenReturn(List.of(complaint));

        var result = controller.complaints("Bearer admin", ComplaintStatus.NEW);

        assertEquals(1, result.size());
        assertEquals("Брат", result.get(0).movieTitle());
        verify(jwtService).requireAdmin("Bearer admin");
        verify(complaintService).getComplaints(ComplaintStatus.NEW);
    }

    @Test
    void updateComplaintStatusRequiresAdminAndDelegatesToService() {
        var complaintId = UUID.randomUUID();
        var command = new UpdateComplaintStatusCommand(ComplaintStatus.RESOLVED, "Проверено");
        var complaint = new ComplaintDto(
                complaintId,
                UUID.randomUUID(),
                "test@yandex.ru",
                UUID.randomUUID(),
                "Брат",
                "Ошибка",
                "Описание",
                ComplaintStatus.RESOLVED,
                "Проверено",
                null,
                null
        );
        when(complaintService.updateComplaintStatus(complaintId, command)).thenReturn(complaint);

        var result = controller.updateComplaintStatus("Bearer admin", complaintId, command);

        assertEquals(ComplaintStatus.RESOLVED, result.status());
        verify(jwtService).requireAdmin("Bearer admin");
        verify(complaintService).updateComplaintStatus(complaintId, command);
    }
}
