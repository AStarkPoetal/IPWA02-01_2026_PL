package de.require4testing.bean;

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
public class TestBean implements Serializable {

    private String name;
    private String status = "open";
    private Integer selectedTestId;
    private Integer selectedTestCaseId;

    private int nextId = 1;

    private final List<Test> tests = new ArrayList<>();

    @Inject
    private LoginBean loginBean;

    @Inject
    private TestCaseBean testCaseBean;

    public void createTest() {
        Test test = new Test(nextId++, name, status, loginBean.getCurrentUser());
        tests.add(test);

        name = "";
        status = "open";
    }

    public void updateStatus(Test test, String newStatus) {
        if (test != null) {
            test.setStatus(newStatus);
        }
    }

    public void assignSelectedTestCase() {
        Test selectedTest = findTestById(selectedTestId);
        TestCase selectedTestCase = findTestCaseById(selectedTestCaseId);

        if (selectedTest != null && selectedTestCase != null && !selectedTest.getTestCases().contains(selectedTestCase)) {
            selectedTest.getTestCases().add(selectedTestCase);
            selectedTestCase.setTest(selectedTest);
        }
    }

    public List<TestCase> getAvailableTestCases() {
        return testCaseBean.getTestCases();
    }

    private Test findTestById(Integer testId) {
        if (testId == null) {
            return null;
        }

        for (Test test : tests) {
            if (test.getId() == testId) {
                return test;
            }
        }

        return null;
    }

    private TestCase findTestCaseById(Integer testCaseId) {
        if (testCaseId == null) {
            return null;
        }

        for (TestCase testCase : testCaseBean.getTestCases()) {
            if (testCase.getId() == testCaseId) {
                return testCase;
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
        return tests;
    }
}
