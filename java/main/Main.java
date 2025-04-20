package main;

import com.formdev.flatlaf.FlatLightLaf;
import view.Dashboard;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Set FlatLaf as the Look and Feel
            FlatLightLaf.setup();
            
            // Customize UI properties
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.focusColor", new java.awt.Color(0, 120, 212));
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
            }
        });
    }
}
