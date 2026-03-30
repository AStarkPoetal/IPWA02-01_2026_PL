package de.require4testing.bean;

import de.require4testing.model.Requirement;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class RequirementBean implements Serializable {

    private String name;
    private String priority;
    private String status = "new";

    private int nextId = 1;

    private final List<Requirement> requirements = new ArrayList<>();

    public void createRequirement() {
        Requirement requirement = new Requirement(nextId++, name, priority, status);
        requirements.add(requirement);

        name = "";
        priority = "";
        status = "new";
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
        return requirements;
    }
}
