package ru.skfu.moviecollection.mediator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import ru.skfu.moviecollection.control.dto.CreateComplaintCommand;
import ru.skfu.moviecollection.control.dto.UpdateComplaintStatusCommand;
import ru.skfu.moviecollection.entity.Complaint;
import ru.skfu.moviecollection.entity.ComplaintStatus;
import ru.skfu.moviecollection.entity.Movie;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.foundation.ComplaintRepository;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;

import static org.mockito.Mockito.mock;

class ComplaintServiceImplTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final MovieRepository movieRepository = mock(MovieRepository.class);
    private final ComplaintRepository complaintRepository = mock(ComplaintRepository.class);
    private final ComplaintServiceImpl service = new ComplaintServiceImpl(
            userRepository,
            movieRepository,
            complaintRepository
    );

    @Test
    void createComplaintSavesReporterMovieAndText() {
        var user = new User("test@yandex.ru", "hash");
        var movie = new Movie("Брат", 1997, "Алексей Балабанов", 100, null, null, "Драма");
        var command = new CreateComplaintCommand(movie.getId(), "Ошибка в карточке", "Неверно указан режиссер");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.createComplaint(user.getId(), command);

        assertEquals("test@yandex.ru", result.reporterEmail());
        assertEquals("Брат", result.movieTitle());
        assertEquals(ComplaintStatus.NEW, result.status());
        verify(complaintRepository).save(any(Complaint.class));
    }

    @Test
    void getUserComplaintsReturnsOnlyReporterItems() {
        var user = new User("test@yandex.ru", "hash");
        var movie = new Movie("Опенгеймер", 2023, "Кристофер Нолан", 180, null, null, "Биография");
        var complaint = new Complaint(user, movie, "Ошибка", "Описание");
        when(complaintRepository.findByReporterIdOrderByCreatedAtDesc(user.getId())).thenReturn(java.util.List.of(complaint));

        var result = service.getUserComplaints(user.getId());

        assertEquals(1, result.size());
        assertEquals("Опенгеймер", result.get(0).movieTitle());
        verify(complaintRepository).findByReporterIdOrderByCreatedAtDesc(user.getId());
    }

    @Test
    void updateComplaintStatusStoresAdminComment() {
        var user = new User("test@yandex.ru", "hash");
        var movie = new Movie("Брат", 1997, "Алексей Балабанов", 100, null, null, "Драма");
        var complaint = new Complaint(user, movie, "Ошибка", "Описание");
        var command = new UpdateComplaintStatusCommand(ComplaintStatus.RESOLVED, "Исправлено");
        when(complaintRepository.findById(complaint.getId())).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(complaint)).thenReturn(complaint);

        var result = service.updateComplaintStatus(complaint.getId(), command);

        assertEquals(ComplaintStatus.RESOLVED, result.status());
        assertEquals("Исправлено", result.adminComment());
        verify(complaintRepository).save(complaint);
    }
}
