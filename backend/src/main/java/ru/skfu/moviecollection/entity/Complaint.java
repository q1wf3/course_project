package ru.skfu.moviecollection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "reporter_email", nullable = false)
    private String reporterEmail;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(nullable = false, length = 120)
    private String reason;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.NEW;

    @Column(name = "admin_comment", length = 1000)
    private String adminComment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    protected Complaint() {
    }

    public Complaint(User reporter, Movie movie, String reason, String description) {
        if (reporter == null) {
            throw new IllegalArgumentException("Пользователь жалобы обязателен");
        }
        if (movie == null) {
            throw new IllegalArgumentException("Фильм жалобы обязателен");
        }
        this.reporter = reporter;
        this.movie = movie;
        this.reporterEmail = reporter.getEmail();
        this.movieTitle = movie.getTitle();
        changeText(reason, description);
    }

    public void changeText(String newReason, String newDescription) {
        if (newReason == null || newReason.isBlank()) {
            throw new IllegalArgumentException("Причина жалобы обязательна");
        }
        if (newDescription == null || newDescription.isBlank()) {
            throw new IllegalArgumentException("Описание жалобы обязательно");
        }
        reason = newReason.trim();
        description = newDescription.trim();
    }

    public void changeStatus(ComplaintStatus newStatus, String comment) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Статус жалобы обязателен");
        }
        status = newStatus;
        adminComment = comment == null || comment.isBlank() ? null : comment.trim();
        resolvedAt = (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.REJECTED)
                ? LocalDateTime.now()
                : null;
    }

    public UUID getId() {
        return id;
    }

    public User getReporter() {
        return reporter;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}
