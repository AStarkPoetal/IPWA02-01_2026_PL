package de.require4testing.model;

public class TestReport {

    private int id;
    private String name;
    private String status;
    private Test test;
    private User user;

    public TestReport() {
    }

    public TestReport(int id, String name, String status, Test test, User user) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.test = test;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
