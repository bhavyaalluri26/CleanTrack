package com.cleantrack.service;
import com.cleantrack.entity.*;
import com.cleantrack.exception.ApiException;
import com.cleantrack.repository.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
@Service
public class ComplaintService {
  private final ComplaintRepository cr; private final UserRepository ur; private final FeedbackRepository fr;
  public ComplaintService(ComplaintRepository cr, UserRepository ur, FeedbackRepository fr){ this.cr=cr; this.ur=ur; this.fr=fr; }

  public Complaint create(User user, String description, String location){
    Complaint c = new Complaint(); c.user=user; c.description=description.trim(); c.location=location.trim();
    return cr.save(c);
  }
  public List<Complaint> mine(User u){ return cr.findByUserOrderByCreatedAtDesc(u); }
  public List<Complaint> assignedTo(User u){ return cr.findByCollectorOrderByCreatedAtDesc(u); }

  public List<Complaint> search(String status, String q){
    String k = q == null ? "" : q.trim().toLowerCase();
    return cr.findAllByOrderByCreatedAtDesc().stream()
      .filter(c -> status == null || status.isBlank() || c.status.name().equals(status))
      .filter(c -> k.isEmpty() || c.description.toLowerCase().contains(k) || c.location.toLowerCase().contains(k) || c.user.name.toLowerCase().contains(k))
      .toList();
  }
  public Complaint get(Long id){ return cr.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Complaint not found")); }

  public Complaint edit(Long id, String description, String location){
    // The citizen's original description is read-only: only the location may be changed here
    Complaint c = get(id); c.location=location.trim(); return cr.save(c);
  }
  public Complaint assign(Long id, Long collectorId){
    Complaint c = get(id);
    if(c.status == Status.IN_PROGRESS || c.status == Status.RESOLVED) throw new ApiException(HttpStatus.BAD_REQUEST, "Complaint can no longer be assigned");
    User col = ur.findById(collectorId).filter(u -> u.role == Role.COLLECTOR).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Collector not found"));
    c.collector = col; c.status = Status.ASSIGNED; return cr.save(c);
  }
  // Collector moves ASSIGNED -> IN_PROGRESS -> RESOLVED, only on own complaints
  public Complaint advance(Long id, User collector, Status next){
    Complaint c = get(id);
    if(c.collector == null || !c.collector.id.equals(collector.id)) throw new ApiException(HttpStatus.FORBIDDEN, "This complaint is not assigned to you");
    boolean ok = (c.status == Status.ASSIGNED && next == Status.IN_PROGRESS) || (c.status == Status.IN_PROGRESS && next == Status.RESOLVED);
    if(!ok) throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status change: " + c.status + " -> " + next);
    c.status = next; return cr.save(c);
  }
  public Feedback feedback(Long id, User user, int rating, String comment){
    Complaint c = get(id);
    if(!c.user.id.equals(user.id)) throw new ApiException(HttpStatus.FORBIDDEN, "Not your complaint");
    if(c.status != Status.RESOLVED) throw new ApiException(HttpStatus.BAD_REQUEST, "Feedback is allowed only after resolution");
    if(fr.existsByComplaint(c)) throw new ApiException(HttpStatus.CONFLICT, "Feedback already submitted");
    Feedback f = new Feedback(); f.complaint=c; f.rating=rating; f.comment=comment == null ? "" : comment.trim();
    return fr.save(f);
  }
  public Map<String,Long> stats(){
    Map<String,Long> m = new LinkedHashMap<>();
    m.put("total", cr.count());
    for(Status s : Status.values()) m.put(s.name(), cr.countByStatus(s));
    return m;
  }
}
