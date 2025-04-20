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
    
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] TIME_SLOTS = {
        "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 1:00", 
        "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
    };
    
    // Maximum number of iterations to try for conflict resolution
    private static final int MAX_ITERATIONS = 100;
    
    public TimetableGenerator(List<Classroom> classrooms, List<Course> courses, 
                             List<Instructor> instructors, List<InstructorCourse> instructorCourses) {
        this.classrooms = classrooms;
        this.courses = courses;
        this.instructors = instructors;
        this.instructorCourses = instructorCourses;
        this.timetable = new ArrayList<>();
        this.conflicts = new ArrayList<>();
        this.random = new Random();
    }
    
    public void setRandomSeed(long seed) {
        this.random = new Random(seed);
    }
    
    public void setStrategyVariant(int variant) {
        // Kept for compatibility with existing code
    }
    
    public List<TimetableEntry> generateTimetable() {
        System.out.println("Starting timetable generation...");
        List<TimetableEntry> bestTimetable = null;
        List<String> fewestConflicts = null;
        int fewestConflictCount = Integer.MAX_VALUE;
        
        // Try multiple approaches with different strategies
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            System.out.println("Iteration " + (iteration + 1) + " of " + MAX_ITERATIONS);
            
            // Clear previous attempt
            timetable.clear();
            conflicts.clear();
            
            // Choose a strategy based on the iteration
            int strategy = iteration % 3;
            
            switch (strategy) {
                case 0:
                    System.out.println("Using even distribution strategy");
                    generateWithEvenDistribution();
                    break;
                case 1:
                    System.out.println("Using prioritized scheduling strategy");
                    generateWithPrioritization();
                    break;
                case 2:
                    System.out.println("Using randomized scheduling strategy");
                    generateWithRandomization();
                    break;
            }
            
            // See if this is the best solution so far
            if (conflicts.size() < fewestConflictCount) {
                fewestConflictCount = conflicts.size();
                bestTimetable = new ArrayList<>(timetable);
                fewestConflicts = new ArrayList<>(conflicts);
                
                System.out.println("New best solution found with " + fewestConflictCount + " conflicts");
                
                // If no conflicts, we're done!
                if (fewestConflictCount == 0) {
                    System.out.println("Perfect solution found!");
                    break;
                }
            }
        }
        
        // Use the best solution found
        timetable = bestTimetable;
        conflicts = fewestConflicts;
        
        System.out.println("Final timetable generated with " + conflicts.size() + " conflicts");
        return timetable;
    }
    
    private void generateWithEvenDistribution() {
        // Create course sessions
        List<CourseSession> allSessions = createCourseSessions();
        int totalSessions = allSessions.size();
        
        // Distribute evenly across days
        int sessionsPerDay = (int) Math.ceil((double) totalSessions / DAYS.length);
        
        // Sort by constraints (most difficult first)
        sortSessionsByConstraints(allSessions);
        
        // For each day, create a fresh schedule
        for (String day : DAYS) {
            Map<String, Set<Integer>> roomBookings = new HashMap<>();
            Map<String, Set<Integer>> instructorBookings = new HashMap<>();
            Map<String, Set<String>> courseBookings = new HashMap<>();
            
            for (String time : TIME_SLOTS) {
                roomBookings.put(time, new HashSet<>());
                instructorBookings.put(time, new HashSet<>());
                courseBookings.put(time, new HashSet<>());
            }
            
            // Get unscheduled sessions
            List<CourseSession> unscheduledSessions = new ArrayList<>();
            for (CourseSession session : allSessions) {
                if (!session.isScheduled()) {
                    unscheduledSessions.add(session);
                }
            }
            
            // Schedule up to target number for this day
            int sessionsScheduledToday = 0;
            for (CourseSession session : unscheduledSessions) {
                if (sessionsScheduledToday >= sessionsPerDay) {
                    break;
                }
                
                boolean scheduled = scheduleSessionForDay(
                    session, day, roomBookings, instructorBookings, courseBookings);
                
                if (scheduled) {
                    sessionsScheduledToday++;
                }
            }
        }
        
        // Handle any remaining unscheduled sessions
        handleRemainingUnscheduledSessions(allSessions);
    }
    
    private void generateWithPrioritization() {
        // Create sessions
        List<CourseSession> allSessions = createCourseSessions();
        
        // Prioritize by department, instructor load, etc.
        Collections.sort(allSessions, (s1, s2) -> {
            // First by session type
            int typeComparison = s1.getSessionType().compareTo(s2.getSessionType());
            if (typeComparison != 0) return typeComparison;
            
            // Then by department
            String dept1 = s1.getCourse().getDomain();
            String dept2 = s2.getCourse().getDomain();
            int deptComparison = dept1.compareTo(dept2);
            if (deptComparison != 0) return deptComparison;
            
            // Then by course code
            return s1.getCourse().getCode().compareTo(s2.getCourse().getCode());
        });
        
        // Try to schedule same departments on same days
        Map<String, List<String>> departmentDays = new HashMap<>();
        for (CourseSession session : allSessions) {
            String dept = session.getCourse().getDomain();
            if (!departmentDays.containsKey(dept)) {
                List<String> days = new ArrayList<>(Arrays.asList(DAYS));
                Collections.shuffle(days, random);
                departmentDays.put(dept, days);
            }
        }
        
        // For each department, try to schedule on preferred days
        for (String dept : departmentDays.keySet()) {
            List<CourseSession> deptSessions = new ArrayList<>();
            for (CourseSession session : allSessions) {
                if (session.getCourse().getDomain().equals(dept) && !session.isScheduled()) {
                    deptSessions.add(session);
                }
            }
            
            // For each preferred day
            for (String day : departmentDays.get(dept)) {
                Map<String, Set<Integer>> roomBookings = new HashMap<>();
                Map<String, Set<Integer>> instructorBookings = new HashMap<>();
                Map<String, Set<String>> courseBookings = new HashMap<>();
                
                initializeBookingsFromTimetable(day, roomBookings, instructorBookings, courseBookings);
                
                // Try to schedule department sessions on this day
                for (CourseSession session : deptSessions) {
                    if (!session.isScheduled()) {
                        scheduleSessionForDay(
                            session, day, roomBookings, instructorBookings, courseBookings);
                    }
                }
            }
        }
        
        // Handle any remaining unscheduled sessions
        handleRemainingUnscheduledSessions(allSessions);
    }
    
    private void generateWithRandomization() {
        // Create sessions
        List<CourseSession> allSessions = createCourseSessions();
        
        // Randomize session order
        Collections.shuffle(allSessions, random);
        
        // For each session, try all days until it fits
        for (CourseSession session : allSessions) {
            // Try days in random order
            List<String> days = new ArrayList<>(Arrays.asList(DAYS));
            Collections.shuffle(days, random);
            
            boolean scheduled = false;
            for (String day : days) {
                Map<String, Set<Integer>> roomBookings = new HashMap<>();
                Map<String, Set<Integer>> instructorBookings = new HashMap<>();
                Map<String, Set<String>> courseBookings = new HashMap<>();
                
                initializeBookingsFromTimetable(day, roomBookings, instructorBookings, courseBookings);
                
                // Try to schedule
                scheduled = scheduleSessionForDay(
                    session, day, roomBookings, instructorBookings, courseBookings);
                
                if (scheduled) {
                    break;
                }
            }
            
            if (!scheduled) {
                conflicts.add("Could not schedule " + session.getCourse().getCode() + 
                             " " + session.getSessionType() + " session. No suitable time slot found.");
            }
        }
    }
    
    private void handleRemainingUnscheduledSessions(List<CourseSession> allSessions) {
        // Second pass for any unscheduled sessions
        List<CourseSession> unscheduled = new ArrayList<>();
        for (CourseSession session : allSessions) {
            if (!session.isScheduled()) {
                unscheduled.add(session);
            }
        }
        
        // Try more aggressively to fit these in
        for (CourseSession session : unscheduled) {
            boolean scheduled = false;
            
            // Try each day
            for (String day : DAYS) {
                Map<String, Set<Integer>> roomBookings = new HashMap<>();
                Map<String, Set<Integer>> instructorBookings = new HashMap<>();
                Map<String, Set<String>> courseBookings = new HashMap<>();
                
                initializeBookingsFromTimetable(day, roomBookings, instructorBookings, courseBookings);
                
                // Try to schedule
                scheduled = scheduleSessionForDay(
                    session, day, roomBookings, instructorBookings, courseBookings);
                
                if (scheduled) {
                    break;
                }
            }
            
            if (!scheduled) {
                conflicts.add("Could not schedule " + session.getCourse().getCode() + 
                             " " + session.getSessionType() + " session. No suitable time slot found.");
            }
        }
    }
    
    private void initializeBookingsFromTimetable(String day, 
                                               Map<String, Set<Integer>> roomBookings,
                                               Map<String, Set<Integer>> instructorBookings,
                                               Map<String, Set<String>> courseBookings) {
        // Initialize the booking maps
        for (String time : TIME_SLOTS) {
            roomBookings.put(time, new HashSet<>());
            instructorBookings.put(time, new HashSet<>());
            courseBookings.put(time, new HashSet<>());
        }
        
        // Populate from existing timetable entries for this day
        for (TimetableEntry entry : timetable) {
            if (entry.getDay().equals(day)) {
                String time = entry.getTime();
                roomBookings.get(time).add(entry.getRoomId());
                instructorBookings.get(time).add(entry.getInstructorId());
                courseBookings.get(time).add(entry.getCourseCode());
            }
        }
    }
    
    private boolean scheduleSessionForDay(CourseSession session, String day,
                                        Map<String, Set<Integer>> roomBookings,
                                        Map<String, Set<Integer>> instructorBookings,
                                        Map<String, Set<String>> courseBookings) {
        Course course = session.getCourse();
        String sessionType = session.getSessionType();
        int instructorId = session.getInstructorId();
        
        // Find suitable rooms
        List<Classroom> suitableRooms = findSuitableRooms(course, sessionType);
        if (suitableRooms.isEmpty()) {
            conflicts.add("No suitable rooms found for " + course.getCode() + " " + sessionType);
            return false;
        }
        
        // Sort rooms by best fit (closest to class size)
        suitableRooms.sort(Comparator.comparingInt(room -> 
            Math.abs(room.getCapacity() - course.getStudents())));
        
        // Randomize time slots to avoid bias
        List<String> timeSlots = new ArrayList<>(Arrays.asList(TIME_SLOTS));
        Collections.shuffle(timeSlots, random);
        
        // Try each time slot
        for (String time : timeSlots) {
            // Check if instructor is available
            if (instructorBookings.get(time).contains(instructorId)) {
                continue;
            }
            
            // Check if course already scheduled at this time
            if (courseBookings.get(time).contains(course.getCode())) {
                continue;
            }
            
            // Try each suitable room
            for (Classroom room : suitableRooms) {
                if (!roomBookings.get(time).contains(room.getId())) {
                    // Schedule here
                    roomBookings.get(time).add(room.getId());
                    instructorBookings.get(time).add(instructorId);
                    courseBookings.get(time).add(course.getCode());
                    
                    // Add to timetable
                    timetable.add(new TimetableEntry(
                        day, time, course.getCode(), room.getId(),
                        instructorId, sessionType
                    ));
                    
                    session.setScheduled(true);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private List<CourseSession> createCourseSessions() {
        List<CourseSession> sessions = new ArrayList<>();
        
        for (Course course : courses) {
            // Create lecture sessions
            if (course.getLectureHours() > 0 && !course.getLectureInstructor().equals("0")) {
                int instructorId = Integer.parseInt(course.getLectureInstructor());
                for (int i = 0; i < course.getLectureHours(); i++) {
                    sessions.add(new CourseSession(course, "Lecture", instructorId));
                }
            }
            
            // Create lab sessions
            if (course.getLabHours() > 0 && !course.getLabInstructor().equals("0")) {
                int instructorId = Integer.parseInt(course.getLabInstructor());
                for (int i = 0; i < course.getLabHours(); i++) {
                    sessions.add(new CourseSession(course, "Lab", instructorId));
                }
            }
        }
        
        return sessions;
    }
    
    private void sortSessionsByConstraints(List<CourseSession> sessions) {
        // Sort sessions by various constraints
        sessions.sort((s1, s2) -> {
            // First sort by session type (labs are harder to schedule)
            if (s1.getSessionType().equals("Lab") && !s2.getSessionType().equals("Lab")) {
                return -1;
            } else if (!s1.getSessionType().equals("Lab") && s2.getSessionType().equals("Lab")) {
                return 1;
            }
            
            // Then by class size (large classes are harder to fit)
            return Integer.compare(s2.getCourse().getStudents(), s1.getCourse().getStudents());
        });
    }
    
    private List<Classroom> findSuitableRooms(Course course, String sessionType) {
        List<Classroom> suitable = new ArrayList<>();
        
        for (Classroom room : classrooms) {
            if (sessionType.equals("Lecture") && 
                room.getRoomType().equals("Lecture") && 
                room.getCapacity() >= course.getStudents()) {
                suitable.add(room);
            } else if (sessionType.equals("Lab") && 
                      room.getRoomType().equals("Lab") && 
                      room.getCapacity() >= course.getStudents() && 
                      room.getComputers() > 0) {
                suitable.add(room);
            }
        }
        
        return suitable;
    }
    
    public List<String> getConflicts() {
        return conflicts;
    }
    
    public static String[] getDays() {
        return DAYS;
    }
    
    public static String[] getTimeSlots() {
        return TIME_SLOTS;
    }
    
    // Inner class to represent a course session that needs to be scheduled
    private class CourseSession {
        private Course course;
        private String sessionType;
        private int instructorId;
        private boolean scheduled;
        
        public CourseSession(Course course, String sessionType, int instructorId) {
            this.course = course;
            this.sessionType = sessionType;
            this.instructorId = instructorId;
            this.scheduled = false;
        }
        
        public Course getCourse() { return course; }
        public String getSessionType() { return sessionType; }
        public int getInstructorId() { return instructorId; }
        public boolean isScheduled() { return scheduled; }
        public void setScheduled(boolean scheduled) { this.scheduled = scheduled; }
    }
}
