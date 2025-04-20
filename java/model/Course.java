package model;

public class Course {
    private String code;
    private String subject;
    private String domain;
    private int students;
    private int lectureHours;
    private int labHours;
    private String lectureInstructor;
    private String labInstructor;
    
    public Course(String code, String subject, String domain, int students, 
                 int lectureHours, int labHours, String lectureInstructor, String labInstructor) {
        this.code = code;
        this.subject = subject;
        this.domain = domain;
        this.students = students;
        this.lectureHours = lectureHours;
        this.labHours = labHours;
        this.lectureInstructor = lectureInstructor;
        this.labInstructor = labInstructor;
    }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public int getStudents() { return students; }
    public void setStudents(int students) { this.students = students; }
    public int getLectureHours() { return lectureHours; }
    public void setLectureHours(int lectureHours) { this.lectureHours = lectureHours; }
    public int getLabHours() { return labHours; }
    public void setLabHours(int labHours) { this.labHours = labHours; }
    public String getLectureInstructor() { return lectureInstructor; }
    public void setLectureInstructor(String lectureInstructor) { this.lectureInstructor = lectureInstructor; }
    public String getLabInstructor() { return labInstructor; }
    public void setLabInstructor(String labInstructor) { this.labInstructor = labInstructor; }
    
    @Override
    public String toString() { return code + " - " + subject; }
}