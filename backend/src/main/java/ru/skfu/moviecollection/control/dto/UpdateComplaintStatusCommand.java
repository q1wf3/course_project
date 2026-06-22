package ru.skfu.moviecollection.control.dto;

import jakarta.validation.constraints.Size;
import ru.skfu.moviecollection.entity.ComplaintStatus;

public record UpdateComplaintStatusCommand(
        ComplaintStatus status,
        @Size(max = 1000) String adminComment
) {
}
