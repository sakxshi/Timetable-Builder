package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static view.UIConstants.*;
import util.IconFactory;

public class Dashboard extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ClassroomManagementPanel classroomPanel;
    private CourseManagementPanel coursePanel;
    private InstructorManagementPanel instructorPanel;
    private TimetableGenerationPanel timetablePanel;
    private HomePanel homePanel;
    private JButton homeBtn, classroomBtn, courseBtn, instructorBtn, timetableBtn;

    public Dashboard() {
        setTitle("Timetable Builder");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeComponents();
        layoutComponents();
        setupActionListeners();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(SECTION_BG_COLOR);
        
        homePanel = new HomePanel();
        classroomPanel = new ClassroomManagementPanel();
        coursePanel = new CourseManagementPanel();
        instructorPanel = new InstructorManagementPanel();
        timetablePanel = new TimetableGenerationPanel();
        
        homeBtn = createSidebarButton("Dashboard", IconFactory.createDashboardIcon());
        classroomBtn = createSidebarButton("Classrooms", IconFactory.createClassroomIcon());
        courseBtn = createSidebarButton("Courses", IconFactory.createCourseIcon());
        instructorBtn = createSidebarButton("Instructors", IconFactory.createInstructorIcon());
        timetableBtn = createSidebarButton("Timetable", IconFactory.createTimetableIcon());
    }

    private JButton createSidebarButton(String text, Icon icon) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setIconTextGap(15);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBackground(new Color(43, 43, 43));
        button.setForeground(WHITE_COLOR);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 20, 0, 0));
        return button;
    }

    private void layoutComponents() {
        cardPanel.add(homePanel, "home");
        cardPanel.add(classroomPanel, "classroom");
        cardPanel.add(coursePanel, "course");
        cardPanel.add(instructorPanel, "instructor");
        cardPanel.add(timetablePanel, "timetable");

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(43, 43, 43));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(35, 35, 35));
        titlePanel.setPreferredSize(new Dimension(200, 80));
        titlePanel.setMaximumSize(new Dimension(200, 80));

        JLabel titleLabel = new JLabel("Timetable Builder");
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(WHITE_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        sidebarPanel.add(titlePanel);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(200, 1));
        separator.setForeground(new Color(60, 60, 60));
        sidebarPanel.add(separator);

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
        sidebarPanel.add(Box.createVerticalGlue());

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(230, 230, 230));
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);

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

        selectButton(homeBtn);
    }

    private void selectButton(JButton selectedButton) {
        homeBtn.setBackground(new Color(43, 43, 43));
        classroomBtn.setBackground(new Color(43, 43, 43));
        courseBtn.setBackground(new Color(43, 43, 43));
        instructorBtn.setBackground(new Color(43, 43, 43));
        timetableBtn.setBackground(new Color(43, 43, 43));
        selectedButton.setBackground(new Color(60, 60, 60));
    }
}
