package br.edu.taskmanager.service;
import br.edu.taskmanager.dto.*; import br.edu.taskmanager.entity.Task; import br.edu.taskmanager.entity.TaskStatus; import br.edu.taskmanager.exception.TaskNotFoundException; import br.edu.taskmanager.repository.TaskRepository;
import java.util.List; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service
public class TaskService {
 private final TaskRepository repository;
 public TaskService(TaskRepository repository){this.repository=repository;}

 // busca com filtros opcionais: se nao vier nada, lista tudo
 @Transactional(readOnly=true)
 public List<TaskResponse> findAll(String title, TaskStatus status) {
     boolean hasTitle = title != null && !title.isBlank();
     List<Task> tasks;
     if (hasTitle && status != null) {
         tasks = repository.findByTitleContainingIgnoreCaseAndStatus(title.trim(), status);
     } else if (hasTitle) {
         tasks = repository.findByTitleContainingIgnoreCase(title.trim());
     } else if (status != null) {
         tasks = repository.findByStatus(status);
     } else {
         tasks = repository.findAll();
     }
     return tasks.stream().map(this::toResponse).toList();
 }

 @Transactional(readOnly=true) public TaskResponse findById(Long id){return toResponse(findEntity(id));}
 @Transactional public TaskResponse create(TaskRequest request){return toResponse(repository.save(new Task(request.title(),request.description(),request.status(),request.priority())));}
 @Transactional public TaskResponse update(Long id,TaskRequest request){Task task=findEntity(id);task.update(request.title(),request.description(),request.status(),request.priority());return toResponse(task);}
 @Transactional public void delete(Long id){repository.delete(findEntity(id));}
 private Task findEntity(Long id){return repository.findById(id).orElseThrow(()->new TaskNotFoundException(id));}
 private TaskResponse toResponse(Task t){return new TaskResponse(t.getId(),t.getTitle(),t.getDescription(),t.getStatus(),t.getPriority());}
}
