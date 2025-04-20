package controller;

import model.*;
import java.util.*;

public class TimetableGenerator {
    private List<Classroom> classrooms;
    private List<Course> courses;
    private List<Instructor> instructors;
    private List<InstructorCourse> instructorCourses;
    private List<TimetableEntry> timetable;
    private List<String> conflicts;
    private Random random;
    private long randomSeed;
    private int strategyVariant;
    
    // Availability tracking maps
    private Map<String, Map<String, Set<Integer>>> instructorAvailability; // day -> time -> instructor IDs
    private Map<String, Map<String, Set<Integer>>> roomAvailability;       // day -> time -> room IDs
    private Map<String, Map<String, Set<String>>> courseYearAvailability;  // day -> time -> domain_year
    
    // Day patterns
    private static final String[] MWF_DAYS = {"Monday", "Wednesday", "Friday"};
    private static final String[] TTS_DAYS = {"Tuesday", "Thursday", "Saturday"};
    private static final String[] ALL_DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    
    // Time slots - ensuring we start from 8:00 AM
    private static final String[] LECTURE_TIME_SLOTS = {
        "8:00 - 9:00", "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", 
        "12:00 - 1:00", "1:00 - 2:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
    };
    
    public TimetableGenerator(List<Classroom> classrooms, List<Course> courses, 
                           List<Instructor> instructors, List<InstructorCourse> instructorCourses) {
        this.classrooms = classrooms;
        this.courses = courses;
        this.instructors = instructors;
        this.instructorCourses = instructorCourses;
        this.timetable = new ArrayList<>();
        this.conflicts = new ArrayList<>();
        this.random = new Random();
        
        // Initialize availability tracking
        initializeAvailabilityMaps();
    }
    
    public void setRandomSeed(long seed) {
        this.randomSeed = seed;
        this.random = new Random(seed);
    }
    
    public void setStrategyVariant(int variant) {
        this.strategyVariant = variant;
    }
    
    public static String[] getLectureTimeSlots() {
        return LECTURE_TIME_SLOTS;
    }
    
    public static String[] getLabTimeSlots() {
        return LECTURE_TIME_SLOTS;
    }
    
    public static String[] getAllDays() {
        return ALL_DAYS;
    }
    
    private void initializeAvailabilityMaps() {
        instructorAvailability = new HashMap<>();
        roomAvailability = new HashMap<>();
        courseYearAvailability = new HashMap<>();
        
        for (String day : ALL_DAYS) {
            instructorAvailability.put(day, new HashMap<>());
            roomAvailability.put(day, new HashMap<>());
            courseYearAvailability.put(day, new HashMap<>());
            
            for (String timeSlot : LECTURE_TIME_SLOTS) {
                instructorAvailability.get(day).put(timeSlot, new HashSet<>());
                roomAvailability.get(day).put(timeSlot, new HashSet<>());
                courseYearAvailability.get(day).put(timeSlot, new HashSet<>());
            }
        }
    }

    public List<TimetableEntry> generateTimetable() {
        System.out.println("Starting domain and year based timetable generation...");
        
        // Clear previous data
        timetable.clear();
        conflicts.clear();
        initializeAvailabilityMaps();
        
        // Sort courses by constraints (more constrained first)
        List<Course> sortedCourses = new ArrayList<>(courses);
        Collections.sort(sortedCourses, (a, b) -> {
            // Prioritize courses with labs
            if (a.getLabHours() > 0 && b.getLabHours() == 0) return -1;
            if (a.getLabHours() == 0 && b.getLabHours() > 0) return 1;
            
            // Then prioritize by total hours (more hours = more constrained)
            int totalA = a.getLectureHours() + a.getLabHours();
            int totalB = b.getLectureHours() + b.getLabHours();
            return Integer.compare(totalB, totalA);
        });
        
        // Process each course with its labs first, then lectures
        for (Course course : sortedCourses) {
            // Schedule labs first (they're more constrained)
            if (course.getLabHours() > 0 && !course.getLabInstructor().equals("0")) {
                scheduleLabs(course);
            }
        }
        
        // Then schedule lectures
        for (Course course : sortedCourses) {
            if (course.getLectureHours() > 0 && !course.getLectureInstructor().equals("0")) {
                scheduleLectures(course);
            }
        }
        
        // Check for conflicts and unscheduled sessions
        detectConflicts();
        
        return timetable;
    }
    
