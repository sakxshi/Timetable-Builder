package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UIConstants {
    // Constants
    public static final Color PRIMARY_COLOR = new Color(230, 57, 70);
    public static final Color SECONDARY_COLOR = new Color(168, 218, 220);
    public static final Color HEADING_COLOR = new Color(29, 53, 87);
    public static final Color WHITE_COLOR = new Color(255, 255, 255);
    public static final Color SECTION_BG_COLOR = new Color(236, 236, 236);
    public static final Color DARK_COLOR = new Color(0, 0, 0);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final int BORDER_RADIUS_SMALL = 10;
    public static final int BORDER_RADIUS_MEDIUM = 20;
    public static final int BORDER_RADIUS_LARGE = 100;

    // Added a new overloaded method to handle Icon
    public static JButton createStyledButton(String text, Icon icon) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setFont(NORMAL_FONT);
        button.setForeground(WHITE_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        return button;
    }

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setForeground(WHITE_COLOR);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        return button;
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(181, 189, 196), 1, true),
            BorderFactory.createEmptyBorder(7, 8, 7, 8)));
        textField.setBackground(new Color(249, 250, 250));
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(WHITE_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
    }

    public static JPanel createCardPanel(String title, int value, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panel.setBackground(WHITE_COLOR);

        JPanel accentPanel = new JPanel();
        accentPanel.setBackground(accentColor);
        accentPanel.setPreferredSize(new Dimension(panel.getWidth(), 5));
        panel.add(accentPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SMALL_FONT);
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.SOUTH);

        return panel;
    }
}
