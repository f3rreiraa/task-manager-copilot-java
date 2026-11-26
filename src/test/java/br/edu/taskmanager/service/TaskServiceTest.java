package br.edu.taskmanager.service;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import br.edu.taskmanager.dto.TaskRequest; import br.edu.taskmanager.entity.*; import br.edu.taskmanager.exception.TaskNotFoundException; import br.edu.taskmanager.repository.TaskRepository;
import java.util.List; import java.util.Optional; import org.junit.jupiter.api.*; import org.mockito.*;
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

 // testes do filtro (desafio final)

 @Test
 void shouldReturnAllTasksWhenNoFilterIsGiven() {
     // Arrange
     when(repository.findAll()).thenReturn(List.of(
         new Task("Estudar Copilot", null, TaskStatus.PENDING, TaskPriority.LOW),
         new Task("Lavar louça", null, TaskStatus.DONE, TaskPriority.LOW)));

     // Act
     var result = service.findAll(null, null);

     // Assert
     assertEquals(2, result.size());
     verify(repository).findAll();
 }

 @Test
 void shouldFilterOnlyByTitleIgnoringCase() {
     // Arrange
     when(repository.findByTitleContainingIgnoreCase("copilot")).thenReturn(List.of(
         new Task("Estudar Copilot", null, TaskStatus.PENDING, TaskPriority.HIGH)));

     // Act
     var result = service.findAll("copilot", null);

     // Assert
     assertEquals(1, result.size());
     assertEquals("Estudar Copilot", result.get(0).title());
     verify(repository).findByTitleContainingIgnoreCase("copilot");
     verify(repository, never()).findAll();
 }

 @Test
 void shouldFilterOnlyByStatus() {
     // Arrange
     when(repository.findByStatus(TaskStatus.DONE)).thenReturn(List.of(
         new Task("Lavar louça", null, TaskStatus.DONE, TaskPriority.LOW)));

     // Act
     var result = service.findAll(null, TaskStatus.DONE);

     // Assert
     assertEquals(1, result.size());
     assertEquals(TaskStatus.DONE, result.get(0).status());
     verify(repository).findByStatus(TaskStatus.DONE);
 }

 @Test
 void shouldFilterByTitleAndStatusTogether() {
     // Arrange
     when(repository.findByTitleContainingIgnoreCaseAndStatus("copilot", TaskStatus.PENDING)).thenReturn(List.of(
         new Task("Estudar Copilot", null, TaskStatus.PENDING, TaskPriority.MEDIUM)));

     // Act
     var result = service.findAll("copilot", TaskStatus.PENDING);

     // Assert
     assertEquals(1, result.size());
     verify(repository).findByTitleContainingIgnoreCaseAndStatus("copilot", TaskStatus.PENDING);
     verify(repository, never()).findByStatus(any());
 }

 @Test
 void shouldIgnoreBlankTitleAndTrimSpaces() {
     // Arrange
     when(repository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of());
     when(repository.findByTitleContainingIgnoreCase("java")).thenReturn(List.of());

     // Act
     service.findAll("   ", TaskStatus.PENDING);
     service.findAll("  java ", null);

     // Assert
     verify(repository).findByStatus(TaskStatus.PENDING);
     verify(repository).findByTitleContainingIgnoreCase("java");
 }
}
