package br.edu.taskmanager.dto;
import br.edu.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.*;
public record TaskRequest(
    @NotBlank @Size(min=3,max=100) String title,
    @Size(max=500) String description,
    @NotNull TaskStatus status) {}
