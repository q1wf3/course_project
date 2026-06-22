package ru.skfu.moviecollection.control.dto;

public record AdminStatsDto(
        long usersCount,
        long moviesCount,
        long collectionItemsCount,
        long openComplaintsCount
) {
}
