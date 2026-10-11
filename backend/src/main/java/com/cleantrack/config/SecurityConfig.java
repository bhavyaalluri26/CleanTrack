package com.cleantrack.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cleantrack.entity.Role;
import com.cleantrack.repository.UserRepository;
@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

  @Bean UserDetailsService userDetailsService(UserRepository repo){
    return email -> {
      com.cleantrack.entity.User u = repo.findByEmail(email.toLowerCase()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
      return org.springframework.security.core.userdetails.User.withUsername(u.email).password(u.password).roles(u.role.name()).build();
    };
  }

  @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> res.sendError(401, "Unauthorized")))
        .authorizeHttpRequests(a -> a
          .requestMatchers("/api/auth/register").permitAll()
          .requestMatchers("/api/auth/me").authenticated()
          .requestMatchers("/api/admin/**").hasRole("ADMIN")
          .requestMatchers("/api/collector/**").hasRole("COLLECTOR")
          .requestMatchers("/api/complaints/**").hasRole("USER")
          .anyRequest().permitAll());
    return http.build();
  }

  // Creates the first admin (and a demo collector) if they do not exist
  @Bean CommandLineRunner seed(UserRepository repo, PasswordEncoder enc){
    return args -> {
      if(!repo.existsByRole(Role.ADMIN)) add(repo, enc, "Admin", "admin@cleantrack.com", "Admin@123", Role.ADMIN);
      if(!repo.existsByEmail("collector@cleantrack.com")) add(repo, enc, "Demo Collector", "collector@cleantrack.com", "Collector@123", Role.COLLECTOR);
    };
  }
  private void add(UserRepository r, PasswordEncoder e, String n, String m, String p, Role role){
    com.cleantrack.entity.User u = new com.cleantrack.entity.User();
    u.name=n; u.email=m; u.password=e.encode(p); u.role=role; r.save(u);
  }
}
