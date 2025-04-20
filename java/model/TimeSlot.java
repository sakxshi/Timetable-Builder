package model;

public class TimeSlot {
    private String day;
    private String time;
    
    public TimeSlot(String day, String time) {
        this.day = day;
        this.time = time;
    }
    
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimeSlot other = (TimeSlot) obj;
        return day.equals(other.day) && time.equals(other.time);
    }
    
    @Override
    public int hashCode() {
        return 31 * day.hashCode() + time.hashCode();
    }
    
    @Override
    public String toString() { return day + " " + time; }
}