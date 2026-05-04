package de.require4testing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
/**
 * Entität, die den Benutzer des Systems mit Rolleninformationen repräsentiert. Dieselbe Entität wird für Login,
 * Berechtigungen und Datensatzbeziehungen verwendet.
 */
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Name", nullable = false, length = 30)
    private String name;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "Passwort", nullable = false, length = 5)
    private String password;

    @Column(name = "Role", nullable = false, length = 3)
    private String role;

    @OneToMany(mappedBy = "createdBy")
    private List<Test> tests = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<TestReport> testReports = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Task> tasks = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<TestReport> getTestReports() {
        return testReports;
    }

    public void setTestReports(List<TestReport> testReports) {
        this.testReports = testReports;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
