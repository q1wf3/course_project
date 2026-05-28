package ru.skfu.moviecollection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String title;

    @Column(name = "release_year")
    private int releaseYear;

    private String director;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    private String description;

    @Column(name = "cover_url", length = 1000)
    private String coverUrl;

    private String category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    protected Movie() {
    }

    public Movie(
            String title,
            int releaseYear,
            String director,
            int durationMinutes,
            String description,
            String coverUrl,
            String category
    ) {
        updateDetails(title, releaseYear, director, durationMinutes, description, coverUrl, category);
    }

    public void rename(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Название фильма обязательно");
        }
        title = newTitle.trim();
    }

    public void changeReleaseYear(int year) {
        if (year < 1888 || year > 2100) {
            throw new IllegalArgumentException("Год выпуска фильма некорректен");
        }
        releaseYear = year;
    }

    public void updateDetails(
            String newTitle,
            int newReleaseYear,
            String newDirector,
            int newDurationMinutes,
            String newDescription,
            String newCoverUrl,
            String newCategory
    ) {
        rename(newTitle);
        changeReleaseYear(newReleaseYear);
        director = newDirector;
        durationMinutes = newDurationMinutes;
        description = newDescription;
        coverUrl = newCoverUrl;
        category = normalizeCategory(newCategory);
    }

    private String normalizeCategory(String value) {
        if (value == null || value.isBlank()) {
            return "Без категории";
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getDirector() {
        return director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getCategory() {
        return category;
    }

    public Set<Genre> getGenres() {
        return genres;
    }
}

