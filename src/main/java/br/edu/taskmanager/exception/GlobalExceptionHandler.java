package br.edu.taskmanager.exception;
import java.time.Instant; import java.util.LinkedHashMap; import java.util.Map;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.*; import org.springframework.http.converter.HttpMessageNotReadableException; import org.springframework.web.ErrorResponse; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class GlobalExceptionHandler {
 private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
 @ExceptionHandler(TaskNotFoundException.class)
 ResponseEntity<ApiError> notFound(TaskNotFoundException ex){return ResponseEntity.status(404).body(new ApiError(Instant.now(),404,ex.getMessage(),Map.of()));}
 @ExceptionHandler(MethodArgumentNotValidException.class)
 ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex){Map<String,String> errors=new LinkedHashMap<>();ex.getBindingResult().getFieldErrors().forEach(e->errors.putIfAbsent(e.getField(),e.getDefaultMessage()));return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"Dados inválidos",errors));}

 // JSON quebrado ou valor de enum que nao existe (ex: priority "URGENTE")
 @ExceptionHandler(HttpMessageNotReadableException.class)
 ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException ex) {
     return error(HttpStatus.BAD_REQUEST, "JSON inválido ou campo com valor não permitido (status: PENDING, IN_PROGRESS, DONE; prioridade: LOW, MEDIUM, HIGH)");
 }

 // parametro com tipo errado, ex: /api/tasks?status=XYZ ou /api/tasks/abc
 @ExceptionHandler(MethodArgumentTypeMismatchException.class)
 ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException ex) {
     return error(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + ex.getName() + "'");
 }

 // qualquer outro erro: nao mostra stack trace pro front
 @ExceptionHandler(Exception.class)
 ResponseEntity<ApiError> generic(Exception ex) {
     if (ex instanceof ErrorResponse springError) {
         // erros do proprio spring (404 de rota, 405 de metodo...) mantem o status certo
         HttpStatusCode status = springError.getStatusCode();
         return error(status, "Requisição não pôde ser atendida");
     }
     log.error("Erro inesperado", ex);
     return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
 }

 private ResponseEntity<ApiError> error(HttpStatusCode status, String message) {
     return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), message, Map.of()));
 }
}
