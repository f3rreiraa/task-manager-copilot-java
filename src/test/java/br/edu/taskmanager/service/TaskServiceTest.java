package br.edu.taskmanager.service;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import br.edu.taskmanager.dto.TaskRequest; import br.edu.taskmanager.entity.*; import br.edu.taskmanager.exception.TaskNotFoundException; import br.edu.taskmanager.repository.TaskRepository;
import java.util.Optional; import org.junit.jupiter.api.*; import org.mockito.*;
class TaskServiceTest {
 @Mock TaskRepository repository; @InjectMocks TaskService service;
 @BeforeEach void setup(){MockitoAnnotations.openMocks(this);}
 @Test void shouldCreateTaskWhenInputIsValid(){Task task=new Task("Estudar Copilot","Laboratório",TaskStatus.PENDING,TaskPriority.MEDIUM);when(repository.save(any(Task.class))).thenReturn(task);var result=service.create(new TaskRequest("Estudar Copilot","Laboratório",TaskStatus.PENDING,TaskPriority.MEDIUM));assertEquals("Estudar Copilot",result.title());verify(repository).save(any(Task.class));}
 @Test void shouldThrowWhenTaskDoesNotExist(){when(repository.findById(99L)).thenReturn(Optional.empty());assertThrows(TaskNotFoundException.class,()->service.findById(99L));}
 @Test void shouldDeleteExistingTask(){Task task=new Task("Tarefa válida","Descrição",TaskStatus.PENDING,TaskPriority.LOW);when(repository.findById(1L)).thenReturn(Optional.of(task));service.delete(1L);verify(repository).delete(task);}
 @Test void shouldUpdateExistingTask(){Task task=new Task("Título antigo","Descrição",TaskStatus.PENDING,TaskPriority.LOW);when(repository.findById(1L)).thenReturn(Optional.of(task));var result=service.update(1L,new TaskRequest("Título novo","Nova descrição",TaskStatus.DONE,TaskPriority.LOW));assertEquals(TaskStatus.DONE,result.status());assertEquals("Título novo",result.title());}

 // testes da prioridade

 @Test
 void shouldSavePriorityWhenCreatingTask() {
     // Arrange
     when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
     var request = new TaskRequest("Fazer relatório", "Entregar sexta", TaskStatus.PENDING, TaskPriority.HIGH);

     // Act
     var result = service.create(request);

     // Assert
     ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
     verify(repository).save(captor.capture());
     assertEquals(TaskPriority.HIGH, captor.getValue().getPriority());
     assertEquals(TaskPriority.HIGH, result.priority());
 }

 @Test
 void shouldChangePriorityWhenUpdatingTask() {
     // Arrange
     Task task = new Task("Estudar JPA", "Capítulo 3", TaskStatus.IN_PROGRESS, TaskPriority.LOW);
     when(repository.findById(5L)).thenReturn(Optional.of(task));

     // Act
     var result = service.update(5L, new TaskRequest("Estudar JPA", "Capítulo 3", TaskStatus.IN_PROGRESS, TaskPriority.HIGH));

     // Assert
     assertEquals(TaskPriority.HIGH, result.priority());
     assertEquals(TaskPriority.HIGH, task.getPriority());
 }

 @Test
 void shouldReturnPriorityWhenFindingById() {
     // Arrange
     Task task = new Task("Ler artigo", null, TaskStatus.DONE, TaskPriority.MEDIUM);
     when(repository.findById(2L)).thenReturn(Optional.of(task));

     // Act
     var result = service.findById(2L);

     // Assert
     assertEquals(TaskPriority.MEDIUM, result.priority());
     assertEquals(TaskStatus.DONE, result.status());
 }

 @Test
 void shouldThrowWhenUpdatingTaskThatDoesNotExist() {
     // Arrange
     when(repository.findById(42L)).thenReturn(Optional.empty());
     var request = new TaskRequest("Qualquer coisa", null, TaskStatus.PENDING, TaskPriority.LOW);

     // Act + Assert
     assertThrows(TaskNotFoundException.class, () -> service.update(42L, request));
     verify(repository, never()).save(any());
 }
}
