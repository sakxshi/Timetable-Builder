package view;

import service.TimetableService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {
    private TimetableService service;
    
    public HomePanel() {
        service = new TimetableService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));
        
        // Header with welcome message
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel welcomeLabel = new JLabel("Welcome to Timetable Builder");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        // Stats panel with modern cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        
        // Classroom stat
        JPanel classroomStat = createStatPanel("Total Classrooms", 
                                             service.getAllClassrooms().size(),
                                             new Color(41, 128, 185));
        
        // Course stat
        JPanel courseStat = createStatPanel("Total Courses", 
                                          service.getAllCourses().size(),
                                          new Color(39, 174, 96));
        
        // Instructor stat
        JPanel instructorStat = createStatPanel("Total Instructors", 
                                              service.getAllInstructors().size(),
                                              new Color(192, 57, 43));
        
        // Timetable stat
        JPanel timetableStat = createStatPanel("Generated Timetables", 
                                             0,
                                             new Color(142, 68, 173));
        
        // Add stats to panel
        statsPanel.add(classroomStat);
        statsPanel.add(courseStat);
        statsPanel.add(instructorStat);
        statsPanel.add(timetableStat);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel actionsLabel = new JLabel("Quick Actions");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionsPanel.add(actionsLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonsPanel.setOpaque(false);
        
        JButton generateBtn = createActionButton("Generate Timetable", new Color(41, 128, 185));
        JButton exportBtn = createActionButton("Export to Excel", new Color(39, 174, 96));
        JButton printBtn = createActionButton("Print Timetable", new Color(142, 68, 173));
        
        buttonsPanel.add(generateBtn);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(printBtn);
        
        actionsPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatPanel(String title, int value, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(Color.WHITE);
        
        // Add a top accent color bar
        JPanel accentPanel = new JPanel();
        accentPanel.setBackground(accentColor);
        accentPanel.setPreferredSize(new Dimension(panel.getWidth(), 5));
        panel.add(accentPanel, BorderLayout.NORTH);
        
        // Title label with icon
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);
        
        // Value label with large font
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);
        
        // Add labels to panel
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color accentColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(accentColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        
        return button;
    }
}
