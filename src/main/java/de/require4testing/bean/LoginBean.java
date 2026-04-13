package de.require4testing.bean;

import de.require4testing.model.Task;
import de.require4testing.model.Test;
import de.require4testing.model.TestReport;
import de.require4testing.model.User;
import de.require4testing.model.UserRoles;
import de.require4testing.service.UserService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private final UserService userService = new UserService();

    private String email;
    private String password;

    private boolean loggedIn = false;
    private User currentUser;
    private Integer currentUserId;

    public String login() {
        User authenticatedUser = userService.authenticate(email, password);
        if (authenticatedUser != null) {
            loggedIn = true;
            currentUser = authenticatedUser;
            currentUserId = authenticatedUser.getId();
            password = "";
            return "dashboard.xhtml?faces-redirect=true";
        }

        loggedIn = false;
        currentUser = null;
        currentUserId = null;
        addErrorMessage("Invalid email or password.");
        return null;
    }

    public String logout() {
        loggedIn = false;
        currentUser = null;
        currentUserId = null;
        password = "";
        return "dashboard.xhtml?faces-redirect=true";
    }

    // GETTER / SETTER

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public User getCurrentUser() {
        if (currentUser == null && loggedIn && currentUserId != null) {
            currentUser = userService.findById(currentUserId);
        }
        return currentUser;
    }

    public boolean hasRole(String... roles) {
        User user = getCurrentUser();
        if (user == null || roles == null) {
            return false;
        }

        for (String role : roles) {
            if (role != null && role.equals(user.getRole())) {
                return true;
            }
        }

        return false;
    }

    public boolean canAccessRequirement() {
        return loggedIn;
    }

    public boolean canManageRequirement() {
        return hasRole(UserRoles.REQUIREMENTS_ENGINEER);
    }

    public boolean canAccessTestCase() {
        return loggedIn;
    }

    public boolean canManageTestCase() {
        return hasRole(UserRoles.REQUIREMENTS_ENGINEER, UserRoles.TEST_MANAGER);
    }

    public boolean canAccessTest() {
        return loggedIn;
    }

    public boolean canCreateTest() {
        return canAccessTest();
    }

    public boolean canManageTest(Test test) {
        return canAccessTest() && isOwnTest(test);
    }

    public boolean canAssignTestCase(Test test) {
        return hasRole(UserRoles.TEST_MANAGER) && isOwnTest(test);
    }

    public boolean canAccessReport() {
        return hasRole(UserRoles.TEST_MANAGER, UserRoles.TESTER);
    }

    public boolean canCreateReport() {
        return hasRole(UserRoles.TEST_MANAGER, UserRoles.TESTER);
    }

    public boolean canCreateReportForTest(Test test) {
        return hasRole(UserRoles.TEST_MANAGER) || (hasRole(UserRoles.TESTER) && isAssignedTester(test));
    }

    public boolean canDeleteReport(TestReport testReport) {
        return hasRole(UserRoles.TEST_MANAGER) || (hasRole(UserRoles.TESTER) && isOwnReport(testReport));
    }

    public boolean canAccessTask() {
        return loggedIn;
    }

    public boolean canCreateTask() {
        return hasRole(UserRoles.REQUIREMENTS_ENGINEER, UserRoles.TEST_MANAGER);
    }

    public boolean canManageTask(Task task) {
        return hasRole(UserRoles.REQUIREMENTS_ENGINEER, UserRoles.TEST_MANAGER)
                || (canCreateTask() && isOwnTask(task));
    }

    public String getRequirementPermissionNote() {
        if (canManageRequirement()) {
            return null;
        }

        return "Note: you are logged in with the " + getRoleCode() + " role, so you can view requirements but cannot create, edit, or delete them.";
    }

    public String getTestCasePermissionNote() {
        if (canManageTestCase()) {
            return null;
        }

        return "Note: you are logged in with the " + getRoleCode() + " role, so you can view test cases here but cannot create, edit, or delete them.";
    }

    public String getTestPermissionNote() {
        if (hasRole(UserRoles.REQUIREMENTS_ENGINEER)) {
            return "Note: you are logged in with the RE role, so you can view tests but cannot create, assign, unassign, or delete them.";
        }

        if (hasRole(UserRoles.TEST_FALL_ENGINEER)) {
            return "Note: you are logged in with the TFE role. You can create your own tests, but you cannot assign or unassign test cases.";
        }

        if (hasRole(UserRoles.TESTER)) {
            return "Note: you are logged in with the T role. You can create your own tests, but you cannot assign or unassign test cases.";
        }

        if (hasRole(UserRoles.TEST_MANAGER)) {
            return "Note: you are logged in with the TM role. You can assign test cases only to your own tests.";
        }

        return null;
    }

    public String getReportPermissionNote() {
        if (hasRole(UserRoles.REQUIREMENTS_ENGINEER)) {
            return "Note: you are logged in with the RE role, so you can only view reports here.";
        }

        if (hasRole(UserRoles.TEST_FALL_ENGINEER)) {
            return "Note: you are logged in with the TFE role, so you cannot create or delete reports.";
        }

        if (hasRole(UserRoles.TESTER)) {
            return "Note: you are logged in with the T role. You can create reports only for your own tests, and you can delete only your own reports.";
        }

        if (hasRole(UserRoles.TEST_MANAGER)) {
            return "Note: you are logged in with the TM role. You can create reports for any test and delete any report.";
        }

        return null;
    }

    public String getTaskPermissionNote() {
        if (hasRole(UserRoles.REQUIREMENTS_ENGINEER)) {
            return "Note: you are logged in with the RE role. You can create tasks and manage all tasks.";
        }

        if (hasRole(UserRoles.TEST_MANAGER)) {
            return "Note: you are logged in with the TM role. You can create tasks and manage all tasks.";
        }

        if (hasRole(UserRoles.TEST_FALL_ENGINEER) || hasRole(UserRoles.TESTER)) {
            return "Note: you are logged in with the " + getRoleCode() + " role, so you can view tasks but cannot create, edit, or delete them.";
        }

        return null;
    }

    public String getRoleCode() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : "-";
    }

    private boolean isOwnTest(Test test) {
        User user = getCurrentUser();
        return user != null
                && test != null
                && test.getCreatedBy() != null
                && test.getCreatedBy().getId() == user.getId();
    }

    private boolean isOwnTask(Task task) {
        User user = getCurrentUser();
        return user != null
                && task != null
                && task.getUser() != null
                && task.getUser().getId() == user.getId();
    }

    private boolean isOwnReport(TestReport testReport) {
        User user = getCurrentUser();
        return user != null
                && testReport != null
                && testReport.getUser() != null
                && testReport.getUser().getId() == user.getId();
    }

    private boolean isAssignedTester(Test test) {
        User user = getCurrentUser();
        return user != null
                && test != null
                && test.getAssignedTester() != null
                && test.getAssignedTester().getId() == user.getId();
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}
