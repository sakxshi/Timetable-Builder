package model;

public class Course {
    private String code;
    private String subject;
    private String domain;
    private int year;
    private int students;
    private int lectureHours;
    private int labHours;
    private String lectureInstructor;
    private String labInstructor;
    private String schedulePattern;

    public Course(String code, String subject, String domain, int year, int students, 
                 int lectureHours, int labHours, String lectureInstructor, 
                 String labInstructor, String schedulePattern) {
        this.code = code;
        this.subject = subject;
        this.domain = domain;
        this.year = year;
        this.students = students;
        this.lectureHours = lectureHours;
        this.labHours = labHours;
        this.lectureInstructor = lectureInstructor;
        this.labInstructor = labInstructor;
        this.schedulePattern = schedulePattern;
    }
    
    // Add this overloaded constructor to fix the course management panel
    public Course(String code, String subject, String domain, int students, 
                int lectureHours, int labHours, String lectureInstructor, String labInstructor) {
        this(code, subject, domain, extractYearFromCode(code), students, lectureHours, 
            labHours, lectureInstructor, labInstructor, "MWF");
    }
    
    private static int extractYearFromCode(String code) {
        // Extract year from course code (e.g., CSF213 -> 2)
        if (code.contains("F")) {
            int fIndex = code.indexOf("F");
            if (fIndex + 1 < code.length()) {
                try {
                    return Integer.parseInt(code.substring(fIndex + 1, fIndex + 2));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    // Getters and setters for all fields
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
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
    
    public String getSchedulePattern() { return schedulePattern; }
    public void setSchedulePattern(String schedulePattern) { this.schedulePattern = schedulePattern; }
    
    @Override
    public String toString() {
        return code + " - " + subject;
    }
}