    private void scheduleLectures(Course course) {
        int lectureInstructorId = Integer.parseInt(course.getLectureInstructor());
        String[] days = course.getSchedulePattern().equals("TTS") ? TTS_DAYS : MWF_DAYS;
        String domain = course.getDomain();
        int year = course.getYear();
        String domainYear = domain + "_" + year;
        
        // Try to find suitable time slots across all days in the pattern
        for (String timeSlot : LECTURE_TIME_SLOTS) {
            // Check if we've scheduled all needed lectures
            if (countScheduledSessions(course.getCode(), "Lecture") >= course.getLectureHours()) {
                break;
            }
            
            boolean slotWorks = true;
            Map<String, Classroom> selectedRooms = new HashMap<>();
            
            // Check if this time slot works for all days in the pattern
            for (String day : days) {
                // Check instructor availability
                if (instructorAvailability.get(day).get(timeSlot).contains(lectureInstructorId)) {
                    slotWorks = false;
                    break;
                }
                
                // Check course-year availability (prevent same year/domain clashes)
                if (courseYearAvailability.get(day).get(timeSlot).contains(domainYear)) {
                    slotWorks = false;
                    break;
                }
                
                // Find a suitable room
                Classroom room = findSuitableRoom(course, "Lecture", roomAvailability.get(day).get(timeSlot));
                if (room == null) {
                    slotWorks = false;
                    break;
                }
                
                selectedRooms.put(day, room);
            }
            
            // If the slot works, schedule the lectures
            if (slotWorks) {
                for (String day : days) {
                    // Check if we've scheduled all needed lectures
                    if (countScheduledSessions(course.getCode(), "Lecture") >= course.getLectureHours()) {
                        break;
                    }
                    
                    Classroom room = selectedRooms.get(day);
                    
                    // Create the timetable entry
                    TimetableEntry entry = new TimetableEntry(
                        day, timeSlot, course.getCode(), room.getId(), 
                        lectureInstructorId, "Lecture"
                    );
                    timetable.add(entry);
                    
                    // Update availability maps
                    instructorAvailability.get(day).get(timeSlot).add(lectureInstructorId);
                    roomAvailability.get(day).get(timeSlot).add(room.getId());
                    courseYearAvailability.get(day).get(timeSlot).add(domainYear);
                }
            }
        }
    }
    
    private void scheduleLabs(Course course) {
        if (course.getLabHours() == 0 || course.getLabInstructor().equals("0")) {
            return;
        }
        
        int labInstructorId = Integer.parseInt(course.getLabInstructor());
        String domain = course.getDomain();
        int year = course.getYear();
        String domainYear = domain + "_" + year;
        int labDuration = 2; // Labs are 2 hours
        
        // Try each day
        for (String day : ALL_DAYS) {
            // Check if we've already scheduled all required labs
            if (countScheduledSessions(course.getCode(), "Lab") >= course.getLabHours()) {
                break;
            }
            
            // Try each possible lab starting time
            for (int startIdx = 0; startIdx < LECTURE_TIME_SLOTS.length - (labDuration - 1); startIdx++) {
                String startTime = LECTURE_TIME_SLOTS[startIdx].split(" - ")[0];
                String endTime = LECTURE_TIME_SLOTS[startIdx + (labDuration - 1)].split(" - ")[1];
                String labTimeSlot = startTime + " - " + endTime;
                
                // Check if all consecutive slots are available
                boolean allSlotsAvailable = true;
                
                // Check instructor availability for all hours of the lab
                for (int i = 0; i < labDuration; i++) {
                    String currentTimeSlot = LECTURE_TIME_SLOTS[startIdx + i];
                    
                    if (instructorAvailability.get(day).get(currentTimeSlot).contains(labInstructorId) ||
                        courseYearAvailability.get(day).get(currentTimeSlot).contains(domainYear)) {
                        allSlotsAvailable = false;
                        break;
                    }
                }
                
                if (!allSlotsAvailable) {
                    continue;
                }
                
                // Find a suitable room that's available for the entire lab duration
                Classroom labRoom = null;
                for (Classroom room : classrooms) {
                    if (room.getRoomType().equals("Lab") && 
                        room.getCapacity() >= course.getStudents() &&
                        room.getComputers() > 0) {
                        
                        boolean roomAvailable = true;
                        for (int i = 0; i < labDuration; i++) {
                            String currentTimeSlot = LECTURE_TIME_SLOTS[startIdx + i];
                            if (roomAvailability.get(day).get(currentTimeSlot).contains(room.getId())) {
                                roomAvailable = false;
                                break;
                            }
                        }
                        
                        if (roomAvailable) {
                            labRoom = room;
                            break;
                        }
                    }
                }
                
                if (labRoom != null) {
                    // Create lab entry
                    TimetableEntry entry = new TimetableEntry(
                        day, labTimeSlot, course.getCode(), labRoom.getId(),
                        labInstructorId, "Lab"
                    );
                    timetable.add(entry);
                    
                    // Mark all slots as used
                    for (int i = 0; i < labDuration; i++) {
                        String currentTimeSlot = LECTURE_TIME_SLOTS[startIdx + i];
                        instructorAvailability.get(day).get(currentTimeSlot).add(labInstructorId);
                        roomAvailability.get(day).get(currentTimeSlot).add(labRoom.getId());
                        courseYearAvailability.get(day).get(currentTimeSlot).add(domainYear);
                    }
                    
                    // Break after scheduling one lab
                    break;
                }
            }
        }
    }
    
