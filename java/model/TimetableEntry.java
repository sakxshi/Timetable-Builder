package model;

public class TimetableEntry {
    private String day;
    private String time;
    private String courseCode;
    private int roomId;
    private int instructorId;
    private String sessionType; // "Lecture" or "Lab"
    
    public TimetableEntry(String day, String time, String courseCode, int roomId, int instructorId, String sessionType) {
        this.day = day;
        this.time = time;
        this.courseCode = courseCode;
        this.roomId = roomId;
        this.instructorId = instructorId;
        this.sessionType = sessionType;
    }
    
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    
    public TimeSlot getTimeSlot() { return new TimeSlot(day, time); }
}