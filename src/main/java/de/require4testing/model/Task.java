package de.require4testing.model;

public class Task {

    private int id;
    private String name;
    private String description;
    private String status;
    private User user;

    public Task() {
    }

    public Task(int id, String name, String description, String status, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
