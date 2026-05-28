package ru.skfu.moviecollection.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CollectionItemTest {
    @Test
    void rateRejectsInvalidValue() {
        var user = new User("user@example.com", "hash");
        var movie = new Movie("Interstellar", 2014, "Christopher Nolan", 169, "Sci-fi drama", null, "Фантастика");
        var item = new CollectionItem(user, movie, WatchStatus.PLANNED);

        assertThrows(IllegalArgumentException.class, () -> item.rate(11));
    }

    @Test
    void changeStatusUpdatesStatus() {
        var user = new User("user@example.com", "hash");
        var movie = new Movie("Arrival", 2016, "Denis Villeneuve", 116, "Sci-fi drama", null, "Фантастика");
        var item = new CollectionItem(user, movie, WatchStatus.PLANNED);

        item.changeStatus(WatchStatus.WATCHED);

        assertEquals(WatchStatus.WATCHED, item.getStatus());
    }
}
