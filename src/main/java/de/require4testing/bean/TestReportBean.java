package de.require4testing.bean;

import de.require4testing.model.Test;
import de.require4testing.model.TestReport;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class TestReportBean implements Serializable {

    private String name;
    private String status = "failed";
    private Integer selectedTestId;

    private int nextId = 1;

    private final List<TestReport> testReports = new ArrayList<>();

    @Inject
    private LoginBean loginBean;

    @Inject
    private TestBean testBean;

    public void createTestReport() {
        if (name == null || name.isBlank()) {
            addErrorMessage("Test report name is required.");
            return;
        }

        Test selectedTest = findTestById(selectedTestId);
        if (selectedTest == null) {
            addErrorMessage("A test must be selected.");
            return;
        }

        TestReport testReport = new TestReport(nextId++, name, status, selectedTest, loginBean.getCurrentUser());
        testReports.add(testReport);

        name = "";
        status = "failed";
        selectedTestId = null;

        addInfoMessage("TestReport created successfully.");
    }

    public void updateStatus(TestReport testReport, String newStatus) {
        if (testReport != null) {
            testReport.setStatus(newStatus);
        }
    }

    public List<Test> getAvailableTests() {
        return testBean.getTests();
    }

    private Test findTestById(Integer testId) {
        if (testId == null) {
            return null;
        }

        for (Test test : testBean.getTests()) {
            if (test.getId() == testId) {
                return test;
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

    public List<TestReport> getTestReports() {
        return testReports;
    }
}
