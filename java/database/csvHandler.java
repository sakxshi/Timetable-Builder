package database;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {
    public static List<Classroom> loadClassrooms(String filename) {
        List<Classroom> classrooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            if ((line = br.readLine()) != null) {} // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                int capacity = Integer.parseInt(parts[1].trim());
                boolean avSupport = Boolean.parseBoolean(parts[2].trim());
                int computers = Integer.parseInt(parts[3].trim());
                String roomType = parts[4].trim();
                
                Classroom classroom = new Classroom(id, capacity, avSupport, computers, roomType);
                classrooms.add(classroom);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classrooms;
    }
    
    public static void saveClassrooms(List<Classroom> classrooms, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("id,capacity,av_support,computers,roomType");
            bw.newLine();
            for (Classroom c : classrooms) {
                String line = c.getId() + "," + c.getCapacity() + "," + c.hasAV() + "," + 
                              c.getComputers() + "," + c.getRoomType();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Course> loadCourses(String filename) {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Skip header
            if ((line = br.readLine()) != null) {}
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String code = parts[0].trim();
                String subject = parts[1].trim();
                String domain = parts[2].trim();
                int year = Integer.parseInt(parts[3].trim());
                int students = Integer.parseInt(parts[4].trim());
                int lectureHours = Integer.parseInt(parts[5].trim());
                int labHours = Integer.parseInt(parts[6].trim());
                String lectureInstructor = parts[7].trim();
                String labInstructor = parts.length > 8 ? parts[8].trim() : "0";
                String schedulePattern = parts.length > 9 ? parts[9].trim() : "MWF";
                
                Course course = new Course(code, subject, domain, year, students, 
                                         lectureHours, labHours, lectureInstructor, 
                                         labInstructor, schedulePattern);
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static void saveCourses(List<Course> courses, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("code,subject,domain,year,students,lecture_hours,lab_hours,lecture_instructor,lab_instructor,schedule_pattern");
            bw.newLine();
            for (Course c : courses) {
                String line = c.getCode() + "," + c.getSubject() + "," + c.getDomain() + "," + 
                           c.getYear() + "," + c.getStudents() + "," + c.getLectureHours() + "," + 
                           c.getLabHours() + "," + c.getLectureInstructor() + "," + 
                           c.getLabInstructor() + "," + c.getSchedulePattern();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Instructor> loadInstructors(String filename) {
        List<Instructor> instructors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            if ((line = br.readLine()) != null) {} // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                String department = parts[3].trim();
                
                Instructor instructor = new Instructor(id, firstName, lastName, department);
                instructors.add(instructor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instructors;
    }
    
    public static void saveInstructors(List<Instructor> instructors, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("id,firstName,lastName,department");
            bw.newLine();
            for (Instructor i : instructors) {
                String line = i.getId() + "," + i.getFirstName() + "," + 
                             i.getLastName() + "," + i.getDepartment();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<InstructorCourse> loadInstructorCourses(String filename) {
        List<InstructorCourse> instructorCourses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            if ((line = br.readLine()) != null) {} // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int instructorId = Integer.parseInt(parts[0].trim());
                String courseCode = parts[1].trim();
                String type = parts[2].trim();
                
                InstructorCourse ic = new InstructorCourse(instructorId, courseCode, type);
                instructorCourses.add(ic);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instructorCourses;
    }
    
    public static void saveInstructorCourses(List<InstructorCourse> instructorCourses, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("instructor_id,course_code,type");
            bw.newLine();
            for (InstructorCourse ic : instructorCourses) {
                String line = ic.getInstructorId() + "," + ic.getCourseCode() + "," + ic.getType();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}