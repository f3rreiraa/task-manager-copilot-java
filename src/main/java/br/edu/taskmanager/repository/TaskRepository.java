package br.edu.taskmanager.repository;
import br.edu.taskmanager.entity.Task;
import br.edu.taskmanager.entity.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTitleContainingIgnoreCase(String title);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByTitleContainingIgnoreCaseAndStatus(String title, TaskStatus status);
}
