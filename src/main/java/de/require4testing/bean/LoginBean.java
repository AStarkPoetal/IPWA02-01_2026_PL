package de.require4testing.bean;

import de.require4testing.model.User;
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
        return "login.xhtml?faces-redirect=true";
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
        return hasRole("TM", "RE");
    }

    public boolean canAccessTestCase() {
        return hasRole("TM", "RE", "TFE");
    }

    public boolean canAccessTest() {
        return hasRole("TM", "TFE", "T");
    }

    public boolean canAccessReport() {
        return hasRole("TM", "TFE", "T");
    }

    public boolean canAccessTask() {
        return hasRole("TM", "TFE", "T");
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}
