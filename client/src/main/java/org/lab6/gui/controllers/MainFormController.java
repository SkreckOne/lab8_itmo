package org.lab6.gui.controllers;

import org.lab6.gui.localization.LanguagesEnum;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFormController extends JFrame {

    private JComboBox<LanguagesEnum> languagesComboBox;
    private JLabel userInfoLabel;
    private JButton logoutButton;
    private JTextArea outputArea;
    private JPanel canvasPanel;
    private JTextField searchField;
    private JButton searchButton;

    public MainFormController(String username, int userId) {
        initUI(username, userId);
    }

    private void initUI(String username, int userId) {
        setTitle("Main Application Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        languagesComboBox = new JComboBox<>(LanguagesEnum.values());
        topPanel.add(languagesComboBox, BorderLayout.WEST);

        userInfoLabel = new JLabel("User: " + username + " (ID: " + userId + ")");
        userInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(userInfoLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.PAGE_START);
        topPanel.setBorder(new LineBorder(Color.GRAY, 1));

        JPanel mainContentPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = createVerticalButtonPanel(
                "Add", "Clear", "Filter Greater Than Full Name", "Filter Less Than Full Name", "Help", "History");
        mainContentPanel.add(leftPanel, BorderLayout.LINE_START);

        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Custom painting logic here
            }
        };
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.setBorder(new LineBorder(Color.GRAY, 1));
        mainContentPanel.add(canvasPanel, BorderLayout.CENTER);

        JPanel rightPanel = createVerticalButtonPanel(
                "Info", "Print Descending", "Remove by ID", "Remove Head", "Remove Lower", "Show Table");
        mainContentPanel.add(rightPanel, BorderLayout.LINE_END);

        JPanel bottomPanel = createBottomPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.8);
        splitPane.setTopComponent(mainContentPanel);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchInTextArea(searchField.getText()));
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchButton = new JButton("Search");

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        bottomPanel.add(searchPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);  // Place scrollable text area below the search panel
        scrollPane.setBorder(new LineBorder(Color.GRAY, 1));  // Add border to the scroll pane

        return bottomPanel;
    }

    private JPanel createVerticalButtonPanel(String... buttonNames) {
        JPanel panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        panel.setBorder(new LineBorder(Color.GRAY, 1));  // Separator

        int maxWidth = 0;
        JButton[] buttons = new JButton[buttonNames.length];
        for (int i = 0; i < buttonNames.length; i++) {
            buttons[i] = new JButton(buttonNames[i]);
            panel.add(buttons[i]);

            maxWidth = Math.max(maxWidth, buttons[i].getPreferredSize().width);
        }

        Dimension buttonSize = new Dimension(maxWidth + 20, 30);
        for (JButton button : buttons) {
            button.setPreferredSize(buttonSize);
        }

        for (int i = 0; i < buttons.length; i++) {
            layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, buttons[i], 0, SpringLayout.HORIZONTAL_CENTER, panel);  // Center horizontally
            if (i == 0) {
                layout.putConstraint(SpringLayout.NORTH, buttons[i], 10, SpringLayout.NORTH, panel);  // First button 10px from top
            } else {
                layout.putConstraint(SpringLayout.NORTH, buttons[i], 10, SpringLayout.SOUTH, buttons[i - 1]);  // 10px gap between buttons
            }
        }

        panel.setPreferredSize(new Dimension(maxWidth + 40, getHeight()));
        return panel;
    }

    private void searchInTextArea(String searchText) {
        try {
            String content = outputArea.getText();
            Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                outputArea.setCaretPosition(matcher.start());  // Move cursor to the found text
                outputArea.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Text not found", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Add an ActionListener for the logout button
    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    // Set output in the text area
    public void appendToOutput(String text) {
        outputArea.append(text + "\n");
    }

}