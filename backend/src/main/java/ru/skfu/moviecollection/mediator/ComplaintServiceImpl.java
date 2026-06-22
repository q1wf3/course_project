package ru.skfu.moviecollection.mediator;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.moviecollection.control.dto.ComplaintDto;
import ru.skfu.moviecollection.control.dto.CreateComplaintCommand;
import ru.skfu.moviecollection.control.dto.UpdateComplaintStatusCommand;
import ru.skfu.moviecollection.entity.Complaint;
import ru.skfu.moviecollection.entity.ComplaintStatus;
import ru.skfu.moviecollection.foundation.ComplaintRepository;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImpl(
            UserRepository userRepository,
            MovieRepository movieRepository,
            ComplaintRepository complaintRepository
    ) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    @Transactional
    public ComplaintDto createComplaint(UUID reporterId, CreateComplaintCommand command) {
        var reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        var movie = movieRepository.findById(command.movieId())
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден"));
        var complaint = new Complaint(reporter, movie, command.reason(), command.description());
        return toDto(complaintRepository.save(complaint));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getComplaints(ComplaintStatus status) {
        var complaints = status == null
                ? complaintRepository.findAllByOrderByCreatedAtDesc()
                : complaintRepository.findByStatusOrderByCreatedAtDesc(status);
        return complaints.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getUserComplaints(UUID reporterId) {
        return complaintRepository.findByReporterIdOrderByCreatedAtDesc(reporterId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ComplaintDto updateComplaintStatus(UUID complaintId, UpdateComplaintStatusCommand command) {
        var complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Жалоба не найдена"));
        complaint.changeStatus(command.status(), command.adminComment());
        return toDto(complaintRepository.save(complaint));
    }

    private ComplaintDto toDto(Complaint complaint) {
        return new ComplaintDto(
                complaint.getId(),
                complaint.getReporter().getId(),
                complaint.getReporterEmail(),
                complaint.getMovie().getId(),
                complaint.getMovieTitle(),
                complaint.getReason(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getAdminComment(),
                complaint.getCreatedAt(),
                complaint.getResolvedAt()
        );
    }
}
