package de.require4testing.bean;

import de.require4testing.model.Requirement;
import de.require4testing.service.RequirementService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class RequirementBean implements Serializable {

    private final RequirementService requirementService = new RequirementService();

    private String name;
    private String priority;
    private String status = "new";

    public void createRequirement() {
        if (name == null || name.isBlank()) {
            addErrorMessage("Requirement name is required.");
            return;
        }

        if (priority == null || priority.isBlank()) {
            addErrorMessage("Requirement priority is required.");
            return;
        }

        Requirement requirement = new Requirement();
        requirement.setName(name);
        requirement.setPriority(priority);
        requirement.setStatus(status);

        requirementService.create(requirement);

        name = "";
        priority = "";
        status = "new";

        addInfoMessage("Requirement created successfully.");
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Requirement> getRequirements() {
        return requirementService.findAll();
    }
}
