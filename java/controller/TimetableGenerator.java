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
    
    // Track resource availability by timeslot
    private Map<String, Map<String, Set<Integer>>> roomAvailability;       // day -> time -> room IDs
    private Map<String, Map<String, Set<Integer>>> instructorAvailability; // day -> time -> instructor IDs
    private Map<String, Map<String, Map<String, Map<Integer, Set<String>>>>> domainYearAvailability; // day -> time -> domain -> year -> course codes
    
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
        roomAvailability = new HashMap<>();
        instructorAvailability = new HashMap<>();
        domainYearAvailability = new HashMap<>();
        
        for (String day : ALL_DAYS) {
            roomAvailability.put(day, new HashMap<>());
            instructorAvailability.put(day, new HashMap<>());
            domainYearAvailability.put(day, new HashMap<>());
            
            for (String timeSlot : LECTURE_TIME_SLOTS) {
                roomAvailability.get(day).put(timeSlot, new HashSet<>());
                instructorAvailability.get(day).put(timeSlot, new HashSet<>());
                domainYearAvailability.get(day).put(timeSlot, new HashMap<>());
                // Now domainYearAvailability.get(day).get(timeSlot) is a Map<String, Map<Integer, Set<String>>>
            }
        }
    }

    public List<TimetableEntry> generateTimetable() {
        System.out.println("Starting domain and year based timetable generation...");
        
        // Clear previous data
        timetable.clear();
        conflicts.clear();
        initializeAvailabilityMaps();
        
        // Sort courses by constraints to prioritize the most constrained courses first
        List<Course> sortedCourses = new ArrayList<>(courses);
        Collections.sort(sortedCourses, (a, b) -> {
            // First prioritize by total hours (more hours = more constrained)
            int totalA = a.getLectureHours() + a.getLabHours();
            int totalB = b.getLectureHours() + b.getLabHours();
            
            if (totalA != totalB) {
                return Integer.compare(totalB, totalA);
            }
            
            // Then prioritize labs over lectures
            if (a.getLabHours() > 0 && b.getLabHours() == 0) return -1;
            if (a.getLabHours() == 0 && b.getLabHours() > 0) return 1;
            
            return 0;
        });
        
        // First schedule all labs, then lectures (labs have more constraints)
        for (Course course : sortedCourses) {
            if (course.getLabHours() > 0 && !course.getLabInstructor().equals("0")) {
                scheduleLabs(course);
            }
        }
        
        for (Course course : sortedCourses) {
            if (course.getLectureHours() > 0 && !course.getLectureInstructor().equals("0")) {
                scheduleLectures(course);
            }
        }
        
        // Detect conflicts and unscheduled sessions
        detectConflicts();
        
        return timetable;
    }
    
    private void scheduleLectures(Course course) {
        int lectureInstructorId = Integer.parseInt(course.getLectureInstructor());
        String[] days = course.getSchedulePattern().equals("TTS") ? TTS_DAYS : MWF_DAYS;
        String domain = course.getDomain();
        int year = course.getYear();
        
        // Try each time slot until we schedule all required lectures
        for (String timeSlot : LECTURE_TIME_SLOTS) {
            // Skip if we've already scheduled all needed lectures
            if (countScheduledSessions(course.getCode(), "Lecture") >= course.getLectureHours()) {
                break;
            }
            
            boolean slotWorks = true;
            Map<String, Classroom> selectedRooms = new HashMap<>();
            
            // Check if this time slot works across all days in the pattern
            for (String day : days) {
                // Check instructor availability
                if (instructorAvailability.get(day).get(timeSlot).contains(lectureInstructorId)) {
                    slotWorks = false;
                    break;
                }
                
                // Check if another course in same domain/year is scheduled
                if (isDomainYearConflict(day, timeSlot, domain, year, course.getCode())) {
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
            
            // If the slot works for all days, schedule the lectures
            if (slotWorks) {
                for (String day : days) {
                    // Don't schedule more than needed
                    if (countScheduledSessions(course.getCode(), "Lecture") >= course.getLectureHours()) {
                        break;
                    }
                    
                    Classroom room = selectedRooms.get(day);
                    
                    // Create timetable entry
                    TimetableEntry entry = new TimetableEntry(
                        day, timeSlot, course.getCode(), room.getId(), 
                        lectureInstructorId, "Lecture"
                    );
                    timetable.add(entry);
                    
                    // Mark resources as used
                    markResourcesUsed(day, timeSlot, room.getId(), lectureInstructorId, domain, year, course.getCode());
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
        int labDuration = 2; // Labs are typically 2 hours
        
        // Try to schedule labs for this course
        for (String day : ALL_DAYS) {
            // Don't schedule more labs than needed
            if (countScheduledSessions(course.getCode(), "Lab") >= course.getLabHours()) {
                break;
            }
            
            // Try to find a suitable starting time slot for the lab
            for (int startIdx = 0; startIdx < LECTURE_TIME_SLOTS.length - (labDuration - 1); startIdx++) {
                // Create the lab time slot (e.g. "8:00 - 10:00")
                String startTime = LECTURE_TIME_SLOTS[startIdx].split(" - ")[0];
                String endTime = LECTURE_TIME_SLOTS[startIdx + (labDuration - 1)].split(" - ")[1];
                String labTimeSlot = startTime + " - " + endTime;
                
                // Check if all consecutive slots are available for this lab
                boolean allSlotsAvailable = true;
                
                for (int i = 0; i < labDuration; i++) {
                    String currentSlot = LECTURE_TIME_SLOTS[startIdx + i];
                    
                    // Check instructor availability
                    if (instructorAvailability.get(day).get(currentSlot).contains(labInstructorId)) {
                        allSlotsAvailable = false;
                        break;
                    }
                    
                    // Check domain/year conflict
                    if (isDomainYearConflict(day, currentSlot, domain, year, course.getCode())) {
                        allSlotsAvailable = false;
                        break;
                    }
                }
                
                if (!allSlotsAvailable) {
                    continue;
                }
                
                // Find a suitable lab room that's available for the entire duration
                Classroom labRoom = findAvailableLabRoom(course, day, startIdx, labDuration);
                
                if (labRoom != null) {
                    // Schedule the lab
                    TimetableEntry entry = new TimetableEntry(
                        day, labTimeSlot, course.getCode(), labRoom.getId(), 
                        labInstructorId, "Lab"
                    );
                    timetable.add(entry);
                    
                    // Mark all affected time slots as used
                    for (int i = 0; i < labDuration; i++) {
                        String currentSlot = LECTURE_TIME_SLOTS[startIdx + i];
                        markResourcesUsed(day, currentSlot, labRoom.getId(), labInstructorId, domain, year, course.getCode());
                    }
                    
                    break; // Successfully scheduled a lab, move to next day
                }
            }
        }
    }
    
    // Checks if there's a conflict with another course in the same domain and year
    private boolean isDomainYearConflict(String day, String timeSlot, String domain, int year, String courseCode) {
        Map<String, Map<Integer, Set<String>>> timeMap = domainYearAvailability.get(day).get(timeSlot);
        
        if (!timeMap.containsKey(domain)) {
            // First course in this domain for this time slot
            timeMap.put(domain, new HashMap<>());
            return false;
        }
        
        Map<Integer, Set<String>> domainMap = timeMap.get(domain);
        
        if (!domainMap.containsKey(year) || domainMap.get(year).isEmpty()) {
            // First course in this year for this domain
            return false;
        }
        
        // There's already at least one course from the same domain and year
        return true;
    }
    
    // Mark resources (room, instructor, domain/year) as used for a time slot
    private void markResourcesUsed(String day, String timeSlot, int roomId, int instructorId, String domain, int year, String courseCode) {
        // Mark room as used
        roomAvailability.get(day).get(timeSlot).add(roomId);
        
        // Mark instructor as used
        instructorAvailability.get(day).get(timeSlot).add(instructorId);
        
        // Mark domain/year as used
        Map<String, Map<Integer, Set<String>>> timeMap = domainYearAvailability.get(day).get(timeSlot);
        
        if (!timeMap.containsKey(domain)) {
            timeMap.put(domain, new HashMap<>());
        }
        
        Map<Integer, Set<String>> domainMap = timeMap.get(domain);
        
        if (!domainMap.containsKey(year)) {
            domainMap.put(year, new HashSet<>());
        }
        
        domainMap.get(year).add(courseCode);
    }
    
    // Find a lab room available for the entire lab duration
    private Classroom findAvailableLabRoom(Course course, String day, int startIdx, int labDuration) {
        for (Classroom room : classrooms) {
            if (room.getRoomType().equals("Lab") && 
                room.getCapacity() >= course.getStudents() &&
                room.getComputers() > 0) {
                
                boolean roomAvailable = true;
                
                // Check if room is available for the entire lab duration
                for (int i = 0; i < labDuration; i++) {
                    String currentSlot = LECTURE_TIME_SLOTS[startIdx + i];
                    if (roomAvailability.get(day).get(currentSlot).contains(room.getId())) {
                        roomAvailable = false;
                        break;
                    }
                }
                
                if (roomAvailable) {
                    return room;
                }
            }
        }
        
        return null;
    }
    
    // Helper method to find a suitable room (lecture or lab) for a given session
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
    
    // Count the number of sessions already scheduled for a course and session type
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
    
    // Detect conflicts and unscheduled sessions
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
        
        // Check for room and instructor conflicts (redundant verification)
        verifyNoConflicts();
    }
    
    // Double-check that there are no resource conflicts in the generated timetable
    private void verifyNoConflicts() {
        Map<String, Map<String, Set<Integer>>> checkRooms = new HashMap<>();
        Map<String, Map<String, Set<Integer>>> checkInstructors = new HashMap<>();
        
        // Initialize verification maps
        for (String day : ALL_DAYS) {
            checkRooms.put(day, new HashMap<>());
            checkInstructors.put(day, new HashMap<>());
            
            for (String time : LECTURE_TIME_SLOTS) {
                checkRooms.get(day).put(time, new HashSet<>());
                checkInstructors.get(day).put(time, new HashSet<>());
            }
        }
        
        // Check each entry in the timetable
        for (TimetableEntry entry : timetable) {
            String day = entry.getDay();
            String timeSlot = entry.getTime();
            int roomId = entry.getRoomId();
            int instructorId = entry.getInstructorId();
            
            // For labs that span multiple hours, we need to check all affected slots
            if (entry.getSessionType().equals("Lab")) {
                String[] times = timeSlot.split(" - ");
                String startTime = times[0];
                String endTime = times[1];
                
                int startIdx = -1;
                int endIdx = -1;
                
                // Find start and end indices in the time slots array
                for (int i = 0; i < LECTURE_TIME_SLOTS.length; i++) {
                    if (LECTURE_TIME_SLOTS[i].startsWith(startTime)) {
                        startIdx = i;
                    }
                    if (LECTURE_TIME_SLOTS[i].endsWith(endTime)) {
                        endIdx = i;
                    }
                }
                
                // Check all affected time slots
                if (startIdx >= 0 && endIdx >= 0) {
                    for (int i = startIdx; i <= endIdx; i++) {
                        String currentSlot = LECTURE_TIME_SLOTS[i];
                        
                        // Check room conflict
                        if (checkRooms.get(day).get(currentSlot).contains(roomId)) {
                            conflicts.add("Room " + roomId + " is double-booked on " + day + " at " + currentSlot);
                        } else {
                            checkRooms.get(day).get(currentSlot).add(roomId);
                        }
                        
                        // Check instructor conflict
                        if (checkInstructors.get(day).get(currentSlot).contains(instructorId)) {
                            conflicts.add("Instructor " + getInstructorName(instructorId) + 
                                         " is double-booked on " + day + " at " + currentSlot);
                        } else {
                            checkInstructors.get(day).get(currentSlot).add(instructorId);
                        }
                    }
                }
            } else { // Lectures are simpler - just check the one time slot
                // Check room conflict
                if (checkRooms.get(day).get(timeSlot).contains(roomId)) {
                    conflicts.add("Room " + roomId + " is double-booked on " + day + " at " + timeSlot);
                } else {
                    checkRooms.get(day).get(timeSlot).add(roomId);
                }
                
                // Check instructor conflict
                if (checkInstructors.get(day).get(timeSlot).contains(instructorId)) {
                    conflicts.add("Instructor " + getInstructorName(instructorId) + 
                                 " is double-booked on " + day + " at " + timeSlot);
                } else {
                    checkInstructors.get(day).get(timeSlot).add(instructorId);
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
