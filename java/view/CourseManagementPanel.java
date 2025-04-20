package view;

import model.Course;
import model.Instructor;
import service.TimetableService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CourseManagementPanel extends JPanel {
    private TimetableService service;
    
    private JTextField codeField, subjectField, domainField, studentsField, lectureHoursField, labHoursField;
    private JComboBox<String> lectureInstructorCombo, labInstructorCombo;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, clearButton;
    
    public CourseManagementPanel() {
        service = new TimetableService();
        initializeUI();
        loadCourseData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Course Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Add/Edit Course"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Grid constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Course Code field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course Code:"), gbc);
        
        gbc.gridx = 1;
        codeField = new JTextField(10);
        formPanel.add(codeField, gbc);
        
        // Subject field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Subject:"), gbc);
        
        gbc.gridx = 1;
        subjectField = new JTextField(10);
        formPanel.add(subjectField, gbc);
        
        // Domain field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Domain:"), gbc);
        
        gbc.gridx = 1;
        domainField = new JTextField(10);
        formPanel.add(domainField, gbc);
        
        // Students field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Students:"), gbc);
        
        gbc.gridx = 1;
        studentsField = new JTextField(10);
        formPanel.add(studentsField, gbc);
        
        // Lecture Hours field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Lecture Hours:"), gbc);
        
        gbc.gridx = 1;
        lectureHoursField = new JTextField(10);
        formPanel.add(lectureHoursField, gbc);
        
        // Lab Hours field
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Lab Hours:"), gbc);
        
        gbc.gridx = 1;
        labHoursField = new JTextField(10);
        formPanel.add(labHoursField, gbc);
        
        // Lecture Instructor combo
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Lecture Instructor:"), gbc);
        
        gbc.gridx = 1;
        lectureInstructorCombo = new JComboBox<>();
        loadInstructorsIntoComboBox(lectureInstructorCombo);
        formPanel.add(lectureInstructorCombo, gbc);
        
        // Lab Instructor combo
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Lab Instructor:"), gbc);
        
        gbc.gridx = 1;
        labInstructorCombo = new JComboBox<>();
        loadInstructorsIntoComboBox(labInstructorCombo);
        labInstructorCombo.addItem("None");
        formPanel.add(labInstructorCombo, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columnNames = {"Code", "Subject", "Domain", "Students", "Lecture Hours", "Lab Hours", "Lecture Instructor", "Lab Instructor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        
        // Add components to main panel
        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners to buttons
        setupActionListeners();
    }
    
    private void loadInstructorsIntoComboBox(JComboBox<String> comboBox) {
        List<Instructor> instructors = service.getAllInstructors();
        for (Instructor instructor : instructors) {
            comboBox.addItem(instructor.getId() + " - " + instructor.getFirstName() + " " + instructor.getLastName());
        }
    }
    
    private void setupActionListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCourse();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCourse();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCourse();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && courseTable.getSelectedRow() != -1) {
                populateForm();
            }
        });
    }
    
    private void loadCourseData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load from service
        for (Course course : service.getAllCourses()) {
            String lectureInstructorName = getInstructorName(course.getLectureInstructor());
            String labInstructorName = course.getLabInstructor().equals("0") ? 
                                     "None" : getInstructorName(course.getLabInstructor());
            
            Object[] row = {
                course.getCode(),
                course.getSubject(),
                course.getDomain(),
                course.getStudents(),
                course.getLectureHours(),
                course.getLabHours(),
                lectureInstructorName,
                labInstructorName
            };
            tableModel.addRow(row);
        }
    }
    
    private String getInstructorName(String instructorId) {
        if (instructorId.equals("0")) return "None";
        
        for (Instructor instructor : service.getAllInstructors()) {
            if (String.valueOf(instructor.getId()).equals(instructorId)) {
                return instructor.getFirstName() + " " + instructor.getLastName();
            }
        }
        return "Unknown";
    }
    
    private void populateForm() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            codeField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            subjectField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            domainField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            studentsField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            lectureHoursField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            labHoursField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            
            // Find instructor in combo box
            selectInstructorInComboBox(lectureInstructorCombo, tableModel.getValueAt(selectedRow, 6).toString());
            selectInstructorInComboBox(labInstructorCombo, tableModel.getValueAt(selectedRow, 7).toString());
        }
    }
    
    private void selectInstructorInComboBox(JComboBox<String> comboBox, String instructorName) {
        if (instructorName.equals("None")) {
            comboBox.setSelectedItem("None");
            return;
        }
        
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            if (item.contains(instructorName)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }
    
    private void clearForm() {
        codeField.setText("");
        subjectField.setText("");
        domainField.setText("");
        studentsField.setText("");
        lectureHoursField.setText("");
        labHoursField.setText("");
        lectureInstructorCombo.setSelectedIndex(0);
        labInstructorCombo.setSelectedItem("None");
        courseTable.clearSelection();
    }
    
    private void addCourse() {
        try {
            String code = codeField.getText();
            String subject = subjectField.getText();
            String domain = domainField.getText();
            int students = Integer.parseInt(studentsField.getText());
            int lectureHours = Integer.parseInt(lectureHoursField.getText());
            int labHours = Integer.parseInt(labHoursField.getText());
            
            String lectureInstructor = lectureInstructorCombo.getSelectedItem().toString();
            String labInstructor = labInstructorCombo.getSelectedItem().toString();
            
            String lectureInstructorId = extractInstructorId(lectureInstructor);
            String labInstructorId = labInstructor.equals("None") ? "0" : extractInstructorId(labInstructor);
            
            Course course = new Course(code, subject, domain, students, lectureHours, labHours, 
                                     lectureInstructorId, labInstructorId);
            service.addCourse(course);
            loadCourseData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Students, Lecture Hours, and Lab Hours", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String extractInstructorId(String instructorString) {
        return instructorString.split(" ")[0];
    }
    
    private void updateCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String code = codeField.getText();
            String subject = subjectField.getText();
            String domain = domainField.getText();
            int students = Integer.parseInt(studentsField.getText());
            int lectureHours = Integer.parseInt(lectureHoursField.getText());
            int labHours = Integer.parseInt(labHoursField.getText());
            
            String lectureInstructor = lectureInstructorCombo.getSelectedItem().toString();
            String labInstructor = labInstructorCombo.getSelectedItem().toString();
            
            String lectureInstructorId = extractInstructorId(lectureInstructor);
            String labInstructorId = labInstructor.equals("None") ? "0" : extractInstructorId(labInstructor);
            
            Course course = new Course(code, subject, domain, students, lectureHours, labHours, 
                                     lectureInstructorId, labInstructorId);
            service.updateCourse(course);
            loadCourseData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Students, Lecture Hours, and Lab Hours", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String code = tableModel.getValueAt(selectedRow, 0).toString();
            service.deleteCourse(code);
            loadCourseData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}