package de.require4testing.bean;

import de.require4testing.model.Task;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class TaskBean implements Serializable {

    private String name;
    private String description;
    private String status = "open";

    private int nextId = 1;

    private final List<Task> tasks = new ArrayList<>();

    @Inject
    private LoginBean loginBean;

    public void createTask() {
        Task task = new Task(nextId++, name, description, status, loginBean.getCurrentUser());
        tasks.add(task);

        name = "";
        description = "";
        status = "open";
    }

    public void updateStatus(Task task, String newStatus) {
        if (task != null) {
            task.setStatus(newStatus);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
