package service;

import controller.TimetableGenerator;
import database.CSVHandler;
import model.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TimetableService {
    private static final String CLASSROOMS_FILE = "resources/classrooms.csv";
    private static final String COURSES_FILE = "resources/courses.csv";
    private static final String INSTRUCTORS_FILE = "resources/instructors.csv";
    private static final String INSTRUCTOR_COURSE_FILE = "resources/instructorCourse.csv";
    
    private List<Classroom> classrooms;
    private List<Course> courses;
    private List<Instructor> instructors;
    private List<InstructorCourse> instructorCourses;
    private List<TimetableEntry> timetable;
    private List<String> conflicts;
    
    private static final int MAX_ATTEMPTS = 100;
    
    public TimetableService() {
        loadData();
    }
    
    public void loadData() {
        classrooms = CSVHandler.loadClassrooms(CLASSROOMS_FILE);
        courses = CSVHandler.loadCourses(COURSES_FILE);
        instructors = CSVHandler.loadInstructors(INSTRUCTORS_FILE);
        instructorCourses = CSVHandler.loadInstructorCourses(INSTRUCTOR_COURSE_FILE);
    }
    
    public void saveData() {
        CSVHandler.saveClassrooms(classrooms, CLASSROOMS_FILE);
        CSVHandler.saveCourses(courses, COURSES_FILE);
        CSVHandler.saveInstructors(instructors, INSTRUCTORS_FILE);
        CSVHandler.saveInstructorCourses(instructorCourses, INSTRUCTOR_COURSE_FILE);
    }
    
    public void generateTimetable() {
        List<TimetableEntry> bestTimetable = null;
        List<String> fewestConflicts = null;
        int fewestConflictCount = Integer.MAX_VALUE;
        
        System.out.println("Starting timetable generation with up to " + MAX_ATTEMPTS + " attempts...");
        
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            TimetableGenerator generator = new TimetableGenerator(classrooms, courses, instructors, instructorCourses);
            generator.setRandomSeed(System.currentTimeMillis() + attempt);
            generator.setStrategyVariant(attempt);
            
            List<TimetableEntry> currentTimetable = generator.generateTimetable();
            List<String> currentConflicts = generator.getConflicts();
            
            System.out.println("Attempt " + (attempt + 1) + ": Found " + currentConflicts.size() + " conflicts");
            
            if (currentConflicts.isEmpty()) {
                timetable = currentTimetable;
                conflicts = currentConflicts;
                System.out.println("Success! Found conflict-free timetable after " + (attempt + 1) + " attempts");
                return;
            }
            
            if (currentConflicts.size() < fewestConflictCount) {
                fewestConflictCount = currentConflicts.size();
                bestTimetable = new ArrayList<>(currentTimetable);
                fewestConflicts = new ArrayList<>(currentConflicts);
                System.out.println("New best solution found with " + fewestConflictCount + " conflicts at attempt " + (attempt + 1));
            }
        }
        
        timetable = bestTimetable;
        conflicts = fewestConflicts;
        System.out.println("Could not find conflict-free timetable after " + MAX_ATTEMPTS + 
                          " attempts. Using best solution with " + conflicts.size() + " conflicts");
    }
    
    public void generateComprehensiveTimetable() {
    System.out.println("Starting comprehensive timetable generation...");
    
    TimetableGenerator generator = new TimetableGenerator(classrooms, courses, instructors, instructorCourses);
    timetable = generator.generateTimetable();
    conflicts = generator.getConflicts();
    
    System.out.println("Timetable generation complete with " + timetable.size() + " entries");
}
    
    public void saveTimetableToCSV() {
        String filename = "resources/timetable.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("day,time,course_code,room_id,instructor_id,session_type");
            bw.newLine();
            
            for (TimetableEntry entry : timetable) {
                String line = entry.getDay() + "," + entry.getTime() + "," + 
                             entry.getCourseCode() + "," + entry.getRoomId() + "," + 
                             entry.getInstructorId() + "," + entry.getSessionType();
                bw.write(line);
                bw.newLine();
            }
            
            System.out.println("Timetable saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving timetable: " + e.getMessage());
        }
    }
    
    // Other methods for managing classrooms, courses, instructors, etc.
    public List<Classroom> getAllClassrooms() {
        return classrooms;
    }
    
    public void addClassroom(Classroom classroom) {
        classrooms.add(classroom);
        saveData();
    }
    
    public void updateClassroom(Classroom classroom) {
        for (int i = 0; i < classrooms.size(); i++) {
            if (classrooms.get(i).getId() == classroom.getId()) {
                classrooms.set(i, classroom);
                break;
            }
        }
        saveData();
    }
    
    public void deleteClassroom(int id) {
        classrooms.removeIf(c -> c.getId() == id);
        saveData();
    }
    
    public List<Course> getAllCourses() {
        return courses;
    }
    
    public void addCourse(Course course) {
        courses.add(course);
        saveData();
    }
    
    public void updateCourse(Course course) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCode().equals(course.getCode())) {
                courses.set(i, course);
                break;
            }
        }
        saveData();
    }
    
    public void deleteCourse(String code) {
        courses.removeIf(c -> c.getCode().equals(code));
        saveData();
    }
    
    public List<Instructor> getAllInstructors() {
        return instructors;
    }
    
    public void addInstructor(Instructor instructor) {
        instructors.add(instructor);
        saveData();
    }
    
    public void updateInstructor(Instructor instructor) {
        for (int i = 0; i < instructors.size(); i++) {
            if (instructors.get(i).getId() == instructor.getId()) {
                instructors.set(i, instructor);
                break;
            }
        }
        saveData();
    }
    
    public void deleteInstructor(int id) {
        instructors.removeIf(i -> i.getId() == id);
        saveData();
    }
    
    public List<TimetableEntry> getTimetable() {
        return timetable;
    }
    
    public List<String> getConflicts() {
        return conflicts;
    }
}
