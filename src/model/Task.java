package model;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private LocalDate dueDate;
    private int priority;
    private boolean completed;

    // 생성자에서 description 제거
    public Task(int id, String title, LocalDate dueDate, int priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    public Task(String title, LocalDate dueDate, int priority) {
        this(0, title, dueDate, priority, false);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
