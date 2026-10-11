package com.cleantrack.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
public class Complaint {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  @Column(nullable=false, length=1000) public String description;
  @Column(nullable=false) public String location;
  @Enumerated(EnumType.STRING) @Column(nullable=false) public Status status = Status.PENDING;
  public LocalDateTime createdAt = LocalDateTime.now();
  @ManyToOne(optional=false) @JoinColumn(name="user_id") public User user;
  @ManyToOne @JoinColumn(name="collector_id") public User collector;   // assignment
  @OneToOne(mappedBy="complaint") public Feedback feedback;
}
