package passwordgenerator;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UsernameIDGenerator extends Frame implements ActionListener {
    private TextField usernameField, userIdField, passwordField, retypePasswordField;
    private Button generateUserIdButton, generatePasswordButton, signUpButton, backToLoginButton;
    private Label messageLabel;
    private Panel mainPanel;
    private Connection conn;
    private Properties config;

    public UsernameIDGenerator() {
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
        setTitle("Sign Up");
        setLayout(new BorderLayout());
        setBackground(UIUtils.BACKGROUND_COLOR);

        mainPanel = new Panel(new GridBagLayout());
        mainPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Label lblHeader = new Label("Create New Account", Label.CENTER);
        lblHeader.setFont(UIUtils.HEADER_FONT);
        lblHeader.setForeground(UIUtils.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(lblHeader, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new Label("Username:"), gbc);
        usernameField = new TextField(20);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new Label("User ID:"), gbc);
        userIdField = new TextField(20);
        gbc.gridx = 1;
        mainPanel.add(userIdField, gbc);
        generateUserIdButton = new Button("Generate ID");
        gbc.gridx = 2;
        mainPanel.add(generateUserIdButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new Label("Password:"), gbc);
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        generatePasswordButton = new Button("Generate Password");
        gbc.gridx = 2;
        mainPanel.add(generatePasswordButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new Label("Retype Password:"), gbc);
        retypePasswordField = new TextField(20);
        retypePasswordField.setEchoChar('*');
        gbc.gridx = 1;
        mainPanel.add(retypePasswordField, gbc);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        signUpButton = new Button("Sign Up");
        backToLoginButton = new Button("Back to Login");
        UIUtils.styleButton(signUpButton);
        UIUtils.styleButton(backToLoginButton);
        buttonPanel.add(signUpButton);
        buttonPanel.add(backToLoginButton);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        mainPanel.add(buttonPanel, gbc);

        gbc.gridy = 6;
        messageLabel = new Label("", Label.CENTER);
        mainPanel.add(messageLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        generateUserIdButton.addActionListener(this);
        generatePasswordButton.addActionListener(this);
        signUpButton.addActionListener(this);
        backToLoginButton.addActionListener(this);

        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private String generateUsernameID() {
        try {
            String sql = "SELECT userid FROM users ORDER BY userid DESC LIMIT 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            String lastId = "D0000";
            if (rs.next()) {
                lastId = rs.getString("userid");
            }

            int year = LocalDate.now().getYear() % 100;
            int currentNum = Integer.parseInt(lastId.substring(3));
            int nextNum = currentNum + 1;

            return String.format("D%02d%03d", year, nextNum);
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error generating ID!");
            return null;
        }
    }

    private void openPasswordGenerator() {
        MainFrame passwordGen = new MainFrame();
        passwordGen.setVisible(true);
        passwordGen.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                String generatedPassword = passwordGen.getGeneratedPassword();
                if (generatedPassword != null && !generatedPassword.isEmpty()) {
                    passwordField.setText(generatedPassword);
                    retypePasswordField.setText(generatedPassword);
                }
            }
        });
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateUserIdButton) {
            String userId = generateUsernameID();
            if (userId != null) {
                userIdField.setText(userId);
                generateUserIdButton.setEnabled(false);
                messageLabel.setText("ID generated successfully!");
            }
        } else if (e.getSource() == generatePasswordButton) {
            openPasswordGenerator();
        } else if (e.getSource() == signUpButton) {
            handleSignUp();
        } else if (e.getSource() == backToLoginButton) {
            new LoginPage();
            dispose();
        }
    }

    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty()) {
            messageLabel.setText("Username is required!");
            return false;
        }
        if (userIdField.getText().trim().isEmpty()) {
            messageLabel.setText("User ID is required!");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            messageLabel.setText("Password is required!");
            return false;
        }
        if (!passwordField.getText().equals(retypePasswordField.getText())) {
            messageLabel.setText("Passwords do not match!");
            return false;
        }
        return true;
    }

    private void handleSignUp() {
        if (!validateFields()) return;

        String username = usernameField.getText().trim();
        String userId = userIdField.getText().trim();
        String password = passwordField.getText();
        String encryptedPassword = encryptPassword(password);

        try {
            String checkSql = "SELECT * FROM users WHERE userid = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                messageLabel.setText("User ID already exists! Please generate a new ID.");
                generateUserIdButton.setEnabled(true);
                return;
            }

            String sql = "INSERT INTO users (username, userid, password) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, userId);
            pstmt.setString(3, encryptedPassword);
            pstmt.executeUpdate();

            messageLabel.setText("Sign up successful! Opening login page...");
            new LoginPage();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error during sign up!");
            generateUserIdButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        new UsernameIDGenerator();
    }
}