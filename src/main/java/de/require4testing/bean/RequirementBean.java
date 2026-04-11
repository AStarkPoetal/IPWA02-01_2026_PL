package de.require4testing.bean;

import de.require4testing.model.Requirement;
import de.require4testing.service.RequirementService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
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
    private Integer editingRequirementId;

    @Inject
    private LoginBean loginBean;

    public void createRequirement() {
        if (!loginBean.canAccessRequirement()) {
            addErrorMessage("You are not allowed to manage requirements.");
            return;
        }

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

        if (editingRequirementId != null) {
            requirement.setId(editingRequirementId);
            requirementService.update(requirement);
            addInfoMessage("Requirement updated successfully.");
        } else {
            requirementService.create(requirement);
            addInfoMessage("Requirement created successfully.");
        }

        name = "";
        priority = "";
        status = "new";
        editingRequirementId = null;
    }

    public void editRequirement(Requirement requirement) {
        if (!loginBean.canAccessRequirement()) {
            addErrorMessage("You are not allowed to manage requirements.");
            return;
        }

        if (requirement == null) {
            return;
        }

        editingRequirementId = requirement.getId();
        name = requirement.getName();
        priority = requirement.getPriority();
        status = requirement.getStatus();
    }

    public void cancelEdit() {
        editingRequirementId = null;
        name = "";
        priority = "";
        status = "new";
    }

    public void deleteRequirement(Requirement requirement) {
        if (!loginBean.canAccessRequirement()) {
            addErrorMessage("You are not allowed to manage requirements.");
            return;
        }

        if (requirement == null) {
            return;
        }

        if (requirementService.delete(requirement.getId())) {
            addInfoMessage("Requirement deleted successfully.");
            return;
        }

        addErrorMessage("Requirement could not be deleted. It may still be referenced by a TestCase.");
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

    public boolean isEditing() {
        return editingRequirementId != null;
    }
}
