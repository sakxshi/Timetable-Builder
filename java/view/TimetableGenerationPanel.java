package view;

import model.*;
import service.TimetableService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class TimetableGenerationPanel extends JPanel {
    private TimetableService service;
    
    private JComboBox<String> departmentCombo, priorityCombo, algorithmCombo;
    private JButton generateButton, downloadButton, saveButton;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private JTextArea conflictsArea;
    
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] TIME_SLOTS = {
        "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 1:00", 
        "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
    };
    
    public TimetableGenerationPanel() {
        service = new TimetableService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel headerLabel = new JLabel("Timetable Generation");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel subtitleLabel = new JLabel("Configure and generate your timetable");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Settings Panel with modern design
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Grid constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Style the labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        
        // Department combo
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(labelFont);
        settingsPanel.add(deptLabel, gbc);
        
        gbc.gridx = 1;
        departmentCombo = new JComboBox<>(new String[]{"All Departments", "CSE", "Mathematics", "Electrical", "Mechanical"});
        styleComboBox(departmentCombo);
        settingsPanel.add(departmentCombo, gbc);
        
        // Priority combo
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel priorityLabel = new JLabel("Prioritize:");
        priorityLabel.setFont(labelFont);
        settingsPanel.add(priorityLabel, gbc);
        
        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(new String[]{"Instructor Availability", "Classroom Utilization", "Student Schedule"});
        styleComboBox(priorityCombo);
        settingsPanel.add(priorityCombo, gbc);
        
        // Algorithm combo
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setFont(labelFont);
        settingsPanel.add(algoLabel, gbc);
        
        gbc.gridx = 1;
        algorithmCombo = new JComboBox<>(new String[]{"Constraint-Based", "Genetic Algorithm", "Greedy Allocation"});
        styleComboBox(algorithmCombo);
        settingsPanel.add(algorithmCombo, gbc);
        
        // Buttons panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        generateButton = createStyledButton("Generate Timetable", new Color(41, 128, 185));
        downloadButton = createStyledButton("Download Timetable", new Color(39, 174, 96));
        saveButton = createStyledButton("Save Timetable", new Color(142, 68, 173));
        
        buttonPanel.add(generateButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(saveButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        settingsPanel.add(buttonPanel, gbc);
        
        // Conflicts Area with better styling
        conflictsArea = new JTextArea(5, 20);
        conflictsArea.setEditable(false);
        conflictsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        conflictsArea.setLineWrap(true);
        conflictsArea.setWrapStyleWord(true);
        conflictsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        conflictsArea.setBackground(new Color(253, 245, 230));
        
        JScrollPane conflictsScrollPane = new JScrollPane(conflictsArea);
        conflictsScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "Conflicts",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(15, 8, 8, 8);
        settingsPanel.add(conflictsScrollPane, gbc);
        
        // Create timetable table with modern styling
        createTimetableTable();
        JScrollPane tableScrollPane = new JScrollPane(timetableTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Style the table header
        JTableHeader header = timetableTable.getTableHeader();
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Layout components
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(settingsPanel, BorderLayout.NORTH);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel tableTitle = new JLabel("Generated Timetable");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        rightPanel.add(tableTitle, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Split pane for responsive layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Set up action listeners
        setupActionListeners();
    }
    
    private void createTimetableTable() {
        // Create column headers with days
        String[] columnNames = new String[DAYS.length + 1];
        columnNames[0] = "Time / Day";
        System.arraycopy(DAYS, 0, columnNames, 1, DAYS.length);
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, TIME_SLOTS.length) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Fill the first column with time slots
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            tableModel.setValueAt(TIME_SLOTS[i], i, 0);
        }
        
        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(80);
        timetableTable.setShowGrid(true);
        timetableTable.setGridColor(new Color(220, 220, 220));
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        
        for (int i = 1; i <= DAYS.length; i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        
        // Set custom renderer for the cells
        timetableTable.setDefaultRenderer(Object.class, new TimetableCellRenderer());
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        
        return button;
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setBackground(Color.WHITE);
    }
    
    private void setupActionListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateTimetable();
            }
        });
        
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TimetableGenerationPanel.this, 
                                           "Timetable downloaded successfully!", 
                                           "Download", 
                                           JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TimetableGenerationPanel.this, 
                                           "Timetable saved successfully!", 
                                           "Save", 
                                           JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    private void generateTimetable() {
        // Show "Generating..." message
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        generateButton.setEnabled(false);
        generateButton.setText("Generating...");
        
        // Clear existing data in table
        clearTable();
        
        // Generate timetable in a separate thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                service.generateTimetable();
                return null;
            }
            
            @Override
            protected void done() {
                // Update UI with generated timetable
                updateTimetableDisplay();
                
                // Update conflicts display
                updateConflictsDisplay();
                
                // Reset button and cursor
                generateButton.setEnabled(true);
                generateButton.setText("Generate Timetable");
                setCursor(Cursor.getDefaultCursor());
            }
        };
        
        worker.execute();
    }
    
    private void clearTable() {
        for (int row = 0; row < TIME_SLOTS.length; row++) {
            for (int col = 1; col <= DAYS.length; col++) {
                tableModel.setValueAt(null, row, col);
            }
        }
    }
    
    private void updateTimetableDisplay() {
        List<TimetableEntry> timetable = service.getTimetable();
        
        for (TimetableEntry entry : timetable) {
            int dayIndex = getDayIndex(entry.getDay());
            int timeIndex = getTimeIndex(entry.getTime());
            
            if (dayIndex >= 0 && timeIndex >= 0) {
                // Create a description of the session
                String courseCode = entry.getCourseCode();
                int roomId = entry.getRoomId();
                int instructorId = entry.getInstructorId();
                String sessionType = entry.getSessionType();
                
                String instructorName = getInstructorName(instructorId);
                String roomName = "Room " + roomId;
                
                TimetableCell cell = new TimetableCell(courseCode, roomName, instructorName, sessionType);
                tableModel.setValueAt(cell, timeIndex, dayIndex + 1);
            }
        }
    }
    
    private void updateConflictsDisplay() {
        List<String> conflicts = service.getConflicts();
        
        conflictsArea.setText("");
        if (conflicts.isEmpty()) {
            conflictsArea.setText("No conflicts detected.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Conflicts detected:\n\n");
            
            for (String conflict : conflicts) {
                sb.append("- ").append(conflict).append("\n");
            }
            
            conflictsArea.setText(sb.toString());
        }
    }
    
    private String getInstructorName(int instructorId) {
        for (Instructor instructor : service.getAllInstructors()) {
            if (instructor.getId() == instructorId) {
                return instructor.getFirstName() + " " + instructor.getLastName();
            }
        }
        return "Unknown";
    }
    
    private int getDayIndex(String day) {
        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equals(day)) {
                return i;
            }
        }
        return -1;
    }
    
    private int getTimeIndex(String time) {
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            if (TIME_SLOTS[i].equals(time)) {
                return i;
            }
        }
        return -1;
    }
    
    // Inner class for representing a timetable cell
    static class TimetableCell {
        private String courseCode;
        private String roomName;
        private String instructorName;
        private String sessionType;
        
        public TimetableCell(String courseCode, String roomName, String instructorName, String sessionType) {
            this.courseCode = courseCode;
            this.roomName = roomName;
            this.instructorName = instructorName;
            this.sessionType = sessionType;
        }
        
        public String getCourseCode() { return courseCode; }
        public String getRoomName() { return roomName; }
        public String getInstructorName() { return instructorName; }
        public String getSessionType() { return sessionType; }
        
        @Override
        public String toString() {
            return courseCode + "\n" + roomName + "\n" + instructorName + "\n" + sessionType;
        }
    }
    
    // Custom cell renderer for the timetable
    class TimetableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                     boolean isSelected, boolean hasFocus, 
                                                     int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof TimetableCell) {
                TimetableCell cell = (TimetableCell) value;
                
                // Create a styled panel for the cell content
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                
                Color bgColor = cell.getSessionType().equals("Lecture") ? 
                              new Color(217, 234, 255) : new Color(255, 234, 217);
                
                panel.setBackground(bgColor);
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                // Create styled components for cell content
                JLabel codeLabel = new JLabel(cell.getCourseCode());
                codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel roomLabel = new JLabel(cell.getRoomName());
                roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel instructorLabel = new JLabel(cell.getInstructorName());
                instructorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                instructorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel typeLabel = new JLabel(cell.getSessionType());
                typeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                typeLabel.setForeground(Color.DARK_GRAY);
                typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                panel.add(codeLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(roomLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(instructorLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(typeLabel);
                
                return panel;
            } else if (column == 0) {
                // Time column
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setBackground(new Color(245, 245, 245));
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setText("");
                setBackground(Color.WHITE);
            }
            
            setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            return this;
        }
    }
}
