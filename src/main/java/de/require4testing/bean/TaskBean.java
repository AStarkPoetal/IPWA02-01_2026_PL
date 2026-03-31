package de.require4testing.bean;

import de.require4testing.model.Task;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
        if (name == null || name.isBlank()) {
            addErrorMessage("Task name is required.");
            return;
        }

        if (description == null || description.isBlank()) {
            addErrorMessage("Task description is required.");
            return;
        }

        Task task = new Task(nextId++, name, description, status, loginBean.getCurrentUser());
        tasks.add(task);

        name = "";
        description = "";
        status = "open";

        addInfoMessage("Task created successfully.");
    }

    public void updateStatus(Task task, String newStatus) {
        if (task != null) {
            task.setStatus(newStatus);
        }
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
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
