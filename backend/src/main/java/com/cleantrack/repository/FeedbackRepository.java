package com.cleantrack.repository;
import com.cleantrack.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FeedbackRepository extends JpaRepository<Feedback, Long> { boolean existsByComplaint(Complaint c); }
