package model;

public class Instructor {
    private int id;
    private String firstName;
    private String lastName;
    private String department;
    
    public Instructor(int id, String firstName, String lastName, String department) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    @Override
    public String toString() { return firstName + " " + lastName; }
}