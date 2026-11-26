package br.edu.taskmanager.service;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import br.edu.taskmanager.dto.TaskRequest; import br.edu.taskmanager.entity.*; import br.edu.taskmanager.exception.TaskNotFoundException; import br.edu.taskmanager.repository.TaskRepository;
import java.util.Optional; import org.junit.jupiter.api.*; import org.mockito.*;
class TaskServiceTest {
 @Mock TaskRepository repository; @InjectMocks TaskService service;
 @BeforeEach void setup(){MockitoAnnotations.openMocks(this);}
 @Test void shouldCreateTaskWhenInputIsValid(){Task task=new Task("Estudar Copilot","Laboratório",TaskStatus.PENDING);when(repository.save(any(Task.class))).thenReturn(task);var result=service.create(new TaskRequest("Estudar Copilot","Laboratório",TaskStatus.PENDING));assertEquals("Estudar Copilot",result.title());verify(repository).save(any(Task.class));}
 @Test void shouldThrowWhenTaskDoesNotExist(){when(repository.findById(99L)).thenReturn(Optional.empty());assertThrows(TaskNotFoundException.class,()->service.findById(99L));}
 @Test void shouldDeleteExistingTask(){Task task=new Task("Tarefa válida","Descrição",TaskStatus.PENDING);when(repository.findById(1L)).thenReturn(Optional.of(task));service.delete(1L);verify(repository).delete(task);}
 @Test void shouldUpdateExistingTask(){Task task=new Task("Título antigo","Descrição",TaskStatus.PENDING);when(repository.findById(1L)).thenReturn(Optional.of(task));var result=service.update(1L,new TaskRequest("Título novo","Nova descrição",TaskStatus.DONE));assertEquals(TaskStatus.DONE,result.status());assertEquals("Título novo",result.title());}
}
