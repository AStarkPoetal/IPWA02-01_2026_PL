package de.require4testing.bean;

import de.require4testing.model.Test;
import de.require4testing.model.TestCase;
import de.require4testing.service.TestCaseService;
import de.require4testing.service.TestService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class TestBean implements Serializable {

    private final TestService testService = new TestService();
    private final TestCaseService testCaseService = new TestCaseService();

    private String name;
    private Integer selectedTestId;
    private Integer selectedTestCaseId;

    @Inject
    private LoginBean loginBean;

    public void createTest() {
        if (!loginBean.canAccessTest()) {
            addErrorMessage("You are not allowed to manage tests.");
            return;
        }

        if (name == null || name.isBlank()) {
            addErrorMessage("Test name is required.");
            return;
        }

        Test test = new Test();
        test.setName(name);
        test.setStatus("open");
        test.setCreatedBy(loginBean.getCurrentUser());

        testService.create(test);
        addInfoMessage("Test created successfully.");

        name = "";
    }

    public void assignSelectedTestCase() {
        if (!loginBean.canAccessTest()) {
            addErrorMessage("You are not allowed to manage tests.");
            return;
        }

        if (selectedTestId == null) {
            addErrorMessage("A test must be selected.");
            return;
        }

        if (selectedTestCaseId == null) {
            addErrorMessage("A test case must be selected.");
            return;
        }

        if (testCaseService.assignToTest(selectedTestCaseId, selectedTestId)) {
            selectedTestCaseId = null;
            addInfoMessage("TestCase assigned successfully.");
            return;
        }

        addErrorMessage("The selected TestCase could not be assigned to the selected Test.");
    }

    public void deleteTest(Test test) {
        if (!loginBean.canAccessTest()) {
            addErrorMessage("You are not allowed to manage tests.");
            return;
        }

        if (test == null) {
            return;
        }

        if (testService.delete(test.getId())) {
            addInfoMessage("Test deleted successfully.");
            return;
        }

        addErrorMessage("Test could not be deleted. It may still be referenced by a TestCase or TestReport.");
    }

    public List<TestCase> getAvailableTestCases() {
        return testCaseService.findAll();
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

    public Integer getSelectedTestId() {
        return selectedTestId;
    }

    public void setSelectedTestId(Integer selectedTestId) {
        this.selectedTestId = selectedTestId;
    }

    public Integer getSelectedTestCaseId() {
        return selectedTestCaseId;
    }

    public void setSelectedTestCaseId(Integer selectedTestCaseId) {
        this.selectedTestCaseId = selectedTestCaseId;
    }

    public List<Test> getTests() {
        return testService.findAll();
    }
}
