package de.require4testing.model;

public class TestCase {

    private int id;
    private String name;
    private String description;
    private String expectedResult;
    private Requirement requirement;
    private Test test;

    public TestCase() {
    }

    public TestCase(int id, String name, String description, String expectedResult, Requirement requirement, Test test) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.expectedResult = expectedResult;
        this.requirement = requirement;
        this.test = test;
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

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }
}
