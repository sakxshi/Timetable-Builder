package database;

import model.Classroom;
import model.Course;
import model.Instructor;
import model.InstructorCourse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    // -------------------------------
    // CLASSROOMS CSV HANDLING
    // -------------------------------
    // Expected CSV Format:
    // id,capacity,av_support,computers,roomType
    public static List<Classroom> loadClassrooms(String filename) {
        List<Classroom> classrooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read header line and ignore (if applicable)
            if ((line = br.readLine()) != null) {
                // Uncomment the following line if you need to verify header:
                // System.out.println("Header: " + line);
            }
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Trim each field to remove extra spaces
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
            // Write header
            bw.write("id,capacity,av_support,computers,roomType");
            bw.newLine();
            for (Classroom c : classrooms) {
                String line = c.getId() + "," + c.getCapacity() + "," + c.hasAV() + "," + c.getComputers() + "," + c.getRoomType();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------
    // COURSES CSV HANDLING
    // -------------------------------
    // Expected CSV Format:
    // code,subject,domain,students,lecture_hours,lab_hours,lecture_instructor,lab_instructor
    public static List<Course> loadCourses(String filename) {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Skip header
            if ((line = br.readLine()) != null) { }
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String code = parts[0].trim();
                String subject = parts[1].trim();
                String domain = parts[2].trim();
                int students = Integer.parseInt(parts[3].trim());
                int lectureHours = Integer.parseInt(parts[4].trim());
                int labHours = Integer.parseInt(parts[5].trim());
                String lectureInstructor = parts[6].trim();
                String labInstructor = parts.length > 7 ? parts[7].trim() : "";
                // Create Course object (modify constructor as needed)
                Course course = new Course(code, subject, domain, students, lectureHours, labHours, lectureInstructor, labInstructor);
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static void saveCourses(List<Course> courses, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write header
            bw.write("code,subject,domain,students,lecture_hours,lab_hours,lecture_instructor,lab_instructor");
            bw.newLine();
            for (Course c : courses) {
                String line = c.getCode() + "," + c.getSubject() + "," + c.getDomain() + "," +
                        c.getStudents() + "," + c.getLectureHours() + "," + c.getLabHours() + "," +
                        c.getLectureInstructor() + "," + c.getLabInstructor();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------
    // INSTRUCTORS CSV HANDLING
    // -------------------------------
    // Expected CSV Format:
    // id,firstName,lastName,department
    public static List<Instructor> loadInstructors(String filename) {
        List<Instructor> instructors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Skip header
            if ((line = br.readLine()) != null) { }
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
            // Write header
            bw.write("id,firstName,lastName,department");
            bw.newLine();
            for (Instructor i : instructors) {
                String line = i.getId() + "," + i.getFirstName() + "," + i.getLastName() + "," + i.getDepartment();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------
    // INSTRUCTORCOURSE CSV HANDLING
    // -------------------------------
    // Expected CSV Format:
    // instructor_id,course_code,type
    public static List<InstructorCourse> loadInstructorCourses(String filename) {
        List<InstructorCourse> instructorCourses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Skip header
            if ((line = br.readLine()) != null) { }
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
            // Write header
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
