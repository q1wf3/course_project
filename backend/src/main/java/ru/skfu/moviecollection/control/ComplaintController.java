package ru.skfu.moviecollection.control;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.ComplaintDto;
import ru.skfu.moviecollection.control.dto.CreateComplaintCommand;
import ru.skfu.moviecollection.mediator.ComplaintService;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    private final ComplaintService complaintService;
    private final JwtService jwtService;

    public ComplaintController(ComplaintService complaintService, JwtService jwtService) {
        this.complaintService = complaintService;
        this.jwtService = jwtService;
    }

    @GetMapping("/my")
    public List<ComplaintDto> myComplaints(@RequestHeader("Authorization") String authorization) {
        return complaintService.getUserComplaints(jwtService.resolveUserId(authorization));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintDto create(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateComplaintCommand command
    ) {
        return complaintService.createComplaint(jwtService.resolveUserId(authorization), command);
    }
}
