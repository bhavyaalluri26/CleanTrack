package com.cleantrack.controller;
import com.cleantrack.entity.*;
import com.cleantrack.service.*;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.*;
/** COLLECTOR role endpoints */
@RestController @RequestMapping("/api/collector")
public class CollectorController {
  private final ComplaintService cs; private final UserService us;
  public CollectorController(ComplaintService cs, UserService us){ this.cs=cs; this.us=us; }

  @GetMapping("/complaints")
  public List<Complaint> assigned(Principal p){ return cs.assignedTo(us.byEmail(p.getName())); }

  @PutMapping("/complaints/{id}/status")
  public Complaint update(@PathVariable Long id, @RequestParam Status status, Principal p){ return cs.advance(id, us.byEmail(p.getName()), status); }
}
