package com.cleantrack.controller;
import com.cleantrack.entity.*;
import com.cleantrack.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
  public record RegisterReq(@NotBlank(message="Name is required") String name,
                            @NotBlank(message="Email is required") @Email(message="Enter a valid email") String email,
                            @NotBlank(message="Password is required") @Size(min=6, message="Password must be at least 6 characters") String password) {}
  private final UserService users;
  public AuthController(UserService users){ this.users = users; }

  @PostMapping("/register")
  public ResponseEntity<User> register(@Valid @RequestBody RegisterReq r){
    return ResponseEntity.status(HttpStatus.CREATED).body(users.create(r.name(), r.email(), r.password(), Role.USER));
  }
  @GetMapping("/me")
  public User me(Principal p){ return users.byEmail(p.getName()); }
}
