package org.example.service;

import org.example.dto.NewTaskRequest;
import org.example.dto.TaskResponse;
import org.example.model.task.TaskStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TaskService {

    Page<TaskResponse> getAll(int page, int size);

    TaskResponse getById(UUID id);

    TaskResponse create(NewTaskRequest task);

    TaskResponse assign(UUID id, UUID userId);

    TaskResponse changeStatus(UUID id, TaskStatus status);
}
