package com.cleantrack.service;
import com.cleantrack.entity.*;
import com.cleantrack.exception.ApiException;
import com.cleantrack.repository.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {
  private final UserRepository repo; private final ComplaintRepository complaints; private final PasswordEncoder enc;
  public UserService(UserRepository repo, ComplaintRepository complaints, PasswordEncoder enc){ this.repo=repo; this.complaints=complaints; this.enc=enc; }

  public User create(String name, String email, String password, Role role){
    String mail = email.trim().toLowerCase();
    if(repo.existsByEmail(mail)) throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
    User u = new User(); u.name=name.trim(); u.email=mail; u.password=enc.encode(password); u.role=role;
    return repo.save(u);
  }
  public User byEmail(String email){ return repo.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found")); }
  public List<User> all(){ return repo.findAll(); }
  public void delete(Long id){
    User u = repo.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    if(u.role == Role.ADMIN) throw new ApiException(HttpStatus.BAD_REQUEST, "Admin account cannot be deleted");
    if(u.role == Role.COLLECTOR && complaints.existsByCollector(u)) throw new ApiException(HttpStatus.CONFLICT, "Cannot delete this collector: complaints are linked to them. Reassign their complaints to another collector before deleting. (Complaints already In Progress or Resolved stay on record and cannot be reassigned.)");
    if(complaints.existsByUser(u) || complaints.existsByCollector(u)) throw new ApiException(HttpStatus.CONFLICT, "Cannot delete: this user has related complaints");
    repo.delete(u);
  }
}
