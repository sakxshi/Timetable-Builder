package view;

import model.*;
import service.TimetableService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import static view.UIConstants.*;
import util.IconFactory;

public class TimetableGenerationPanel extends JPanel {
    private TimetableService service;
    private JComboBox<String> departmentCombo, priorityCombo, algorithmCombo;
    private JButton generateButton, downloadButton, saveButton;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private JTextArea conflictsArea;
    
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private static final String[] TIME_SLOTS = {"9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 1:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"};

    public TimetableGenerationPanel() {
        service = new TimetableService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(SECTION_BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel headerLabel = new JLabel("Timetable Generation");
        headerLabel.setFont(HEADING_FONT);

        JLabel subtitleLabel = new JLabel("Configure and generate your timetable");
        subtitleLabel.setFont(NORMAL_FONT);
        subtitleLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(WHITE_COLOR);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Generation Settings",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                SUBHEADING_FONT
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setOpaque(false);

        JPanel departmentPanel = new JPanel(new BorderLayout());
        departmentPanel.setOpaque(false);
        JLabel departmentLabel = new JLabel("Department");
        departmentLabel.setFont(SMALL_FONT);
        departmentCombo = new JComboBox<>(new String[]{"All Departments", "CSE", "Mathematics", "Electrical", "Mechanical"});
        departmentCombo.setFont(NORMAL_FONT);
        departmentPanel.add(departmentLabel, BorderLayout.NORTH);
        departmentPanel.add(departmentCombo, BorderLayout.CENTER);

        JPanel priorityPanel = new JPanel(new BorderLayout());
        priorityPanel.setOpaque(false);
        JLabel priorityLabel = new JLabel("Prioritize");
        priorityLabel.setFont(SMALL_FONT);
        priorityCombo = new JComboBox<>(new String[]{"Instructor Availability", "Classroom Utilization", "Student Schedule"});
        priorityCombo.setFont(NORMAL_FONT);
        priorityPanel.add(priorityLabel, BorderLayout.NORTH);
        priorityPanel.add(priorityCombo, BorderLayout.CENTER);

        JPanel algorithmPanel = new JPanel(new BorderLayout());
        algorithmPanel.setOpaque(false);
        JLabel algorithmLabel = new JLabel("Algorithm");
        algorithmLabel.setFont(SMALL_FONT);
        algorithmCombo = new JComboBox<>(new String[]{"Constraint-Based", "Genetic Algorithm", "Greedy Allocation"});
        algorithmCombo.setFont(NORMAL_FONT);
        algorithmPanel.add(algorithmLabel, BorderLayout.NORTH);
        algorithmPanel.add(algorithmCombo, BorderLayout.CENTER);

        fieldsPanel.add(departmentPanel);
        fieldsPanel.add(priorityPanel);
        fieldsPanel.add(algorithmPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        settingsPanel.add(fieldsPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        generateButton = createStyledButton("Generate Timetable", new Color(41, 128, 185));
generateButton.setIcon(IconFactory.createClockIcon());
        
        downloadButton = createStyledButton("Download Timetable", new Color(39, 174, 96));
downloadButton.setIcon(IconFactory.createDownloadIcon());
        
saveButton = createStyledButton("Save Timetable", new Color(142, 68, 173));
saveButton.setIcon(IconFactory.createSaveIcon());

        buttonPanel.add(generateButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        settingsPanel.add(buttonPanel, gbc);

        conflictsArea = new JTextArea(5, 20);
        conflictsArea.setEditable(false);
        conflictsArea.setFont(NORMAL_FONT);
        conflictsArea.setLineWrap(true);
        conflictsArea.setWrapStyleWord(true);
        conflictsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        conflictsArea.setBackground(new Color(253, 245, 230));

        JScrollPane conflictsScrollPane = new JScrollPane(conflictsArea);
        conflictsScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "Conflicts",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            SUBHEADING_FONT
        ));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(15, 8, 8, 8);
        settingsPanel.add(conflictsScrollPane, gbc);

        createTimetableTable();

        JScrollPane tableScrollPane = new JScrollPane(timetableTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JTableHeader header = timetableTable.getTableHeader();
        header.setBackground(new Color(41, 128, 185));
        header.setForeground(WHITE_COLOR);
        header.setFont(SMALL_FONT);

        JPanel timetablePanel = new JPanel(new BorderLayout());
        timetablePanel.setBackground(WHITE_COLOR);
        timetablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Generated Timetable",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                SUBHEADING_FONT
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        timetablePanel.add(tableScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(settingsPanel);
        splitPane.setRightComponent(timetablePanel);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(10);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        setupActionListeners();
    }

    private void createTimetableTable() {
        String[] columnNames = new String[DAYS.length + 1];
        columnNames[0] = "Time / Day";
        System.arraycopy(DAYS, 0, columnNames, 1, DAYS.length);

        tableModel = new DefaultTableModel(columnNames, TIME_SLOTS.length) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (int i = 0; i < TIME_SLOTS.length; i++) {
            tableModel.setValueAt(TIME_SLOTS[i], i, 0);
        }

        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(80);
        timetableTable.setShowGrid(true);
        timetableTable.setGridColor(new Color(220, 220, 220));
        timetableTable.setFont(NORMAL_FONT);

        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i <= DAYS.length; i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(200);
        }

        timetableTable.setDefaultRenderer(Object.class, new TimetableCellRenderer());
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
                    "Timetable downloaded successfully!", "Download", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TimetableGenerationPanel.this,
                    "Timetable saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void generateTimetable() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        generateButton.setEnabled(false);
        generateButton.setText("Generating...");
        generateButton.setIcon(IconFactory.createRefreshIcon());
        
        clearTable();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                service.generateTimetable();
                return null;
            }

            @Override
            protected void done() {
                updateTimetableDisplay();
                updateConflictsDisplay();
                generateButton.setEnabled(true);
                generateButton.setText("Generate Timetable");
                generateButton.setIcon(IconFactory.createClockIcon());
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

        public String getCourseCode() {
            return courseCode;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getInstructorName() {
            return instructorName;
        }

        public String getSessionType() {
            return sessionType;
        }

        @Override
        public String toString() {
            return courseCode + "\n" + roomName + "\n" + instructorName + "\n" + sessionType;
        }
    }

    class TimetableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof TimetableCell) {
                TimetableCell cell = (TimetableCell) value;
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                
                Color bgColor = cell.getSessionType().equals("Lecture") 
                    ? new Color(217, 234, 255) 
                    : new Color(255, 234, 217);
                panel.setBackground(bgColor);
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
