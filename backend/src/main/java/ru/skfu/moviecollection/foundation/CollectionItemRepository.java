package ru.skfu.moviecollection.foundation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skfu.moviecollection.entity.CollectionItem;
import ru.skfu.moviecollection.entity.WatchStatus;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, UUID> {
    List<CollectionItem> findByOwnerIdOrderByMovieTitle(UUID ownerId);

    List<CollectionItem> findByOwnerIdAndStatusOrderByMovieTitle(UUID ownerId, WatchStatus status);

    Optional<CollectionItem> findByOwnerIdAndMovieId(UUID ownerId, UUID movieId);

    boolean existsByOwnerIdAndMovieId(UUID ownerId, UUID movieId);

    long countByOwnerId(UUID ownerId);

    void deleteByOwnerId(UUID ownerId);
}
