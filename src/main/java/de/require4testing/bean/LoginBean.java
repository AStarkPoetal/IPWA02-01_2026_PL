package de.require4testing.bean;

import de.require4testing.model.User;
import de.require4testing.service.UserService;
import jakarta.enterprise.context.SessionScoped;
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

    public String login() {

        if ("test@test.com".equals(email) && "12345".equals(password)) {
            loggedIn = true;
            currentUser = userService.findOrCreateDemoUser();
            return "dashboard.xhtml?faces-redirect=true";
        }

        return null;
    }

    public String logout() {
        loggedIn = false;
        currentUser = null;
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
        return currentUser;
    }
}
