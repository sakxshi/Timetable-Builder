package model;

public class InstructorCourse {
    private int instructorId;
    private String courseCode;
    private String type; // "Lecture" or "Lab"
    
    public InstructorCourse(int instructorId, String courseCode, String type) {
        this.instructorId = instructorId;
        this.courseCode = courseCode;
        this.type = type;
    }
    
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}