package br.edu.taskmanager.exception;
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id){super("Tarefa não encontrada: " + id);}
}
