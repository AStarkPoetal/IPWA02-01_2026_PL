package de.require4testing.bean;

import de.require4testing.model.Requirement;
import de.require4testing.model.Test;
import de.require4testing.model.TestCase;
import de.require4testing.service.TestCaseService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class TestCaseBean implements Serializable {

    private final TestCaseService testCaseService = new TestCaseService();

    private String name;
    private String description;
    private String expectedResult;
    private Integer selectedRequirementId;
    private Integer editingTestCaseId;

    @Inject
    private RequirementBean requirementBean;

    public void createTestCase() {
        if (name == null || name.isBlank()) {
            addErrorMessage("TestCase name is required.");
            return;
        }

        if (description == null || description.isBlank()) {
            addErrorMessage("TestCase description is required.");
            return;
        }

        if (expectedResult == null || expectedResult.isBlank()) {
            addErrorMessage("Expected result is required.");
            return;
        }

        Requirement selectedRequirement = findRequirementById(selectedRequirementId);
        if (selectedRequirement == null) {
            addErrorMessage("A requirement must be selected.");
            return;
        }

        Test selectedTest = null;
        TestCase testCase = new TestCase();
        testCase.setName(name);
        testCase.setDescription(description);
        testCase.setExpectedResult(expectedResult);
        testCase.setRequirement(selectedRequirement);
        testCase.setTest(selectedTest);

        if (editingTestCaseId != null) {
            testCase.setId(editingTestCaseId);
            testCaseService.update(testCase);
            addInfoMessage("TestCase updated successfully.");
        } else {
            testCaseService.create(testCase);
            addInfoMessage("TestCase created successfully.");
        }

        name = "";
        description = "";
        expectedResult = "";
        selectedRequirementId = null;
        editingTestCaseId = null;
    }

    public void editTestCase(TestCase testCase) {
        if (testCase == null) {
            return;
        }

        editingTestCaseId = testCase.getId();
        name = testCase.getName();
        description = testCase.getDescription();
        expectedResult = testCase.getExpectedResult();
        selectedRequirementId = testCase.getRequirement() != null ? testCase.getRequirement().getId() : null;
    }

    public void cancelEdit() {
        editingTestCaseId = null;
        name = "";
        description = "";
        expectedResult = "";
        selectedRequirementId = null;
    }

    public void deleteTestCase(TestCase testCase) {
        if (testCase == null) {
            return;
        }

        if (testCaseService.delete(testCase.getId())) {
            addInfoMessage("TestCase deleted successfully.");
            return;
        }

        addErrorMessage("TestCase could not be deleted.");
    }

    public List<Requirement> getAvailableRequirements() {
        return requirementBean.getRequirements();
    }

    private Requirement findRequirementById(Integer requirementId) {
        if (requirementId == null) {
            return null;
        }

        for (Requirement requirement : requirementBean.getRequirements()) {
            if (requirement.getId() == requirementId) {
                return requirement;
            }
        }

        return null;
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

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Integer getSelectedRequirementId() {
        return selectedRequirementId;
    }

    public void setSelectedRequirementId(Integer selectedRequirementId) {
        this.selectedRequirementId = selectedRequirementId;
    }

    public List<TestCase> getTestCases() {
        return testCaseService.findAll();
    }

    public boolean isEditing() {
        return editingTestCaseId != null;
    }
}
