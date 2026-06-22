package ru.skfu.moviecollection.mediator;

import java.util.List;
import java.util.UUID;
import ru.skfu.moviecollection.control.dto.ComplaintDto;
import ru.skfu.moviecollection.control.dto.CreateComplaintCommand;
import ru.skfu.moviecollection.control.dto.UpdateComplaintStatusCommand;
import ru.skfu.moviecollection.entity.ComplaintStatus;

public interface ComplaintService {
    ComplaintDto createComplaint(UUID reporterId, CreateComplaintCommand command);

    List<ComplaintDto> getComplaints(ComplaintStatus status);

    List<ComplaintDto> getUserComplaints(UUID reporterId);

    ComplaintDto updateComplaintStatus(UUID complaintId, UpdateComplaintStatusCommand command);
}
