package br.edu.taskmanager.dto;
import br.edu.taskmanager.entity.TaskStatus;
public record TaskResponse(Long id,String title,String description,TaskStatus status) {}
