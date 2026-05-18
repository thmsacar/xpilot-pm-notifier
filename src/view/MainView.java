package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Main View class responsible for the graphical user interface.
 * Handles the configuration screen and the live message display.
 */
public class MainView extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextField pathField, tokenField, chatIdField;
    private JButton submitBtn;
    private JLabel gifLabel;
    private JTextArea logArea;

    public MainView() {
        setTitle("xPM - xPilot Message Monitor");
        setSize(600, 520); // Height increased to fit all fields and headers
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        setupInputScreen();
        setupLogScreen();

        add(cardPanel);
        setLocationRelativeTo(null);
    }

    /**
     * Configuration screen with xPilot path and Telegram settings.
     */
    private void setupInputScreen() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(33, 37, 43));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        pathField = new JTextField(30);
        tokenField = new JTextField(30);
        chatIdField = new JTextField(30);
        submitBtn = new JButton("Start Monitoring");

        // UI Rows for configuration
        gbc.gridy = 0; inputPanel.add(createLabel("xPilot AppData Directory:"), gbc);
        gbc.gridy = 1; inputPanel.add(pathField, gbc);

        gbc.gridy = 2; inputPanel.add(createLabel("Telegram Bot Token:"), gbc);
        gbc.gridy = 3; inputPanel.add(tokenField, gbc);

        gbc.gridy = 4; inputPanel.add(createLabel("Telegram Chat ID:"), gbc);
        gbc.gridy = 5; inputPanel.add(chatIdField, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(15, 10, 5, 10);
        inputPanel.add(submitBtn, gbc);

        setupGifLabel();
        gbc.gridy = 7; inputPanel.add(gifLabel, gbc);

        cardPanel.add(inputPanel, "INPUT_SCREEN");
    }

    /**
     * Restored Log View with the professional status header.
     */
    private void setupLogScreen() {
        JPanel logPanel = new JPanel(new BorderLayout());

        // THE RESTORED HEADER
        JLabel headerLabel = new JLabel(" [LIVE] Monitoring Network Logs...", SwingConstants.CENTER);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(45, 45, 45));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerLabel.setPreferredSize(new Dimension(0, 30));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(15, 15, 15));
        logArea.setForeground(new Color(0, 255, 127));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        logPanel.add(headerLabel, BorderLayout.NORTH);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        cardPanel.add(logPanel, "LOG_SCREEN");
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private void setupGifLabel() {
        URL gifUrl = getClass().getResource("/loader.gif");
        if (gifUrl != null) {
            gifLabel = new JLabel("<html><img src='" + gifUrl + "' width='40' height='40'></html>", SwingConstants.CENTER);
        } else {
            gifLabel = new JLabel("Synchronizing...", SwingConstants.CENTER);
            gifLabel.setForeground(Color.GRAY);
        }
        gifLabel.setVisible(false);
    }

    public String getPathInput() { return pathField.getText().trim(); }
    public String getTokenInput() { return tokenField.getText().trim(); }
    public String getChatIdInput() { return chatIdField.getText().trim(); }

    /**
     * Populates fields with saved preferences.
     */
    public void setFields(String p, String t, String c) {
        pathField.setText(p);
        tokenField.setText(t);
        chatIdField.setText(c);
    }

    public void addSubmitListener(ActionListener l) { submitBtn.addActionListener(l); }
    public void showLogScreen() { cardLayout.show(cardPanel, "LOG_SCREEN"); }
    public void showInputScreen() { cardLayout.show(cardPanel, "INPUT_SCREEN"); }

    public void setLoadingState(boolean l) {
        submitBtn.setEnabled(!l);
        gifLabel.setVisible(l);
        submitBtn.setText(l ? "Initializing..." : "Start Monitoring");
    }

    public void appendLog(String m) {
        logArea.append(m + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void showError(String m) {
        JOptionPane.showMessageDialog(this, m, "Configuration Error", JOptionPane.ERROR_MESSAGE);
    }
}