package com.cleantrack.exception;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ApiException.class)
  ResponseEntity<Map<String,String>> api(ApiException e){ return ResponseEntity.status(e.status).body(Map.of("message", e.getMessage())); }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String,String>> invalid(MethodArgumentNotValidException e){
    String m = e.getBindingResult().getFieldErrors().stream().map(f -> f.getDefaultMessage()).findFirst().orElse("Invalid input");
    return ResponseEntity.badRequest().body(Map.of("message", m));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<Map<String,String>> unreadable(HttpMessageNotReadableException e){ return ResponseEntity.badRequest().body(Map.of("message","Invalid request body")); }
}
