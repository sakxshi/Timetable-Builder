package view;

import model.Course;
import model.Instructor;
import service.TimetableService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import static view.UIConstants.*;
import java.util.List;
import util.IconFactory;

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
        setBackground(SECTION_BG_COLOR);

        JLabel headerLabel = new JLabel("Course Management");
        headerLabel.setFont(HEADING_FONT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Add New Course",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                SUBHEADING_FONT
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setOpaque(false);

        JPanel codeFieldPanel = new JPanel(new BorderLayout());
        codeFieldPanel.setOpaque(false);
        JLabel codeLabel = new JLabel("Course Code");
        codeLabel.setFont(SMALL_FONT);
        codeField = new JTextField(10);
        styleTextField(codeField);
        codeFieldPanel.add(codeLabel, BorderLayout.NORTH);
        codeFieldPanel.add(codeField, BorderLayout.CENTER);

        JPanel subjectFieldPanel = new JPanel(new BorderLayout());
        subjectFieldPanel.setOpaque(false);
        JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setFont(SMALL_FONT);
        subjectField = new JTextField(10);
        styleTextField(subjectField);
        subjectFieldPanel.add(subjectLabel, BorderLayout.NORTH);
        subjectFieldPanel.add(subjectField, BorderLayout.CENTER);

        JPanel domainFieldPanel = new JPanel(new BorderLayout());
        domainFieldPanel.setOpaque(false);
        JLabel domainLabel = new JLabel("Domain");
        domainLabel.setFont(SMALL_FONT);
        domainField = new JTextField(10);
        styleTextField(domainField);
        domainFieldPanel.add(domainLabel, BorderLayout.NORTH);
        domainFieldPanel.add(domainField, BorderLayout.CENTER);

        JPanel studentsFieldPanel = new JPanel(new BorderLayout());
        studentsFieldPanel.setOpaque(false);
        JLabel studentsLabel = new JLabel("Students");
        studentsLabel.setFont(SMALL_FONT);
        studentsField = new JTextField(10);
        styleTextField(studentsField);
        studentsFieldPanel.add(studentsLabel, BorderLayout.NORTH);
        studentsFieldPanel.add(studentsField, BorderLayout.CENTER);

        JPanel lectureHoursFieldPanel = new JPanel(new BorderLayout());
        lectureHoursFieldPanel.setOpaque(false);
        JLabel lectureHoursLabel = new JLabel("Lecture Hours");
        lectureHoursLabel.setFont(SMALL_FONT);
        lectureHoursField = new JTextField(10);
        styleTextField(lectureHoursField);
        lectureHoursFieldPanel.add(lectureHoursLabel, BorderLayout.NORTH);
        lectureHoursFieldPanel.add(lectureHoursField, BorderLayout.CENTER);

        JPanel labHoursFieldPanel = new JPanel(new BorderLayout());
        labHoursFieldPanel.setOpaque(false);
        JLabel labHoursLabel = new JLabel("Lab Hours");
        labHoursLabel.setFont(SMALL_FONT);
        labHoursField = new JTextField(10);
        styleTextField(labHoursField);
        labHoursFieldPanel.add(labHoursLabel, BorderLayout.NORTH);
        labHoursFieldPanel.add(labHoursField, BorderLayout.CENTER);

        JPanel lectureInstructorPanel = new JPanel(new BorderLayout());
        lectureInstructorPanel.setOpaque(false);
        JLabel lectureInstructorLabel = new JLabel("Lecture Instructor");
        lectureInstructorLabel.setFont(SMALL_FONT);
        lectureInstructorCombo = new JComboBox<>();
        lectureInstructorCombo.setFont(NORMAL_FONT);
        loadInstructorsIntoComboBox(lectureInstructorCombo);
        lectureInstructorPanel.add(lectureInstructorLabel, BorderLayout.NORTH);
        lectureInstructorPanel.add(lectureInstructorCombo, BorderLayout.CENTER);

        JPanel labInstructorPanel = new JPanel(new BorderLayout());
        labInstructorPanel.setOpaque(false);
        JLabel labInstructorLabel = new JLabel("Lab Instructor");
        labInstructorLabel.setFont(SMALL_FONT);
        labInstructorCombo = new JComboBox<>();
        labInstructorCombo.setFont(NORMAL_FONT);
        loadInstructorsIntoComboBox(labInstructorCombo);
        labInstructorCombo.addItem("None");
        labInstructorPanel.add(labInstructorLabel, BorderLayout.NORTH);
        labInstructorPanel.add(labInstructorCombo, BorderLayout.CENTER);

        fieldsPanel.add(codeFieldPanel);
        fieldsPanel.add(subjectFieldPanel);
        fieldsPanel.add(domainFieldPanel);
        fieldsPanel.add(studentsFieldPanel);
        fieldsPanel.add(lectureHoursFieldPanel);
        fieldsPanel.add(labHoursFieldPanel);
        fieldsPanel.add(lectureInstructorPanel);
        fieldsPanel.add(labInstructorPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(fieldsPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        addButton = createStyledButton("Add Course", PRIMARY_COLOR);
addButton.setIcon(IconFactory.createPlusIcon());
        updateButton = createStyledButton("Update", SECONDARY_COLOR);
        deleteButton = createStyledButton("Delete", new Color(192, 57, 43));
        clearButton = createStyledButton("Clear", new Color(108, 117, 125));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        String[] columnNames = {"Code", "Subject", "Domain", "Students", "Lecture Hours", "Lab Hours", "Lecture Instructor", "Lab Instructor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setFont(NORMAL_FONT);
        courseTable.setRowHeight(40);
        courseTable.setShowGrid(true);
        courseTable.setGridColor(new Color(230, 230, 230));

        JTableHeader header = courseTable.getTableHeader();
        header.setFont(SMALL_FONT);
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(HEADING_COLOR);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 20));
        mainContentPanel.setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(formPanel, BorderLayout.NORTH);
        mainContentPanel.add(tablePanel, BorderLayout.CENTER);

        add(headerLabel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);

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
        tableModel.setRowCount(0);
        for (Course course : service.getAllCourses()) {
            String lectureInstructorName = getInstructorName(course.getLectureInstructor());
            String labInstructorName = course.getLabInstructor().equals("0") ? "None" : getInstructorName(course.getLabInstructor());
            
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
            
            Course course = new Course(code, subject, domain, students, lectureHours, labHours, lectureInstructorId, labInstructorId);
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
            
            Course course = new Course(code, subject, domain, students, lectureHours, labHours, lectureInstructorId, labInstructorId);
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
