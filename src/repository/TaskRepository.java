package repository;

import model.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todo_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "0828";

    private Connection connection;

    public TaskRepository() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
    }

    // 작업 추가
    public void addTask(Task task) {
        String query = "INSERT INTO todos (title, due_date, priority, completed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getTitle());
            stmt.setDate(2, Date.valueOf(task.getDueDate()));
            stmt.setInt(3, task.getPriority());
            stmt.setBoolean(4, task.isCompleted());

            stmt.executeUpdate();

            // 자동 생성된 ID 가져오기
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 작업 삭제
    public void deleteTask(int taskId) {
        String query = "DELETE FROM todos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 모든 작업 조회
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM todos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getInt("priority"),
                        rs.getBoolean("completed")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // 작업 완료 상태 업데이트
    public void updateTaskCompletion(int taskId, boolean completed) {
        String query = "UPDATE todos SET completed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, completed);
            stmt.setInt(2, taskId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 작업 업데이트 (전체 필드)
    public void updateTask(Task updatedTask) {
        String query = "UPDATE todos SET title = ?, due_date = ?, priority = ?, completed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, updatedTask.getTitle());
            stmt.setDate(2, Date.valueOf(updatedTask.getDueDate()));
            stmt.setInt(3, updatedTask.getPriority());
            stmt.setBoolean(4, updatedTask.isCompleted());
            stmt.setInt(5, updatedTask.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
