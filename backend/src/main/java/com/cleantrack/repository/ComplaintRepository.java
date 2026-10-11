package com.cleantrack.repository;
import com.cleantrack.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
  List<Complaint> findByUserOrderByCreatedAtDesc(User user);
  List<Complaint> findByCollectorOrderByCreatedAtDesc(User collector);
  List<Complaint> findAllByOrderByCreatedAtDesc();
  long countByStatus(Status status);
  boolean existsByUser(User user);
  boolean existsByCollector(User collector);
}
