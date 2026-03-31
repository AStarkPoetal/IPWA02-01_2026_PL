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
    private String status = "open";
    private Integer selectedTestId;
    private Integer selectedTestCaseId;

    @Inject
    private LoginBean loginBean;

    public void createTest() {
        if (name == null || name.isBlank()) {
            addErrorMessage("Test name is required.");
            return;
        }

        Test test = new Test();
        test.setName(name);
        test.setStatus(status);
        test.setCreatedBy(loginBean.getCurrentUser());

        testService.create(test);

        name = "";
        status = "open";

        addInfoMessage("Test created successfully.");
    }

    public void updateStatus(Test test, String newStatus) {
        if (test != null) {
            test.setStatus(newStatus);
        }
    }

    public void assignSelectedTestCase() {
        Test selectedTest = findTestById(selectedTestId);
        TestCase selectedTestCase = findTestCaseById(selectedTestCaseId);

        if (selectedTest == null) {
            addErrorMessage("A test must be selected.");
            return;
        }

        if (selectedTestCase == null) {
            addErrorMessage("A test case must be selected.");
            return;
        }

        if (!selectedTest.getTestCases().contains(selectedTestCase)) {
            selectedTestCase.setTest(selectedTest);
            testCaseService.update(selectedTestCase);
            addInfoMessage("TestCase assigned successfully.");
            return;
        }

        addErrorMessage("This TestCase is already assigned to the selected Test.");
    }

    public List<TestCase> getAvailableTestCases() {
        return testCaseService.findAll();
    }

    private Test findTestById(Integer testId) {
        return testService.findById(testId);
    }

    private TestCase findTestCaseById(Integer testCaseId) {
        return testCaseService.findById(testCaseId);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
