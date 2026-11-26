package br.edu.taskmanager.dto;
import br.edu.taskmanager.entity.TaskPriority;
import br.edu.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.*;
public record TaskRequest(
    @NotBlank(message = "Título é obrigatório") @Size(min=3,max=100,message = "Título deve ter entre 3 e 100 caracteres") String title,
    @Size(max=500,message = "Descrição pode ter no máximo 500 caracteres") String description,
    @NotNull(message = "Status é obrigatório") TaskStatus status,
    @NotNull(message = "Prioridade é obrigatória") TaskPriority priority) {}
