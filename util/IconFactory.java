package util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class IconFactory {
    private static final Color ICON_COLOR = Color.WHITE;
    private static final int ICON_SIZE = 20;
    
    public static Icon createDashboardIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.fillRect(x+3, y+3, 6, 6);
                g2.fillRect(x+11, y+3, 6, 6);
                g2.fillRect(x+3, y+11, 6, 6);
                g2.fillRect(x+11, y+11, 6, 6);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createClassroomIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.fillRect(x+5, y+12, 10, 4);
                g2.fillRect(x+8, y+8, 4, 4);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createCourseIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.fillRoundRect(x+4, y+3, 12, 14, 3, 3);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createInstructorIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.fillOval(x+6, y+2, 8, 8);
                g2.fillRect(x+5, y+10, 10, 8);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createTimetableIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.fillRect(x+3, y+3, 14, 14);
                g2.setColor(new Color(43, 43, 43));
                g2.drawLine(x+3, y+7, x+17, y+7);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createPlusIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x+5, y+10, x+15, y+10);
                g2.drawLine(x+10, y+5, x+10, y+15);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createClockIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.drawOval(x+3, y+3, 14, 14);
                g2.drawLine(x+10, y+10, x+10, y+5);
                g2.drawLine(x+10, y+10, x+14, y+10);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createDownloadIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.drawLine(x+10, y+3, x+10, y+13);
                g2.drawLine(x+6, y+9, x+10, y+13);
                g2.drawLine(x+10, y+13, x+14, y+9);
                g2.drawLine(x+5, y+16, x+15, y+16);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createSaveIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.fillRect(x+3, y+3, 14, 14);
                g2.setColor(new Color(43, 43, 43));
                g2.fillRect(x+5, y+5, 10, 6);
                g2.setColor(ICON_COLOR);
                g2.fillRect(x+7, y+7, 6, 2);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
    
    public static Icon createRefreshIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_COLOR);
                g2.drawArc(x+3, y+3, 14, 14, 45, 270);
                g2.drawLine(x+16, y+6, x+17, y+9);
                g2.drawLine(x+16, y+6, x+13, y+5);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        };
    }
}
