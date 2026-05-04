package de.require4testing.bean;

import de.require4testing.model.Test;
import de.require4testing.model.TestCase;
import de.require4testing.model.User;
import de.require4testing.service.TestCaseService;
import de.require4testing.service.TestService;
import de.require4testing.service.UserService;
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
 * Verwaltung von Testlauf Bean.
 * Hier erfolgen das Erstellen von Tests, die Zuweisung von Testfällen, die Auswahl des Testers sowie die Bereitstellung der Testliste.
 */
public class TestBean implements Serializable {

    private final TestService testService = new TestService();
    private final TestCaseService testCaseService = new TestCaseService();
    private final UserService userService = new UserService();

    private String name;
    private Integer selectedTestId;
    private Integer selectedTestCaseId;
    private Integer selectedTesterTestId;
    private Integer selectedTesterId;

    @Inject
    private LoginBean loginBean;

    /**
     * Erstellung von neu Test.
     * Initial Status ist immer "open".
     */
    public void createTest() {
        if (!loginBean.canCreateTest()) {
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
        // Der erstellende Benutzer wird gespeichert, da mehrere Berechtigungsregeln darauf basieren.
        test.setCreatedBy(loginBean.getCurrentUser());

        testService.create(test);
        addInfoMessage("Test created successfully.");

        name = "";
    }

    /**
     * Die UI übergibt zwei IDs (Test + Test Case); die eigentliche Aktualisierung der Beziehung wird vom Service durchgeführt.
     */
    public void assignSelectedTestCase() {
        if (selectedTestId == null) {
            addErrorMessage("A test must be selected.");
            return;
        }

        if (selectedTestCaseId == null) {
            addErrorMessage("A test case must be selected.");
            return;
        }

        Test selectedTest = findTestById(selectedTestId);
        if (!loginBean.canAssignTestCase(selectedTest)) {
            addErrorMessage("You are not allowed to assign test cases to this test.");
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
        if (!loginBean.canManageTest(test)) {
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

    /**
     * Beim Entfernen entscheidet der Service, ob der Test wieder in den Status „open“ zurückgesetzt wird.
     */
    public void unassignTestCase(TestCase testCase) {
        if (testCase == null || !loginBean.canAssignTestCase(testCase.getTest())) {
            addErrorMessage("You are not allowed to manage tests.");
            return;
        }

        if (testCase.getId() <= 0) {
            return;
        }

        if (testCaseService.unassignFromTest(testCase.getId())) {
            addInfoMessage("TestCase unassigned successfully.");
            return;
        }

        addErrorMessage("The selected TestCase could not be unassigned.");
    }

    /**
     * Itt rendeli a TM a kiválasztott tester usert egy konkrét testhez.
     */
    public void assignTester() {
        if (selectedTesterTestId == null) {
            addErrorMessage("A test must be selected.");
            return;
        }

        Test selectedTest = findTestById(selectedTesterTestId);
        if (!loginBean.canAssignTestCase(selectedTest)) {
            addErrorMessage("You are not allowed to assign a tester to this test.");
            return;
        }

        if (selectedTesterId == null) {
            addErrorMessage("A tester must be selected.");
            return;
        }

        if (testService.assignTester(selectedTesterTestId, selectedTesterId)) {
            selectedTesterId = null;
            addInfoMessage("Tester assigned successfully.");
            return;
        }

        addErrorMessage("The selected tester could not be assigned to the selected test.");
    }

    public List<TestCase> getAvailableTestCases() {
        return testCaseService.findAll();
    }

    public List<Test> getAssignableTests() {
        // In den Zuweisungslisten werden nur die Tests angezeigt, die der Benutzer tatsächlich verwalten darf.
        return getTests().stream()
                .filter(loginBean::canAssignTestCase)
                .toList();
    }

    private Test findTestById(Integer testId) {
        if (testId == null) {
            return null;
        }

        for (Test test : getTests()) {
            if (test.getId() == testId) {
                return test;
            }
        }

        return null;
    }

    public List<User> getAvailableTesters() {
        return userService.findAllTesters();
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

    public Integer getSelectedTesterTestId() {
        return selectedTesterTestId;
    }

    public void setSelectedTesterTestId(Integer selectedTesterTestId) {
        this.selectedTesterTestId = selectedTesterTestId;
    }

    public Integer getSelectedTesterId() {
        return selectedTesterId;
    }

    public void setSelectedTesterId(Integer selectedTesterId) {
        this.selectedTesterId = selectedTesterId;
    }

    public List<Test> getTests() {
        return testService.findAll();
    }

    /**
     * Diese Zähler dienen dem Übersichts-Panel im Manager-Dashboard.
     */
    public long getOpenTestCount() {
        return getTests().stream()
                .filter(test -> "open".equals(test.getStatus()))
                .count();
    }

    public long getInProgressTestCount() {
        return getTests().stream()
                .filter(test -> "in_progress".equals(test.getStatus()))
                .count();
    }

    public long getDoneTestCount() {
        return getTests().stream()
                .filter(test -> "done".equals(test.getStatus()))
                .count();
    }
}
