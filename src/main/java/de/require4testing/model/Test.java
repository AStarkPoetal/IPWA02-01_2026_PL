package de.require4testing.model;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private int id;
    private String name;
    private String status;
    private User createdBy;
    private List<TestCase> testCases = new ArrayList<>();

    public Test() {
    }

    public Test(int id, String name, String status, User createdBy) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdBy = createdBy;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}
