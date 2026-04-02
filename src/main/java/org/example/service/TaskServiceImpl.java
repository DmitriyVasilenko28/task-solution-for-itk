package org.example.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NewTaskRequest;
import org.example.dto.TaskResponse;
import org.example.exception.NotFoundException;
import org.example.mapper.TaskMapper;
import org.example.model.task.Task;
import org.example.model.task.TaskStatus;
import org.example.model.user.User;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskMapper taskMapper;
    UserRepository userRepository;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAll(int page, int size) {
        log.info("Получаем данные всех задач. Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(taskMapper::mapTaskToTaskResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(UUID id) {
        log.info("Получаем данные задачи по ее id");
        return taskMapper.mapTaskToTaskResponse(getTaskById(id));
    }

    @Override
    @Transactional
    public TaskResponse create(NewTaskRequest task) {
        log.info("Проверяем лимит задач.");
        long count = taskRepository.count();
        if (count >= 10_000) {
            throw new IllegalStateException("Достигнут лимит пользователей");
        }

        log.info("Создаем новую задачу");
        Task newTask = taskMapper.mapNewTaskRequestToTask(task);
        newTask.setStatus(TaskStatus.CREATED);
        Task createdTask = taskRepository.save(newTask);

        kafkaTemplate.send("task-created", createdTask);

        return taskMapper.mapTaskToTaskResponse(createdTask);
    }

    @Override
    @Transactional
    public TaskResponse assign(UUID id, UUID userId) {
        Task task = getTaskById(id);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Сотрудник не найден, UUID: " + userId));
        task.setUser(user);

        Task saved = taskRepository.save(task);

        kafkaTemplate.send("task-assigned", saved);

        return taskMapper.mapTaskToTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse changeStatus(UUID id, TaskStatus status) {
        Task task = getTaskById(id);
        task.setStatus(status);
        return taskMapper.mapTaskToTaskResponse(task);
    }

    private Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Задача не найдена, UUID: " + id));
    }
}
