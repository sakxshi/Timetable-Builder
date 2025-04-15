package main;

import view.Dashboard;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Always create and update Swing components on the Event Dispatch Thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Instantiate the main dashboard window.
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
            }
        });
    }
}
