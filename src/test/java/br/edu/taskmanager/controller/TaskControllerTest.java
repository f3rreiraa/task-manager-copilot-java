package br.edu.taskmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.edu.taskmanager.dto.TaskResponse;
import br.edu.taskmanager.entity.TaskPriority;
import br.edu.taskmanager.entity.TaskStatus;
import br.edu.taskmanager.exception.TaskNotFoundException;
import br.edu.taskmanager.service.TaskService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TaskService service;

    @Test
    void shouldPassFiltersToService() throws Exception {
        when(service.findAll("copilot", TaskStatus.PENDING)).thenReturn(List.of(
            new TaskResponse(1L, "Estudar Copilot", null, TaskStatus.PENDING, TaskPriority.HIGH)));

        mvc.perform(get("/api/tasks").param("title", "copilot").param("status", "PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Estudar Copilot"))
            .andExpect(jsonPath("$[0].priority").value("HIGH"));

        verify(service).findAll("copilot", TaskStatus.PENDING);
    }

    @Test
    void shouldReturn400WhenStatusFilterIsInvalid() throws Exception {
        mvc.perform(get("/api/tasks").param("status", "XYZ"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn201WhenTaskIsCreated() throws Exception {
        when(service.create(any())).thenReturn(new TaskResponse(7L, "Nova tarefa", null, TaskStatus.PENDING, TaskPriority.LOW));

        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Nova tarefa\",\"status\":\"PENDING\",\"priority\":\"LOW\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/tasks/7"));
    }

    @Test
    void shouldReturn400WhenTitleIsTooShort() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"ab\",\"status\":\"PENDING\",\"priority\":\"LOW\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.title").value("Título deve ter entre 3 e 100 caracteres"));
        verify(service, never()).create(any());
    }

    @Test
    void shouldReturn400WhenPriorityIsMissing() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Sem prioridade\",\"status\":\"PENDING\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.priority").exists());
    }

    @Test
    void shouldReturn400WhenPriorityValueDoesNotExist() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Prioridade errada\",\"status\":\"PENDING\",\"priority\":\"URGENTE\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        mvc.perform(get("/api/tasks/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Tarefa não encontrada: 99"));
    }
}