    private int countScheduledSessions(String courseCode, String sessionType) {
        int count = 0;
        for (TimetableEntry entry : timetable) {
            if (entry.getCourseCode().equals(courseCode) && 
                entry.getSessionType().equals(sessionType)) {
                count++;
            }
        }
        return count;
    }
    
    private Classroom findSuitableRoom(Course course, String sessionType, Set<Integer> bookedRooms) {
        List<Classroom> suitable = new ArrayList<>();
        
        for (Classroom room : classrooms) {
            if (bookedRooms.contains(room.getId())) {
                continue;
            }
            
            if (sessionType.equals("Lecture")) {
                if (room.getRoomType().equals("Lecture") && 
                    room.getCapacity() >= course.getStudents()) {
                    suitable.add(room);
                }
            } else if (sessionType.equals("Lab")) {
                if (room.getRoomType().equals("Lab") && 
                    room.getCapacity() >= course.getStudents() && 
                    room.getComputers() > 0) {
                    suitable.add(room);
                }
            }
        }
        
        if (suitable.isEmpty()) {
            return null;
        }
        
        // Sort by capacity (prefer rooms that are just big enough)
        Collections.sort(suitable, Comparator.comparingInt(r -> 
            Math.abs(r.getCapacity() - course.getStudents())
        ));
        
        return suitable.get(0);
    }
    
    private void detectConflicts() {
        // Check for unscheduled sessions
        for (Course course : courses) {
            int scheduledLectures = countScheduledSessions(course.getCode(), "Lecture");
            int scheduledLabs = countScheduledSessions(course.getCode(), "Lab");
            
            if (scheduledLectures < course.getLectureHours()) {
                conflicts.add("Could not schedule all lectures for " + course.getCode() + 
                             ". Required: " + course.getLectureHours() + 
                             ", Scheduled: " + scheduledLectures);
            }
            
            if (scheduledLabs < course.getLabHours()) {
                conflicts.add("Could not schedule all labs for " + course.getCode() + 
                             ". Required: " + course.getLabHours() + 
                             ", Scheduled: " + scheduledLabs);
            }
        }
        
        // Verify no instructor double-bookings
        Map<String, Map<String, Set<Integer>>> verifyInstructorBookings = new HashMap<>();
        for (String day : ALL_DAYS) {
            verifyInstructorBookings.put(day, new HashMap<>());
            for (String time : LECTURE_TIME_SLOTS) {
                verifyInstructorBookings.get(day).put(time, new HashSet<>());
            }
        }
        
        // Check each entry for instructor double-booking
        for (TimetableEntry entry : timetable) {
            String day = entry.getDay();
            String timeSlot = entry.getTime();
            int instructorId = entry.getInstructorId();
            
            // Handle lab sessions that span multiple hours
            if (entry.getSessionType().equals("Lab")) {
                // Extract start and end times
                String[] times = timeSlot.split(" - ");
                String startTime = times[0];
                String endTime = times[1];
                
                // Find corresponding lecture time slots
                int startIdx = -1;
                int endIdx = -1;
                
                for (int i = 0; i < LECTURE_TIME_SLOTS.length; i++) {
                    if (LECTURE_TIME_SLOTS[i].startsWith(startTime)) {
                        startIdx = i;
                    }
                    if (LECTURE_TIME_SLOTS[i].endsWith(endTime)) {
                        endIdx = i;
                    }
                }
                
                if (startIdx >= 0 && endIdx >= 0) {
                    // Check each hour in the lab
                    for (int i = startIdx; i <= endIdx; i++) {
                        String currentTimeSlot = LECTURE_TIME_SLOTS[i];
                        
                        if (verifyInstructorBookings.get(day).get(currentTimeSlot).contains(instructorId)) {
                            conflicts.add("Instructor " + getInstructorName(instructorId) + 
                                         " is double-booked on " + day + " at " + currentTimeSlot);
                        } else {
                            verifyInstructorBookings.get(day).get(currentTimeSlot).add(instructorId);
                        }
                    }
                }
            } else {
                // Regular lecture hour
                if (verifyInstructorBookings.get(day).get(timeSlot).contains(instructorId)) {
                    conflicts.add("Instructor " + getInstructorName(instructorId) + 
                                 " is double-booked on " + day + " at " + timeSlot);
                } else {
                    verifyInstructorBookings.get(day).get(timeSlot).add(instructorId);
                }
            }
        }
    }
    
    private String getInstructorName(int instructorId) {
        for (Instructor instructor : instructors) {
            if (instructor.getId() == instructorId) {
                return instructor.getFirstName() + " " + instructor.getLastName();
            }
        }
        return "Unknown Instructor";
    }
    
    public List<String> getConflicts() {
        return conflicts;
    }
}
