package view;

import model.*;
import service.TimetableService;
import controller.TimetableGenerator;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.File;
import static view.UIConstants.*;
import util.IconFactory;


public class TimetableGenerationPanel extends JPanel {
    private TimetableService service;
    private JComboBox<String> domainCombo, yearCombo;
    private JButton generateButton, downloadButton, saveButton;
    private JTabbedPane timetableTabs;
    private JTextArea conflictsArea;
    private Map<String, Map<Integer, JTable>> domainYearTables;
    private JTabbedPane viewTabs;
private JPanel weeklyViewPanel;
private JPanel tableViewPanel;
private JTable subjectsTable;
private DefaultTableModel subjectsTableModel;

    
    public TimetableGenerationPanel() {
        service = new TimetableService();
        domainYearTables = new HashMap<>();
        initializeUI();
    }
    
    private void initializeUI() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    setBackground(SECTION_BG_COLOR);
    
    // Header section
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
    
    JLabel headerLabel = new JLabel("College Timetable Generator");
    headerLabel.setFont(HEADING_FONT);
    
    JLabel subtitleLabel = new JLabel("Generate comprehensive timetables for all departments and years");
    subtitleLabel.setFont(NORMAL_FONT);
    subtitleLabel.setForeground(Color.DARK_GRAY);
    
    headerPanel.add(headerLabel, BorderLayout.NORTH);
    headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
    
    // Initialize timetableTabs first
    timetableTabs = new JTabbedPane();
    timetableTabs.setFont(NORMAL_FONT);
    
    // Initialize panels for dual view
    weeklyViewPanel = new JPanel(new BorderLayout());
    tableViewPanel = new JPanel(new BorderLayout());
    
    // Add timetableTabs to weekly view panel
    weeklyViewPanel.add(timetableTabs, BorderLayout.CENTER);
    
    // Setup table view
    setupTableView();
    
    // Create tabbed pane for views
    viewTabs = new JTabbedPane();
    viewTabs.setFont(NORMAL_FONT);
    viewTabs.addTab("Weekly View", weeklyViewPanel);
    viewTabs.addTab("Table View", tableViewPanel);
    
