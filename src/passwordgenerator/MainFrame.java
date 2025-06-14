package passwordgenerator;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import javax.swing.Timer;

public class MainFrame extends Frame implements ActionListener {
    private Checkbox chkUpper, chkLower, chkDigits, chkSpecial;
    private TextField txtLength;
    private TextArea txtOutput;
    private Label lblStrength;
    private Button btnGenerate, btnCopy, btnSave;
    private StrengthBarCanvas strengthBar;
    private Label lblDateTime;
    private String generatedPassword;

    public MainFrame() {
        initializeUI();
        setupEventListeners();
        setLocationRelativeTo(null); // Center the window on screen
    setVisible(true); 
    }

    private void initializeUI() {
        setTitle("Secure Password Generator");
        setSize(700, 600);
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();

        // Header
        Label lblHeader = new Label("Password Generator");
        lblHeader.setFont(UIUtils.HEADER_FONT);
        lblHeader.setForeground(UIUtils.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 10, 10, 10);
        add(lblHeader, gbc);

        // DateTime Label
        lblDateTime = new Label();
        lblDateTime.setFont(UIUtils.LABEL_FONT);
        lblDateTime.setForeground(Color.RED);
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(lblDateTime, gbc);

        // Password Length
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new Label("Password Length:"), gbc);

        txtLength = new TextField("16", 5);
        txtLength.setFont(UIUtils.LABEL_FONT);
        txtLength.setBackground(new Color(255, 255, 204));
        gbc.gridx = 1; gbc.gridy = 1;
        add(txtLength, gbc);

        // Checkboxes
        chkUpper = new Checkbox("Include Uppercase (A-Z)", true);
        chkLower = new Checkbox("Include Lowercase (a-z)", true);
        chkDigits = new Checkbox("Include Numbers (0-9)", true);
        chkSpecial = new Checkbox("Include Special (!@#...)");

        Checkbox[] checks = {chkUpper, chkLower, chkDigits, chkSpecial};
        int y = 2;
        for (Checkbox cb : checks) {
            cb.setFont(UIUtils.LABEL_FONT);
            cb.setBackground(UIUtils.BACKGROUND_COLOR);
            gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 3;
            gbc.insets = new Insets(5, 10, 5, 10);
            add(cb, gbc);
        }

        // Buttons
        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(UIUtils.BACKGROUND_COLOR);

        btnGenerate = new Button(" Generate");
        btnCopy = new Button(" Copy");
        btnSave = new Button(" Save");

        UIUtils.styleButton(btnGenerate);
        UIUtils.styleButton(btnCopy);
        UIUtils.styleButton(btnSave);

        btnPanel.add(btnGenerate);
        btnPanel.add(btnCopy);
        btnPanel.add(btnSave);

        gbc.gridy++; gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(btnPanel, gbc);

        // Output Area
        txtOutput = new TextArea(4, 50);
        txtOutput.setFont(new Font("Monospaced", Font.BOLD, 16));
        txtOutput.setEditable(false);
        txtOutput.setBackground(new Color(153, 153, 255));
        gbc.gridy++;
        add(txtOutput, gbc);

        // Strength Label
        lblStrength = new Label("Strength: ", Label.LEFT);
        lblStrength.setFont(UIUtils.LABEL_FONT);
        lblStrength.setForeground(Color.DARK_GRAY);

        Panel strengthPanel = new Panel(new BorderLayout());
        strengthPanel.add(lblStrength, BorderLayout.CENTER);
        strengthPanel.setPreferredSize(new Dimension(300, 25));

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 10, 5, 10);
        add(strengthPanel, gbc);

        // Strength Bar
        strengthBar = new StrengthBarCanvas();
        strengthBar.setPreferredSize(new Dimension(300, 20));
        strengthBar.setBackground(Color.WHITE);

        Panel barContainer = new Panel();
        barContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        barContainer.add(strengthBar);

        gbc.gridy++;
        add(barContainer, gbc);
    }

    private void setupEventListeners() {
        btnGenerate.addActionListener(this);
        btnCopy.addActionListener(this);
        btnSave.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        new Timer(1000, e -> lblDateTime.setText(new Date().toString())).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGenerate) {
            handleGenerate();
        } else if (e.getSource() == btnCopy) {
            handleCopy();
        } else if (e.getSource() == btnSave) {
            handleSave();
        }
    }

    private void handleGenerate() {
        int length;
        try {
            length = Integer.parseInt(txtLength.getText());
            if (length <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            txtOutput.setText(" Invalid length!");
            lblStrength.setText("Strength: N/A");
            strengthBar.updateStrength("None");
            return;
        }

        boolean upper = chkUpper.getState();
        boolean lower = chkLower.getState();
        boolean digits = chkDigits.getState();
        boolean special = chkSpecial.getState();

        generatedPassword = PasswordGenerator.generate(length, upper, lower, digits, special);
        txtOutput.setText(generatedPassword);

        if (generatedPassword.isEmpty()) {
            lblStrength.setText("Strength:  Invalid character selection");
            strengthBar.updateStrength("None");
        } else {
            String strength = PasswordGenerator.evaluateStrength(generatedPassword, upper, lower, digits, special);
            lblStrength.setText("Strength: " + strength);
            strengthBar.updateStrength(strength);
        }
    }

    private void handleCopy() {
        if (generatedPassword == null || generatedPassword.isEmpty()) return;

        StringSelection selection = new StringSelection(generatedPassword);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        lblStrength.setText("Copied to clipboard!");
    }

    private void handleSave() {
        if (generatedPassword == null || generatedPassword.isEmpty()) return;

        FileDialog fileDialog = new FileDialog(this, "Save Password", FileDialog.SAVE);
        fileDialog.setFile("password.txt");
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        if (directory != null && filename != null) {
            try (FileWriter writer = new FileWriter(directory + filename, true)) {
                writer.write("[" + new Date() + "] " + generatedPassword + "\n");
                lblStrength.setText("Password saved to file.");
            } catch (IOException ex) {
                txtOutput.setText(" Error saving file!");
            }
        }
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    class StrengthBarCanvas extends Canvas {
        private Color fill = Color.GRAY;
        private int width = 50;

        public void updateStrength(String level) {
            switch (level) {
                case "Weak": fill = Color.RED; width = 75; break;
                case "Medium": fill = Color.ORANGE; width = 150; break;
                case "Strong": fill = Color.GREEN; width = 225; break;
                case "Very Strong": fill = new Color(0, 153, 255); width = 300; break;
                default: fill = Color.WHITE; width = 50;
            }
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(new Color(255, 255, 255));
            g.fillRect(0, 0, 300, 20);
            g.setColor(fill);
            g.fillRect(0, 0, width, 20);
        }
    }
}