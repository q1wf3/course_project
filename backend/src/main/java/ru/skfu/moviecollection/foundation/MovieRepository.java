package ru.skfu.moviecollection.foundation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skfu.moviecollection.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Optional<Movie> findByTitleIgnoreCaseAndReleaseYear(String title, int releaseYear);

    @Query("""
            select m from Movie m
            where lower(m.title) like lower(concat('%', :query, '%'))
            order by m.title
            """)
    List<Movie> searchByTitle(@Param("query") String query);
}