    // Create timetable panel and add viewTabs to it
    JPanel timetablePanel = new JPanel(new BorderLayout());
    timetablePanel.setBackground(WHITE_COLOR);
    timetablePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "Department Timetables",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            SUBHEADING_FONT
        ),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    timetablePanel.add(viewTabs, BorderLayout.CENTER);
    
    // Settings panel
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
    
    // Filter options
    JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    fieldsPanel.setOpaque(false);
    
    JPanel domainPanel = new JPanel(new BorderLayout());
    domainPanel.setOpaque(false);
    JLabel domainLabel = new JLabel("Filter Domain");
    domainLabel.setFont(SMALL_FONT);
    domainCombo = new JComboBox<>(new String[]{"All Domains", "Computer Science", "Electrical", "Mathematics", "Civil"});
    domainCombo.setFont(NORMAL_FONT);
    domainPanel.add(domainLabel, BorderLayout.NORTH);
    domainPanel.add(domainCombo, BorderLayout.CENTER);
    
    JPanel yearPanel = new JPanel(new BorderLayout());
    yearPanel.setOpaque(false);
    JLabel yearLabel = new JLabel("Filter Year");
    yearLabel.setFont(SMALL_FONT);
    yearCombo = new JComboBox<>(new String[]{"All Years", "1", "2", "3", "4"});
    yearCombo.setFont(NORMAL_FONT);
    yearPanel.add(yearLabel, BorderLayout.NORTH);
    yearPanel.add(yearCombo, BorderLayout.CENTER);
    
    fieldsPanel.add(domainPanel);
    fieldsPanel.add(yearPanel);
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    settingsPanel.add(fieldsPanel, gbc);
    
    // Buttons
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
    
    gbc.gridy = 1;
    gbc.insets = new Insets(15, 8, 8, 8);
    settingsPanel.add(buttonPanel, gbc);
    
    // Conflicts area
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
    
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    settingsPanel.add(conflictsScrollPane, gbc);
    
    // Layout components in splitPane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(settingsPanel);
    splitPane.setRightComponent(timetablePanel);
    splitPane.setDividerLocation(400);
    splitPane.setDividerSize(10);
    splitPane.setBorder(null);
    splitPane.setOpaque(false);
    
    // Add to main panel
    add(headerPanel, BorderLayout.NORTH);
    add(splitPane, BorderLayout.CENTER);
    
    // Setup event listeners
    setupActionListeners();
}

    private void setupTableView() {
    String[] columnNames = {"Domain", "Year", "Course Code", "Subject", "Day", "Time", "Room", "Instructor", "Session Type"};
    subjectsTableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    subjectsTable = new JTable(subjectsTableModel);
    subjectsTable.setFont(NORMAL_FONT);
    subjectsTable.setRowHeight(30);
    subjectsTable.setShowGrid(true);
    subjectsTable.setGridColor(new Color(220, 220, 220));
    
    JTableHeader header = subjectsTable.getTableHeader();
    header.setFont(SMALL_FONT);
    header.setBackground(new Color(240, 240, 240));
    header.setForeground(HEADING_COLOR);
    
    // Add sorting capability
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(subjectsTableModel);
    subjectsTable.setRowSorter(sorter);
    
    JScrollPane scrollPane = new JScrollPane(subjectsTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    
    tableViewPanel.add(scrollPane, BorderLayout.CENTER);
}

    private void setupActionListeners() {
        generateButton.addActionListener(e -> generateTimetable());
        downloadButton.addActionListener(e -> downloadTimetable());
        saveButton.addActionListener(e -> saveTimetable());
        domainCombo.addActionListener(e -> filterTimetables());
        yearCombo.addActionListener(e -> filterTimetables());
    }
    
    private void generateTimetable() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        generateButton.setEnabled(false);
        generateButton.setText("Generating...");
        generateButton.setIcon(IconFactory.createRefreshIcon());
        
        // Clear existing timetable displays
        timetableTabs.removeAll();
        domainYearTables.clear();
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.generateComprehensiveTimetable();
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
    
    private void updateTimetableDisplay() {
    List<TimetableEntry> timetable = service.getTimetable();
    
    // Group entries for weekly view
    Map<String, Map<Integer, List<TimetableEntry>>> domainYearEntries = new HashMap<>();
    Map<String, Set<Integer>> domainsAndYears = new HashMap<>();
    
    // Clear table view
    subjectsTableModel.setRowCount(0);
    
    for (TimetableEntry entry : timetable) {
        String courseCode = entry.getCourseCode();
        Course course = findCourseByCode(courseCode);
        
        if (course != null) {
            String domain = course.getDomain();
            int year = course.getYear();
            String subject = course.getSubject();
            
            // Update domains and years map for weekly view
            if (!domainsAndYears.containsKey(domain)) {
                domainsAndYears.put(domain, new HashSet<>());
            }
            domainsAndYears.get(domain).add(year);
            
            if (!domainYearEntries.containsKey(domain)) {
                domainYearEntries.put(domain, new HashMap<>());
            }
            
            Map<Integer, List<TimetableEntry>> yearEntries = domainYearEntries.get(domain);
            if (!yearEntries.containsKey(year)) {
                yearEntries.put(year, new ArrayList<>());
            }
            
            yearEntries.get(year).add(entry);
            
            // Add to table view
            String day = entry.getDay();
            String time = entry.getTime();
            String roomName = "Room " + entry.getRoomId();
            String instructorName = getInstructorName(entry.getInstructorId());
            String sessionType = entry.getSessionType();
            
            Object[] row = {domain, year, courseCode, subject, day, time, roomName, instructorName, sessionType};
            subjectsTableModel.addRow(row);
        }
    }
    
    // Update weekly view
    timetableTabs.removeAll();
    domainYearTables.clear();
    
    for (String domain : domainsAndYears.keySet()) {
        JTabbedPane yearTabs = new JTabbedPane();
        yearTabs.setFont(NORMAL_FONT);
        
        Map<Integer, JTable> yearTableMap = new HashMap<>();
        domainYearTables.put(domain, yearTableMap);
        
        Set<Integer> years = domainsAndYears.get(domain);
        for (int year : years) {
            JTable yearTable = createTimetableTable();
            yearTableMap.put(year, yearTable);
            
            fillTimetableTable(yearTable, domainYearEntries.get(domain).get(year));
            
            JScrollPane scrollPane = new JScrollPane(yearTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            
            yearTabs.addTab("Year " + year, scrollPane);
        }
        
        timetableTabs.addTab(domain, yearTabs);
    }
}

    
    private Course findCourseByCode(String code) {
        for (Course course : service.getAllCourses()) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }
    
    private JTable createTimetableTable() {
    // Create column names: first column for time slots, rest for days
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    String[] columnNames = new String[days.length + 1];
    columnNames[0] = "Time / Day";
    System.arraycopy(days, 0, columnNames, 1, days.length);
    
    // Use only lecture time slots (1-hour blocks)
    String[] timeSlots = TimetableGenerator.getLectureTimeSlots();
    
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, timeSlots.length) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    // Set time slots in first column
    for (int i = 0; i < timeSlots.length; i++) {
        tableModel.setValueAt(timeSlots[i], i, 0);
    }
    
    JTable table = new JTable(tableModel);
    table.setRowHeight(80);
    table.setShowGrid(true);
    table.setGridColor(new Color(220, 220, 220));
    table.setFont(NORMAL_FONT);
    
    // Set column widths
    table.getColumnModel().getColumn(0).setPreferredWidth(150);
    for (int i = 1; i <= days.length; i++) {
        table.getColumnModel().getColumn(i).setPreferredWidth(200);
    }
    
    // Set custom renderer for cells
    table.setDefaultRenderer(Object.class, new TimetableCellRenderer());
    
    return table;
}

    
    private void fillTimetableTable(JTable table, List<TimetableEntry> entries) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    
    // Clear existing content (except first column)
    for (int row = 0; row < model.getRowCount(); row++) {
        for (int col = 1; col < model.getColumnCount(); col++) {
            model.setValueAt(null, row, col);
        }
    }
    
    // Fill in timetable entries
    for (TimetableEntry entry : entries) {
        String day = entry.getDay();
        String time = entry.getTime();
        String courseCode = entry.getCourseCode();
        int roomId = entry.getRoomId();
        int instructorId = entry.getInstructorId();
        String sessionType = entry.getSessionType();
        
        // Find column index for day
        int dayIndex = -1;
        for (int col = 1; col < model.getColumnCount(); col++) {
            if (model.getColumnName(col).equals(day)) {
                dayIndex = col;
                break;
            }
        }
        
        // Find row index for starting time
        int startRowIndex = -1;
        for (int row = 0; row < model.getRowCount(); row++) {
            String cellTime = (String) model.getValueAt(row, 0);
            if (cellTime != null && time.startsWith(cellTime.split(" - ")[0])) {
                startRowIndex = row;
                break;
            }
        }
        
        if (dayIndex > 0 && startRowIndex >= 0) {
            String instructorName = getInstructorName(instructorId);
            String roomName = "Room " + roomId;
            
            TimetableCell cell = new TimetableCell(courseCode, roomName, instructorName, sessionType);
            model.setValueAt(cell, startRowIndex, dayIndex);
            
            // For labs, fill additional cells based on duration
            if (sessionType.equals("Lab")) {
                // Calculate duration in hours
                int duration = calculateDuration(time);
                
                // Fill additional cells for the lab's duration
                for (int i = 1; i < duration && (startRowIndex + i) < model.getRowCount(); i++) {
                    // Create a continuation cell
                    TimetableCell contCell = new TimetableCell(
                        courseCode, roomName, instructorName, sessionType + " (cont.)"
                    );
                    model.setValueAt(contCell, startRowIndex + i, dayIndex);
                }
            }
        }
    }
}

