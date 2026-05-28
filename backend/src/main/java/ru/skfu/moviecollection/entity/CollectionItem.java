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
@Table(name = "collection_items")
public class CollectionItem {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WatchStatus status;

    private Integer rating;
    private String note;
    private boolean favorite;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected CollectionItem() {
    }

    public CollectionItem(User owner, Movie movie, WatchStatus status) {
        this.owner = owner;
        this.movie = movie;
        changeStatus(status);
    }

    public void changeStatus(WatchStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Статус просмотра обязателен");
        }
        status = newStatus;
        updatedAt = LocalDateTime.now();
    }

    public void rate(int newRating) {
        if (newRating < 1 || newRating > 10) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 10");
        }
        rating = newRating;
        updatedAt = LocalDateTime.now();
    }

    public void clearRating() {
        rating = null;
        updatedAt = LocalDateTime.now();
    }

    public void updateNote(String newNote) {
        note = newNote;
        updatedAt = LocalDateTime.now();
    }

    public void toggleFavorite() {
        favorite = !favorite;
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public Movie getMovie() {
        return movie;
    }

    public WatchStatus getStatus() {
        return status;
    }

    public Integer getRating() {
        return rating;
    }

    public String getNote() {
        return note;
    }

    public boolean isFavorite() {
        return favorite;
    }
}

