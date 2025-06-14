package passwordgenerator;

import java.awt.*;
import java.awt.event.*;

public class WelcomePage extends Frame {
    public WelcomePage(String username) {
        setTitle("Welcome");
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        Label welcomeLabel = new Label("Welcome, " + username + "!", Label.CENTER);
        welcomeLabel.setFont(UIUtils.HEADER_FONT);
        welcomeLabel.setForeground(UIUtils.PRIMARY_COLOR);
        add(welcomeLabel, gbc);

        // Frame settings
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    
    addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0); 
    }
});
    }
    
}