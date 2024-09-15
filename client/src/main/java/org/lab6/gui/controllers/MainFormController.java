package org.lab6.gui.controllers;

import common.console.TextAreaConsole;
import org.lab6.Client;
import org.lab6.gui.localization.LanguagesEnum;
import org.lab6.gui.models.CommandsModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainFormController extends JFrame {
    private final CommandsModel commandsModel;
    private JComboBox<LanguagesEnum> languagesComboBox;
    private JLabel userInfoLabel;
    private JButton logoutButton;
    private JTextArea outputArea;
    private JPanel canvasPanel;
    private JTextField searchField;
    private JButton searchButton;

    private JTextField filterGreaterThanFullNameField;
    private JTextField filterLessThanFullNameField;
    private JTextField removeByIdField;
    private JTextField removeLowerField;

    public MainFormController(String username, int userId, Client client) {
        initUI(username, userId);
        this.commandsModel = new CommandsModel(this, client, new TextAreaConsole(outputArea));
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

        logoutButton.addActionListener(e -> commandsModel.logout());

        searchButton.addActionListener(e -> searchInTextArea(searchField.getText()));

        linkButtonActions(leftPanel, rightPanel);
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
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setBorder(new LineBorder(Color.GRAY, 1));

        return bottomPanel;
    }

    private JPanel createVerticalButtonPanel(String... buttonNames) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton[] buttons = new JButton[buttonNames.length];
        JTextField[] textFields = new JTextField[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setBorder(new LineBorder(Color.BLUE, 1));
            container.setAlignmentX(Component.CENTER_ALIGNMENT);

            buttons[i] = new JButton(buttonNames[i]);
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            container.add(buttons[i]);

            if (buttonRequiresTextField(buttonNames[i])) {
                textFields[i] = new JTextField();
                textFields[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                textFields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                textFields[i].setBorder(new LineBorder(Color.GRAY, 1));
                container.add(Box.createVerticalStrut(5));
                container.add(textFields[i]);

                switch (buttonNames[i]) {
                    case "Filter Greater Than Full Name":
                        filterGreaterThanFullNameField = textFields[i];
                        break;
                    case "Filter Less Than Full Name":
                        filterLessThanFullNameField = textFields[i];
                        break;
                    case "Remove by ID":
                        removeByIdField = textFields[i];
                        break;
                    case "Remove Lower":
                        removeLowerField = textFields[i];
                        break;
                }
            }

            panel.add(container);
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    private boolean buttonRequiresTextField(String buttonName) {
        return buttonName.equals("Filter Greater Than Full Name") ||
                buttonName.equals("Filter Less Than Full Name") ||
                buttonName.equals("Remove by ID") ||
                buttonName.equals("Remove Lower");
    }

    private void searchInTextArea(String searchText) {
        String content = outputArea.getText();
        if (content.contains(searchText)) {
            outputArea.setCaretPosition(content.indexOf(searchText));
            outputArea.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Text not found", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void linkButtonActions(JPanel leftPanel, JPanel rightPanel) {
        for (Component comp : leftPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JButton) {
                        JButton button = (JButton) innerComp;
                        switch (button.getText()) {
                            case "Clear":
                                button.addActionListener(e -> commandsModel.clear());
                                break;
                            case "Filter Greater Than Full Name":
                                button.addActionListener(e -> commandsModel.filterGreaterThanFullName());
                                break;
                            case "Filter Less Than Full Name":
                                button.addActionListener(e -> commandsModel.filterLessThanFullName());
                                break;
                            case "Help":
                                button.addActionListener(e -> commandsModel.help());
                                break;
                            case "History":
                                button.addActionListener(e -> commandsModel.history());
                                break;
                        }
                    }
                }
            }
        }

        for (Component comp : rightPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JButton) {
                        JButton button = (JButton) innerComp;
                        switch (button.getText()) {
                            case "Info":
                                button.addActionListener(e -> commandsModel.info());
                                break;
                            case "Print Descending":
                                button.addActionListener(e -> commandsModel.printDescending());
                                break;
                            case "Remove by ID":
                                button.addActionListener(e -> commandsModel.removeById());
                                break;
                            case "Remove Head":
                                button.addActionListener(e -> commandsModel.removeHead());
                                break;
                            case "Remove Lower":
                                button.addActionListener(e -> commandsModel.removeLower());
                                break;
                        }
                    }
                }
            }
        }
    }

    public String getInputForCommand(String commandName) {
        switch (commandName) {
            case "Filter Greater Than Full Name":
                return filterGreaterThanFullNameField.getText();
            case "Filter Less Than Full Name":
                return filterLessThanFullNameField.getText();
            case "remove_by_id":
                return removeByIdField.getText();
            case "remove_lower":
                return removeLowerField.getText();
            default:
                return "";
        }
    }

    public void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> outputArea.append(text + "\n"));
    }

}