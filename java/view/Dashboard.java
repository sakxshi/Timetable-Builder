package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    private ClassroomManagementPanel classroomPanel;
    private CourseManagementPanel coursePanel;
    private InstructorManagementPanel instructorPanel;
    private TimetableGenerationPanel timetablePanel;
    private HomePanel homePanel;
    
    private JButton homeBtn, classroomBtn, courseBtn, instructorBtn, timetableBtn;
    
    private Color primaryColor = new Color(0, 120, 212);
    private Color sidebarColor = new Color(43, 43, 43);
    private Color backgroundColor = new Color(245, 245, 245);
    
    public Dashboard() {
        setTitle("Timetable Builder");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize components
        initializeComponents();
        layoutComponents();
        
        // Set up action listeners
        setupActionListeners();
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(backgroundColor);
        
        homePanel = new HomePanel();
        classroomPanel = new ClassroomManagementPanel();
        coursePanel = new CourseManagementPanel();
        instructorPanel = new InstructorManagementPanel();
        timetablePanel = new TimetableGenerationPanel();
        
        homeBtn = createSidebarButton("Dashboard", "icons/dashboard.png");
        classroomBtn = createSidebarButton("Classrooms", "icons/classroom.png");
        courseBtn = createSidebarButton("Courses", "icons/course.png");
        instructorBtn = createSidebarButton("Instructors", "icons/instructor.png");
        timetableBtn = createSidebarButton("Timetable", "icons/timetable.png");
    }
    
    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(sidebarColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        // Try to add icon if available
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + iconPath));
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(10);
            }
        } catch (Exception e) {
            // Icon not found, continue without icon
        }
        
        return button;
    }
    
    private void layoutComponents() {
        // Add panels to card panel
        cardPanel.add(homePanel, "home");
        cardPanel.add(classroomPanel, "classroom");
        cardPanel.add(coursePanel, "course");
        cardPanel.add(instructorPanel, "instructor");
        cardPanel.add(timetablePanel, "timetable");
        
        // Create better-looking sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(sidebarColor);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Add logo/app title to sidebar
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(35, 35, 35));
        titlePanel.setPreferredSize(new Dimension(200, 80));
        titlePanel.setMaximumSize(new Dimension(200, 80));
        
        JLabel titleLabel = new JLabel("Timetable Builder");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        sidebarPanel.add(titlePanel);
        
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(200, 1));
        separator.setForeground(new Color(60, 60, 60));
        sidebarPanel.add(separator);
        
        // Add navigation buttons with some spacing
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(homeBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(classroomBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(courseBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(instructorBtn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(timetableBtn);
        
        // Add filler to push buttons to the top
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Add status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(230, 230, 230));
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // Add components to main frame
        setLayout(new BorderLayout());
        add(sidebarPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupActionListeners() {
        homeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "home");
                selectButton(homeBtn);
            }
        });
        
        classroomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "classroom");
                selectButton(classroomBtn);
            }
        });
        
        courseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "course");
                selectButton(courseBtn);
            }
        });
        
        instructorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "instructor");
                selectButton(instructorBtn);
            }
        });
        
        timetableBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "timetable");
                selectButton(timetableBtn);
            }
        });
        
        // Select home button by default
        selectButton(homeBtn);
    }
    
    private void selectButton(JButton selectedButton) {
        // Reset all buttons
        homeBtn.setBackground(sidebarColor);
        classroomBtn.setBackground(sidebarColor);
        courseBtn.setBackground(sidebarColor);
        instructorBtn.setBackground(sidebarColor);
        timetableBtn.setBackground(sidebarColor);
        
        // Highlight selected button
        selectedButton.setBackground(new Color(60, 60, 60));
    }
}
