package com.cleantrack.controller;
import com.cleantrack.entity.*;
import com.cleantrack.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
/** USER role endpoints */
@RestController @RequestMapping("/api/complaints")
public class ComplaintController {
  public record ComplaintReq(@NotBlank(message="Description is required") @Size(max=1000, message="Description is too long") String description,
                             @NotBlank(message="Location is required") String location) {}
  public record FeedbackReq(@Min(value=1, message="Rating must be 1 to 5") @Max(value=5, message="Rating must be 1 to 5") int rating, String comment) {}
  private final ComplaintService cs; private final UserService us;
  public ComplaintController(ComplaintService cs, UserService us){ this.cs=cs; this.us=us; }

  @PostMapping
  public ResponseEntity<Complaint> create(@Valid @RequestBody ComplaintReq r, Principal p){
    return ResponseEntity.status(HttpStatus.CREATED).body(cs.create(us.byEmail(p.getName()), r.description(), r.location()));
  }
  @GetMapping("/my")
  public List<Complaint> mine(Principal p){ return cs.mine(us.byEmail(p.getName())); }

  @PostMapping("/{id}/feedback")
  public ResponseEntity<Feedback> feedback(@PathVariable Long id, @Valid @RequestBody FeedbackReq r, Principal p){
    return ResponseEntity.status(HttpStatus.CREATED).body(cs.feedback(id, us.byEmail(p.getName()), r.rating(), r.comment()));
  }
}
