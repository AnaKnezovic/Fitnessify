package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/camunda-tasks")
public class CamundaTaskController {

    @Autowired
    private TaskService taskService;

    // DTO za task
    public static class TaskDto {
        public String id;
        public String name;
        public String processInstanceId;
        public String klijentId; 

        public TaskDto(Task task, TaskService taskService) {
            this.id = task.getId();
            this.name = task.getName();
            this.processInstanceId = task.getProcessInstanceId();
            Object klijentIdVar = taskService.getVariable(task.getExecutionId(), "klijentId");
            this.klijentId = klijentIdVar != null ? klijentIdVar.toString() : null;
        }
    }

    @GetMapping("/for-trainer")
    public List<TaskDto> getTasksForTrainer(@RequestParam String trenerId) {
        List<Task> tasks = taskService.createTaskQuery()
                        .processVariableValueEquals("trenerId", trenerId)
                        .active()
                        .list();

        return tasks.stream().map(task -> new TaskDto(task, taskService)).collect(Collectors.toList());
    }

    // Kompletiraj task (npr. kad trener prihvati ili odbije)
    @PostMapping("/{taskId}/complete")
    public void completeTask(@PathVariable String taskId, @RequestBody Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
}
