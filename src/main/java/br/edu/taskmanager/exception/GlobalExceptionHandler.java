package br.edu.taskmanager.exception;
import java.time.Instant; import java.util.LinkedHashMap; import java.util.Map;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(TaskNotFoundException.class)
 ResponseEntity<ApiError> notFound(TaskNotFoundException ex){return ResponseEntity.status(404).body(new ApiError(Instant.now(),404,ex.getMessage(),Map.of()));}
 @ExceptionHandler(MethodArgumentNotValidException.class)
 ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex){Map<String,String> errors=new LinkedHashMap<>();ex.getBindingResult().getFieldErrors().forEach(e->errors.putIfAbsent(e.getField(),e.getDefaultMessage()));return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"Dados inválidos",errors));}
}
