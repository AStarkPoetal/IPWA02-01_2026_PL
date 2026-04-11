package de.require4testing.bean;

import de.require4testing.model.Task;
import de.require4testing.service.TaskService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class TaskBean implements Serializable {

    private final TaskService taskService = new TaskService();

    private String name;
    private String description;
    private Integer editingTaskId;

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

        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus("in_progress");
        task.setUser(loginBean.getCurrentUser());

        if (editingTaskId != null) {
            task.setId(editingTaskId);
            Task existingTask = getTasks().stream()
                    .filter(currentTask -> currentTask.getId() == editingTaskId)
                    .findFirst()
                    .orElse(null);
            task.setStatus(existingTask != null ? existingTask.getStatus() : "in_progress");
            task.setUser(existingTask != null ? existingTask.getUser() : loginBean.getCurrentUser());
            taskService.update(task);
            addInfoMessage("Task updated successfully.");
        } else {
            taskService.create(task);
            addInfoMessage("Task created successfully.");
        }

        name = "";
        description = "";
        editingTaskId = null;
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

    public void markAsDone(Task task) {
        if (task == null) {
            return;
        }

        taskService.updateStatus(task.getId(), "done");
        addInfoMessage("Task marked as done.");
    }

    public void deleteTask(Task task) {
        if (task == null) {
            return;
        }

        if (taskService.delete(task.getId())) {
            addInfoMessage("Task deleted successfully.");
            return;
        }

        addErrorMessage("Task could not be deleted.");
    }

    public void editTask(Task task) {
        if (task == null) {
            return;
        }

        editingTaskId = task.getId();
        name = task.getName();
        description = task.getDescription();
    }

    public void cancelEdit() {
        editingTaskId = null;
        name = "";
        description = "";
    }

    public List<Task> getTasks() {
        return taskService.findAll();
    }

    public boolean isEditing() {
        return editingTaskId != null;
    }
}
