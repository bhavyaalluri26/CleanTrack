package com.cleantrack.controller;
import com.cleantrack.entity.*;
import com.cleantrack.service.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
/** ADMIN role endpoints */
@RestController @RequestMapping("/api/admin")
public class AdminController {
  private final ComplaintService cs; private final UserService us;
  public AdminController(ComplaintService cs, UserService us){ this.cs=cs; this.us=us; }

  @GetMapping("/complaints")
  public List<Complaint> list(@RequestParam(required=false) String status, @RequestParam(required=false) String q){ return cs.search(status, q); }

  @PutMapping("/complaints/{id}")
  public Complaint edit(@PathVariable Long id, @Valid @RequestBody ComplaintController.ComplaintReq r){ return cs.edit(id, r.description(), r.location()); }

  @PutMapping("/complaints/{id}/assign/{collectorId}")
  public Complaint assign(@PathVariable Long id, @PathVariable Long collectorId){ return cs.assign(id, collectorId); }

  @GetMapping("/stats")
  public Map<String,Long> stats(){ return cs.stats(); }

  @GetMapping("/users")
  public List<User> users(){ return us.all(); }

  @PostMapping("/collectors")
  public ResponseEntity<User> addCollector(@Valid @RequestBody AuthController.RegisterReq r){
    return ResponseEntity.status(HttpStatus.CREATED).body(us.create(r.name(), r.email(), r.password(), Role.COLLECTOR));
  }
  @DeleteMapping("/users/{id}")
  public Map<String,String> delete(@PathVariable Long id){ us.delete(id); return Map.of("message", "User deleted"); }
}
