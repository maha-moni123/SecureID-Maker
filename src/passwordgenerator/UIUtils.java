package passwordgenerator;

import java.awt.*;

public class UIUtils {
    public static final Color PRIMARY_COLOR = new Color(0, 51, 102);
    public static final Color BACKGROUND_COLOR = new Color(173, 216, 230);
    public static final Color TEXT_COLOR = new Color(0, 0, 0);
    
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    
    public static void styleButton(Button button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
    }
}