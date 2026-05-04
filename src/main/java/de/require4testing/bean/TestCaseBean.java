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
/**
 * Verwaltung von TestCase.
 *  Dieses Bean verwaltet die Testfälle zu den Requirements, also das Erstellen, Anzeigen, Bearbeiten und Löschen.
 */
public class TestCaseBean implements Serializable {

    private final TestCaseService testCaseService = new TestCaseService();

    private String name;
    private String description;
    private String expectedResult;
    private String testSteps;
    private Integer selectedRequirementId;
    private Integer editingTestCaseId;

    @Inject
    private RequirementBean requirementBean;

    @Inject
    private LoginBean loginBean;

    /**
     * Erstellung von TestCase oder update.
     * Auswahl von einem Requirement ist kein oprional, da jede TestFall zu einem Testcase gehört.
     */
    public void createTestCase() {
        if (!loginBean.canManageTestCase()) {
            addErrorMessage("You are not allowed to manage test cases.");
            return;
        }

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
        testCase.setTestSteps(testSteps);
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
        testSteps = "";
        selectedRequirementId = null;
        editingTestCaseId = null;
    }

    public void editTestCase(TestCase testCase) {
        if (!loginBean.canManageTestCase()) {
            addErrorMessage("You are not allowed to manage test cases.");
            return;
        }

        if (testCase == null) {
            return;
        }

        // Die Felder des Datensatzes werden in das Bearbeitungsformular zurückgeladen.
        editingTestCaseId = testCase.getId();
        name = testCase.getName();
        description = testCase.getDescription();
        expectedResult = testCase.getExpectedResult();
        testSteps = testCase.getTestSteps();
        selectedRequirementId = testCase.getRequirement() != null ? testCase.getRequirement().getId() : null;
    }

    public void cancelEdit() {
        editingTestCaseId = null;
        name = "";
        description = "";
        expectedResult = "";
        testSteps = "";
        selectedRequirementId = null;
    }

    public void deleteTestCase(TestCase testCase) {
        if (!loginBean.canManageTestCase()) {
            addErrorMessage("You are not allowed to manage test cases.");
            return;
        }

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

    public String getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(String testSteps) {
        this.testSteps = testSteps;
    }

    public List<TestCase> getTestCases() {
        return testCaseService.findAll();
    }

    public List<TestCase> getAssignedTestCasesForCurrentTester() {
        // Der Tester sieht nur die Testfälle, die zu Tests gehören, bei denen er als zugewiesener Tester eingetragen ist.
        return getTestCases().stream()
                .filter(testCase -> testCase.getTest() != null)
                .filter(testCase -> testCase.getTest().getAssignedTester() != null)
                .filter(testCase -> loginBean.getCurrentUser() != null)
                .filter(testCase -> testCase.getTest().getAssignedTester().getId() == loginBean.getCurrentUser().getId())
                .toList();
    }

    public long getAssignedTestCaseCountForCurrentTester() {
        return getAssignedTestCasesForCurrentTester().size();
    }

    public boolean isEditing() {
        return editingTestCaseId != null;
    }
}
