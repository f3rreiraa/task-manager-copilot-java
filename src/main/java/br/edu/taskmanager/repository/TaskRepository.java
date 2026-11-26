package br.edu.taskmanager.repository;
import br.edu.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TaskRepository extends JpaRepository<Task,Long> {}
