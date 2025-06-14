package passwordgenerator;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginPage extends Frame implements ActionListener {
    private TextField userIdField, passwordField;
    private Button loginButton, signUpButton;
    private Label messageLabel;
    private Connection conn;
    private Properties config;

    public LoginPage() {
        loadConfig();
        setupDatabase();
        initializeUI();
    }

    private void loadConfig() {
        config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = config.getProperty("db.url");
            String user = config.getProperty("db.user");
            String password = config.getProperty("db.password");
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("Login");
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        Label lblHeader = new Label("Welcome to Password Generator", Label.CENTER);
        lblHeader.setFont(UIUtils.HEADER_FONT);
        lblHeader.setForeground(UIUtils.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(lblHeader, gbc);

        // User ID Field
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new Label("User ID:"), gbc);
        userIdField = new TextField(20);
        gbc.gridx = 1;
        add(userIdField, gbc);

        // Password Field
        gbc.gridx = 0; gbc.gridy = 2;
        add(new Label("Password:"), gbc);
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Buttons Panel
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new Button("Login");
        signUpButton = new Button("Sign Up");
        UIUtils.styleButton(loginButton);
        UIUtils.styleButton(signUpButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        gbc.gridy = 4;
        messageLabel = new Label("", Label.CENTER);
        add(messageLabel, gbc);

        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);

        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == signUpButton) {
            new UsernameIDGenerator();
            dispose();
        }
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = passwordField.getText();
        String encryptedPassword = encryptPassword(password);

        try {
            String sql = "SELECT username FROM users WHERE userid = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, encryptedPassword);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                messageLabel.setText("Login successful!");
                new WelcomePage(username);
                dispose();
            } else {
                messageLabel.setText("Invalid User ID or Password!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Error during login!");
        }
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}