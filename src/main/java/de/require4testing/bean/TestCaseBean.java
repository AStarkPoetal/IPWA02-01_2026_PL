package de.require4testing.bean;

import de.require4testing.model.Requirement;
import de.require4testing.model.Test;
import de.require4testing.model.TestCase;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class TestCaseBean implements Serializable {

    private String name;
    private String description;
    private String expectedResult;
    private Integer selectedRequirementId;
    private Integer selectedTestId;

    private int nextId = 1;

    private final List<TestCase> testCases = new ArrayList<>();

    @Inject
    private RequirementBean requirementBean;

    public void createTestCase() {
        Requirement selectedRequirement = findRequirementById(selectedRequirementId);
        Test selectedTest = null;
        TestCase testCase = new TestCase(nextId++, name, description, expectedResult, selectedRequirement, selectedTest);
        testCases.add(testCase);

        name = "";
        description = "";
        expectedResult = "";
        selectedRequirementId = null;
        selectedTestId = null;
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

    public Integer getSelectedTestId() {
        return selectedTestId;
    }

    public void setSelectedTestId(Integer selectedTestId) {
        this.selectedTestId = selectedTestId;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }
}
