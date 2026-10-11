package com.cleantrack.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
public class Feedback {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  @JsonIgnore @OneToOne(optional=false) @JoinColumn(name="complaint_id", unique=true) public Complaint complaint;
  public int rating;
  @Column(length=500) public String comment;
  public LocalDateTime createdAt = LocalDateTime.now();
}
