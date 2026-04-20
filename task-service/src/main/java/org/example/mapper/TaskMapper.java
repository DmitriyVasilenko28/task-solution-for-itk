package org.example.mapper;

import org.example.dto.NewTaskRequest;
import org.example.dto.TaskResponse;
import org.example.model.task.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task mapNewTaskRequestToTask(NewTaskRequest newTaskRequest);

    TaskResponse mapTaskToTaskResponse(Task task);
}
