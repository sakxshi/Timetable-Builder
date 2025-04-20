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
    
    // For retry mechanism
    private Random random;
    private int strategyVariant;
    
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] TIME_SLOTS = {
        "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 1:00", 
        "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
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
        this.strategyVariant = 0;
    }
    
    // Method to set random seed for varied retry strategies
    public void setRandomSeed(long seed) {
        this.random = new Random(seed);
    }
    
    // Method to set strategy variant
    public void setStrategyVariant(int variant) {
        this.strategyVariant = variant;
    }
    
    public List<TimetableEntry> generateTimetable() {
        timetable.clear();
        conflicts.clear();
        
        // Maps to track allocations
        Map<TimeSlot, Set<Integer>> roomAllocations = new HashMap<>();
        Map<TimeSlot, Set<Integer>> instructorAllocations = new HashMap<>();
        Map<TimeSlot, Set<String>> courseAllocations = new HashMap<>();
        
        // Initialize maps
        for (String day : DAYS) {
            for (String time : TIME_SLOTS) {
                TimeSlot slot = new TimeSlot(day, time);
                roomAllocations.put(slot, new HashSet<>());
                instructorAllocations.put(slot, new HashSet<>());
                courseAllocations.put(slot, new HashSet<>());
            }
        }
        
        // Create a copy of courses that we can sort differently based on strategy
        List<Course> coursesCopy = new ArrayList<>(courses);
        
        // Apply different course ordering strategies based on variant
        switch (strategyVariant % 4) {
            case 0:
                // Default order (no sort)
                break;
            case 1:
                // Sort by number of students (largest first)
                coursesCopy.sort(Comparator.comparingInt(Course::getStudents).reversed());
                break;
            case 2:
                // Sort by total hours (most hours first)
                coursesCopy.sort(Comparator.comparingInt((Course c) -> 
    c.getLectureHours() + c.getLabHours()).reversed());

                break;
            case 3:
                // Random order
                Collections.shuffle(coursesCopy, random);
                break;
        }
        
        // Process courses in the determined order
        for (Course course : coursesCopy) {
            if (course.getLectureHours() > 0 && !course.getLectureInstructor().equals("0")) {
                allocateSessionsForCourse(course, "Lecture", course.getLectureHours(), 
                                     Integer.parseInt(course.getLectureInstructor()), 
                                     roomAllocations, instructorAllocations, courseAllocations);
            }
            
            if (course.getLabHours() > 0 && !course.getLabInstructor().equals("0")) {
                allocateSessionsForCourse(course, "Lab", course.getLabHours(), 
                                     Integer.parseInt(course.getLabInstructor()), 
                                     roomAllocations, instructorAllocations, courseAllocations);
            }
        }
        
        return timetable;
    }
    
    private void allocateSessionsForCourse(Course course, String sessionType, int hours, 
                                     int instructorId, Map<TimeSlot, Set<Integer>> roomAllocations, 
                                     Map<TimeSlot, Set<Integer>> instructorAllocations, 
                                     Map<TimeSlot, Set<String>> courseAllocations) {
        int sessionsAllocated = 0;
        
        // Find suitable rooms
        List<Classroom> suitableRooms = findSuitableRooms(course, sessionType);
        
        if (suitableRooms.isEmpty()) {
            conflicts.add("No suitable rooms found for " + course.getCode() + " " + sessionType);
            return;
        }
        
        // Vary room sorting strategy based on variant
        int roomStrategy = (strategyVariant / 4) % 3;
        switch (roomStrategy) {
            case 0:
                // Default: smaller rooms first (to maximize utilization)
                suitableRooms.sort(Comparator.comparingInt(Classroom::getCapacity));
                break;
            case 1:
                // Larger rooms first
                suitableRooms.sort(Comparator.comparingInt(Classroom::getCapacity).reversed());
                break;
            case 2:
                // Random order
                Collections.shuffle(suitableRooms, random);
                break;
        }
        
        // Create different order of days based on strategy
        List<String> dayOrder = new ArrayList<>(Arrays.asList(DAYS));
        
        // Different day ordering strategies
        int dayStrategy = (strategyVariant / 12) % 3;
        switch (dayStrategy) {
            case 0:
                // Default order
                break;
            case 1:
                // Reverse order
                Collections.reverse(dayOrder);
                break;
            case 2:
                // Random order
                Collections.shuffle(dayOrder, random);
                break;
        }
        
        // Track sessions per day to balance allocation
        Map<String, Integer> sessionsPerDay = new HashMap<>();
        for (String day : dayOrder) {
            sessionsPerDay.put(day, 0);
        }
        
        // First pass: try to distribute across days
        for (String day : dayOrder) {
            if (sessionsAllocated >= hours) break;
            
            // Create different time slot orderings
            List<String> timeOrder = new ArrayList<>(Arrays.asList(TIME_SLOTS));
            if ((strategyVariant / 36) % 2 == 1) {
                Collections.shuffle(timeOrder, random);
            }
            
            for (String time : timeOrder) {
                if (sessionsAllocated >= hours) break;
                
                TimeSlot slot = new TimeSlot(day, time);
                
                // Check constraints
                if (instructorAllocations.get(slot).contains(instructorId) || 
                    courseAllocations.get(slot).contains(course.getCode())) {
                    continue;
                }
                
                // Try to allocate to a room
                boolean allocated = false;
                for (Classroom room : suitableRooms) {
                    if (!roomAllocations.get(slot).contains(room.getId())) {
                        // Allocate this slot
                        roomAllocations.get(slot).add(room.getId());
                        instructorAllocations.get(slot).add(instructorId);
                        courseAllocations.get(slot).add(course.getCode());
                        
                        timetable.add(new TimetableEntry(day, time, course.getCode(), 
                                                       room.getId(), instructorId, sessionType));
                        
                        sessionsAllocated++;
                        sessionsPerDay.put(day, sessionsPerDay.get(day) + 1);
                        allocated = true;
                        break;
                    }
                }
                
                if (allocated) break; // Move to next day after successful allocation
            }
        }
        
        // Second pass: allocate remaining sessions prioritizing days with fewer allocations
        if (sessionsAllocated < hours) {
            // Sort days by current allocation (least allocated first)
            List<String> sortedDays = new ArrayList<>(dayOrder);
            sortedDays.sort(Comparator.comparingInt(sessionsPerDay::get));
            
            for (String day : sortedDays) {
                if (sessionsAllocated >= hours) break;
                
                List<String> timeOrder = new ArrayList<>(Arrays.asList(TIME_SLOTS));
                Collections.shuffle(timeOrder, random);
                
                for (String time : timeOrder) {
                    if (sessionsAllocated >= hours) break;
                    
                    TimeSlot slot = new TimeSlot(day, time);
                    
                    // Skip if already allocated for this course
                    if (courseAllocations.get(slot).contains(course.getCode())) {
                        continue;
                    }
                    
                    // Skip if instructor is busy
                    if (instructorAllocations.get(slot).contains(instructorId)) {
                        continue;
                    }
                    
                    // Try to find available room
                    for (Classroom room : suitableRooms) {
                        if (!roomAllocations.get(slot).contains(room.getId())) {
                            // Allocate
                            roomAllocations.get(slot).add(room.getId());
                            instructorAllocations.get(slot).add(instructorId);
                            courseAllocations.get(slot).add(course.getCode());
                            
                            timetable.add(new TimetableEntry(day, time, course.getCode(), 
                                                           room.getId(), instructorId, sessionType));
                            
                            sessionsAllocated++;
                            sessionsPerDay.put(day, sessionsPerDay.get(day) + 1);
                            break;
                        }
                    }
                }
            }
        }
        
        if (sessionsAllocated < hours) {
            conflicts.add("Could not allocate all required hours for " + course.getCode() + 
                         " " + sessionType + ". Allocated " + sessionsAllocated + " out of " + hours);
        }
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
}
