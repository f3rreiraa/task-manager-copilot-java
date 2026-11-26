package br.edu.taskmanager.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.edu.taskmanager.entity.Task;
import br.edu.taskmanager.entity.TaskPriority;
import br.edu.taskmanager.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// testa as consultas de verdade no H2, pra ter certeza que o ignore case funciona
@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    TaskRepository repository;

    @BeforeEach
    void setup() {
        repository.save(new Task("Estudar Copilot", "lab", TaskStatus.PENDING, TaskPriority.HIGH));
        repository.save(new Task("Revisar COPILOT chat", null, TaskStatus.DONE, TaskPriority.LOW));
        repository.save(new Task("Fazer compras", null, TaskStatus.PENDING, TaskPriority.MEDIUM));
    }

    @Test
    void shouldFindByTitleIgnoringCase() {
        var result = repository.findByTitleContainingIgnoreCase("copilot");
        assertEquals(2, result.size());
    }

    @Test
    void shouldFindByStatus() {
        var result = repository.findByStatus(TaskStatus.PENDING);
        assertEquals(2, result.size());
    }

    @Test
    void shouldFindByTitleAndStatus() {
        var result = repository.findByTitleContainingIgnoreCaseAndStatus("copilot", TaskStatus.PENDING);
        assertEquals(1, result.size());
        assertEquals("Estudar Copilot", result.get(0).getTitle());
    }
}