private int calculateDuration(String timeSlot) {
    try {
        String[] parts = timeSlot.split(" - ");
        String startTime = parts[0];
        String endTime = parts[1];
        
        // Parse hours
        int startHour = parseHour(startTime);
        int endHour = parseHour(endTime);
        
        // Calculate duration
        int duration = endHour - startHour;
        if (duration <= 0) {
            duration += 12; // Handle cases crossing noon
        }
        return duration;
    } catch (Exception e) {
        // Default to 1 hour if parsing fails
        return 1;
    }
}

private int parseHour(String timeStr) {
    try {
        timeStr = timeStr.trim();
        
        // Handle different time formats
        if (timeStr.contains(":")) {
            return Integer.parseInt(timeStr.split(":")[0]);
        } else {
            return Integer.parseInt(timeStr);
        }
    } catch (Exception e) {
        return 0;
    }
}


// Helper method to calculate duration in hours


    
    private String getInstructorName(int instructorId) {
        for (Instructor instructor : service.getAllInstructors()) {
            if (instructor.getId() == instructorId) {
                return instructor.getFirstName() + " " + instructor.getLastName();
            }
        }
        return "Unknown";
    }
    
    private void updateConflictsDisplay() {
        List<String> conflicts = service.getConflicts();
        conflictsArea.setText("");
        
        if (conflicts.isEmpty()) {
            conflictsArea.setText("No conflicts detected. The timetable has been generated successfully!");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Conflicts detected:\n\n");
            
            for (String conflict : conflicts) {
                sb.append("• ").append(conflict).append("\n");
            }
            
            conflictsArea.setText(sb.toString());
        }
    }
    
    private void filterTimetables() {
    String selectedDomain = (String) domainCombo.getSelectedItem();
    String selectedYear = (String) yearCombo.getSelectedItem();
    
    // Default to showing all if "All" is selected
    boolean showAllDomains = "All Domains".equals(selectedDomain);
    boolean showAllYears = "All Years".equals(selectedYear);
    
    // Filter the weekly view
    if (timetableTabs.getTabCount() > 0) {
        timetableTabs.removeAll();
        
        for (String domain : domainYearTables.keySet()) {
            if (!showAllDomains && !domain.equals(selectedDomain)) {
                continue;
            }
            
            JTabbedPane yearTabs = new JTabbedPane();
            yearTabs.setFont(NORMAL_FONT);
            
            Map<Integer, JTable> yearTableMap = domainYearTables.get(domain);
            for (int year : yearTableMap.keySet()) {
                if (!showAllYears && year != Integer.parseInt(selectedYear)) {
                    continue;
                }
                
                JTable yearTable = yearTableMap.get(year);
                JScrollPane scrollPane = new JScrollPane(yearTable);
                scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
                
                yearTabs.addTab("Year " + year, scrollPane);
            }
            
            if (yearTabs.getTabCount() > 0) {
                timetableTabs.addTab(domain, yearTabs);
            }
        }
    }
    
    // Filter the table view
    TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) subjectsTable.getRowSorter();
    List<RowFilter<Object, Object>> filters = new ArrayList<>();
    
    if (!showAllDomains) {
        filters.add(RowFilter.regexFilter("^" + selectedDomain + "$", 0)); // Domain column
    }
    
    if (!showAllYears) {
        filters.add(RowFilter.regexFilter("^" + selectedYear + "$", 1)); // Year column
    }
    
    if (!filters.isEmpty()) {
        sorter.setRowFilter(RowFilter.andFilter(filters));
    } else {
        sorter.setRowFilter(null);
    }
}

    
    private void downloadTimetable() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Timetable");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".csv")) {
                file = new File(path + ".csv");
            }
            
            try {
                service.saveTimetableToCSV();
                JOptionPane.showMessageDialog(this, 
                    "Timetable successfully exported to " + file.getName(), 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting timetable: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveTimetable() {
        try {
            service.saveTimetableToCSV();
            JOptionPane.showMessageDialog(this, 
                "Timetable successfully saved", 
                "Save Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving timetable: " + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom cell renderer and helper class for timetable cells
    static class TimetableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
                                                 boolean isSelected, boolean hasFocus, 
                                                 int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (value instanceof TimetableCell) {
            TimetableCell cell = (TimetableCell) value;
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            // Color coding by session type
            Color bgColor = cell.getSessionType().equals("Lecture") ? 
                new Color(217, 234, 255) : new Color(255, 234, 217);
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
            
            // Check if this is a continuing cell from the row above
            boolean isContinuation = false;
            if (row > 0 && cell.getSessionType().equals("Lab")) {
                Object aboveCell = table.getValueAt(row - 1, column);
                if (aboveCell instanceof TimetableCell && 
                    ((TimetableCell)aboveCell).getCourseCode().equals(cell.getCourseCode())) {
                    isContinuation = true;
                }
            }
            
            // For lab continuation cells, show simplified info
            if (isContinuation) {
                panel.add(new JLabel("↑ " + cell.getCourseCode() + " (continued)"));
            } else {
                panel.add(codeLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(roomLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(instructorLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 2)));
                panel.add(typeLabel);
            }
            
            return panel;
        } else if (column == 0) {
            // Time column styling
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBackground(new Color(245, 245, 245));
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            // Empty cells
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setText("");
            setBackground(Color.WHITE);
        }
        
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return this;
    }
}

    
    // Helper class for timetable cell data
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
}
