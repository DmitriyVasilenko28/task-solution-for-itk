package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.NewTaskRequest;
import org.example.dto.TaskResponse;
import org.example.model.task.TaskStatus;
import org.example.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    public Page<TaskResponse> getAll(@RequestParam int page,
                             @RequestParam int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public TaskResponse create(@RequestBody @Valid NewTaskRequest task) {
        return service.create(task);
    }

    @PatchMapping("/{id}/assignee/{userId}")
    public TaskResponse assign(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        return service.assign(id, userId);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse changeStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status) {
        return service.changeStatus(id, status);
    }
}
