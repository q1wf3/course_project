package ru.skfu.moviecollection.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MovieTest {
    @Test
    void constructorNormalizesCategory() {
        var movie = new Movie("Arrival", 2016, "Denis Villeneuve", 116, null, null, "  Фантастика  ");

        assertEquals("Фантастика", movie.getCategory());
    }

    @Test
    void constructorUsesDefaultCategoryWhenBlank() {
        var movie = new Movie("Arrival", 2016, "Denis Villeneuve", 116, null, null, " ");

        assertEquals("Без категории", movie.getCategory());
    }

    @Test
    void renameRejectsBlankTitle() {
        var movie = new Movie("Arrival", 2016, "Denis Villeneuve", 116, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> movie.rename(" "));
    }

    @Test
    void changeReleaseYearRejectsTooEarlyYear() {
        var movie = new Movie("Arrival", 2016, "Denis Villeneuve", 116, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> movie.changeReleaseYear(1800));
    }
}
