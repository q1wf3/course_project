package ru.skfu.moviecollection.foundation;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skfu.moviecollection.entity.Complaint;
import ru.skfu.moviecollection.entity.ComplaintStatus;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    List<Complaint> findAllByOrderByCreatedAtDesc();

    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);

    List<Complaint> findByReporterIdOrderByCreatedAtDesc(UUID reporterId);

    long countByStatusIn(Collection<ComplaintStatus> statuses);

    void deleteByReporterId(UUID reporterId);
}
