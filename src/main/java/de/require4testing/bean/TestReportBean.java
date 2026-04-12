package de.require4testing.bean;

import de.require4testing.model.Test;
import de.require4testing.model.TestReport;
import de.require4testing.service.TestReportService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class TestReportBean implements Serializable {

    private final TestReportService testReportService = new TestReportService();

    private String name;
    private String status = "failed";
    private Integer selectedTestId;

    @Inject
    private LoginBean loginBean;

    @Inject
    private TestBean testBean;

    public void createTestReport() {
        if (!loginBean.canAccessReport()) {
            addErrorMessage("You are not allowed to manage test reports.");
            return;
        }

        if (name == null || name.isBlank()) {
            addErrorMessage("Test report name is required.");
            return;
        }

        Test selectedTest = findTestById(selectedTestId);
        if (selectedTest == null) {
            addErrorMessage("A test must be selected.");
            return;
        }

        if (!loginBean.canCreateReportForTest(selectedTest)) {
            addErrorMessage("You are not allowed to create a report for this test.");
            return;
        }

        TestReport testReport = new TestReport();
        testReport.setName(name);
        testReport.setStatus(status);
        testReport.setTest(selectedTest);
        testReport.setUser(loginBean.getCurrentUser());

        testReportService.create(testReport);

        name = "";
        status = "failed";
        selectedTestId = null;

        addInfoMessage("TestReport created successfully.");
    }

    public void deleteTestReport(TestReport testReport) {
        if (!loginBean.canDeleteReport(testReport)) {
            addErrorMessage("You are not allowed to manage test reports.");
            return;
        }

        if (testReport == null) {
            return;
        }

        if (testReportService.delete(testReport.getId())) {
            addInfoMessage("TestReport deleted successfully.");
            return;
        }

        addErrorMessage("TestReport could not be deleted.");
    }

    public List<Test> getAvailableTests() {
        return testBean.getTests().stream()
                .filter(loginBean::canCreateReportForTest)
                .toList();
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
        return testReportService.findAll();
    }
}
