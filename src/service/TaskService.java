package service;

import model.Task;
import repository.TaskRepository;

import java.util.List;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService() {
        this.taskRepository = new TaskRepository();
    }

    // 새로운 작업 추가
    public void addTask(Task task) {
        taskRepository.addTask(task);
    }

    // 작업 삭제
    public void deleteTask(int taskId) {
        taskRepository.deleteTask(taskId);
    }

    // 모든 작업 조회
    public List<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    // 작업 완료 상태 업데이트
    public void updateTaskCompletion(int taskId, boolean completed) {
        taskRepository.updateTaskCompletion(taskId, completed);
    }

    // 작업 수정 (제목, 기한, 우선순위 등 전체 업데이트)
    public void updateTask(Task task) {
        taskRepository.updateTask(task);
    }
}
