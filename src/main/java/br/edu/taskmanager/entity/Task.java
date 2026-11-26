package br.edu.taskmanager.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "tasks")
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(length = 500)
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TaskStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TaskPriority priority;
    protected Task() {}
    public Task(String title, String description, TaskStatus status, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }
    public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public TaskStatus getStatus(){return status;}
    public TaskPriority getPriority(){return priority;}
    public void update(String title, String description, TaskStatus status, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }
}
