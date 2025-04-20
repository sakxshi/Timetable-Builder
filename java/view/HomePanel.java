package view;

import service.TimetableService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import static view.UIConstants.*;

public class HomePanel extends JPanel {
    private TimetableService service;
    
    public HomePanel() {
        service = new TimetableService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(SECTION_BG_COLOR);
        
        // Header with welcome message
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(HEADING_FONT);
        
        JLabel welcomeLabel = new JLabel("Welcome to Timetable Builder");
        welcomeLabel.setFont(NORMAL_FONT);
        welcomeLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        // Stats panel with styled cards (matching React design)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        
        // Create stats cards with styled panels
        JPanel classroomStat = createCardPanel("Total Classrooms", 
                                             service.getAllClassrooms().size(),
                                             new Color(41, 128, 185));
        
        JPanel courseStat = createCardPanel("Total Courses", 
                                          service.getAllCourses().size(),
                                          new Color(39, 174, 96));
        
        JPanel instructorStat = createCardPanel("Total Instructors", 
                                              service.getAllInstructors().size(),
                                              new Color(192, 57, 43));
        
        JPanel timetableStat = createCardPanel("Generated Timetables", 
                                             0,
                                             new Color(142, 68, 173));
        
        statsPanel.add(classroomStat);
        statsPanel.add(courseStat);
        statsPanel.add(instructorStat);
        statsPanel.add(timetableStat);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel actionsLabel = new JLabel("Quick Actions");
        actionsLabel.setFont(SUBHEADING_FONT);
        actionsPanel.add(actionsLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonsPanel.setOpaque(false);
        
        JButton generateBtn = createStyledButton("Generate Timetable", new Color(41, 128, 185));
        JButton exportBtn = createStyledButton("Export to Excel", new Color(39, 174, 96));
        JButton printBtn = createStyledButton("Print Timetable", new Color(142, 68, 173));
        
        buttonsPanel.add(generateBtn);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(printBtn);
        
        actionsPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }
}
