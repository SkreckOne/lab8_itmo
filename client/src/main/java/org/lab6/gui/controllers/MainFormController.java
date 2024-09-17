package org.lab6.gui.controllers;

import common.console.TextAreaConsole;
import org.lab6.Client;
import org.lab6.gui.localization.LanguagesEnum;
import org.lab6.gui.models.AddCommandModel;
import org.lab6.gui.models.CanvaModel;
import org.lab6.gui.models.CommandsModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MainFormController extends JFrame {
    private final CommandsModel commandsModel;
    private JComboBox<LanguagesEnum> languagesComboBox;
    private JLabel userInfoLabel;
    private JButton logoutButton;
    private final JTextArea outputArea;
    private JTextField searchField;
    private JButton searchButton;

    private JTextField filterGreaterThanFullNameField;
    private JTextField filterLessThanFullNameField;
    private JTextField removeByIdField;
    private JTextField removeLowerField;
    private final Client client;
    private ResourceBundle bundle;
    private final String username;
    private final int userId;
    private final Map<String, JButton> buttonMap = new HashMap<>();

    public MainFormController(String username, int userId, Client client) {
        this.client = client;
        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        this.username = username;
        this.userId = userId;
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        this.commandsModel = new CommandsModel(this, client, new TextAreaConsole(outputArea));
        initUI(username, userId);
    }

    private void initUI(String username, int userId) {
        setTitle("Main Application Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Locale.setDefault(new Locale("en", "ZA"));
        bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        languagesComboBox = new JComboBox<>(LanguagesEnum.values());
        languagesComboBox.addActionListener(e -> changeLanguage());
        topPanel.add(languagesComboBox, BorderLayout.WEST);

        userInfoLabel = new JLabel(bundle.getString("User") + ": " + username + " (" + bundle.getString("ID") + ": " + userId + ")");
        userInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(userInfoLabel, BorderLayout.CENTER);

        logoutButton = new JButton(bundle.getString("Logout"));
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.PAGE_START);
        topPanel.setBorder(new LineBorder(Color.GRAY, 1));

        JPanel mainContentPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = createVerticalButtonPanel(
                new String[]{"Add", "Clear", "FilterGreaterThanFullName", "FilterLessThanFullName", "Help", "History"});
        mainContentPanel.add(leftPanel, BorderLayout.LINE_START);

        CanvaModel canvaModel = new CanvaModel(client);
        canvaModel.setBorder(new LineBorder(Color.GRAY, 1));
        mainContentPanel.add(canvaModel, BorderLayout.CENTER);

        JPanel rightPanel = createVerticalButtonPanel(
                new String[]{"Info", "PrintDescending", "RemoveById", "RemoveHead", "RemoveLower", "ShowTable"});
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
        searchButton = new JButton(bundle.getString("Search"));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        bottomPanel.add(searchPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setBorder(new LineBorder(Color.GRAY, 1));

        return bottomPanel;
    }

    private void changeLanguage() {
        LanguagesEnum selectedLanguage = (LanguagesEnum) languagesComboBox.getSelectedItem();
        Locale.setDefault(selectedLanguage.getLocale());

        bundle = ResourceBundle.getBundle("messages", selectedLanguage.getLocale());

        updateUIWithBundle(bundle);
    }

    private void updateUIWithBundle(ResourceBundle bundle) {
        setTitle(bundle.getString("auth_form_title"));
        userInfoLabel.setText(bundle.getString("User") + ": " + username + " (" + bundle.getString("ID") + ": " + userId + ")");
        logoutButton.setText(bundle.getString("Logout"));
        searchButton.setText(bundle.getString("Search"));

        // Update button texts according to the new bundle
        buttonMap.forEach((key, button) -> button.setText(bundle.getString(key)));
    }

    private JPanel createVerticalButtonPanel(String[] buttonKeys) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (String buttonKey : buttonKeys) {
            panel.add(createButtonWithOptionalTextField(buttonKey));
            panel.add(Box.createVerticalStrut(10)); // Add spacing between buttons
        }

        return panel;
    }

    private JPanel createButtonWithOptionalTextField(String buttonKey) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new LineBorder(Color.BLUE, 1));
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        String buttonLabel = bundle.getString(buttonKey);
        JButton button = new JButton(buttonLabel);
        buttonMap.put(buttonKey, button);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        container.add(button);

        if (buttonRequiresTextField(buttonKey)) {
            JTextField textField = new JTextField();
            textField.setAlignmentX(Component.CENTER_ALIGNMENT);
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            textField.setBorder(new LineBorder(Color.GRAY, 1));
            container.add(Box.createVerticalStrut(5));
            container.add(textField);

            switch (buttonKey) {
                case "FilterGreaterThanFullName":
                    filterGreaterThanFullNameField = textField;
                    break;
                case "FilterLessThanFullName":
                    filterLessThanFullNameField = textField;
                    break;
                case "RemoveById":
                    removeByIdField = textField;
                    break;
                case "RemoveLower":
                    removeLowerField = textField;
                    break;
            }
        }

        return container;
    }

    private boolean buttonRequiresTextField(String buttonKey) {
        return buttonKey.equals("FilterGreaterThanFullName") ||
                buttonKey.equals("FilterLessThanFullName") ||
                buttonKey.equals("RemoveById") ||
                buttonKey.equals("RemoveLower");
    }

    private void searchInTextArea(String searchText) {
        String content = outputArea.getText();
        if (content.contains(searchText)) {
            outputArea.setCaretPosition(content.indexOf(searchText));
            outputArea.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("text_not_found"), bundle.getString("Search_Result"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void linkButtonActions(JPanel leftPanel, JPanel rightPanel) {
        for (Component comp : leftPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JButton button) {
                        linkButtonAction(button);
                    }
                }
            }
        }

        for (Component comp : rightPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JButton button) {
                        linkButtonAction(button);
                    }
                }
            }
        }
    }

    private void linkButtonAction(JButton button) {
        switch (button.getText()) {
            case "Add":
                button.addActionListener(e -> openAddCommandController());
                break;
            case "Clear":
                button.addActionListener(e -> commandsModel.clear());
                break;
            case "FilterGreaterThanFullName":
                button.addActionListener(e -> commandsModel.filterGreaterThanFullName());
                break;
            case "FilterLessThanFullName":
                button.addActionListener(e -> commandsModel.filterLessThanFullName());
                break;
            case "Help":
                button.addActionListener(e -> commandsModel.help());
                break;
            case "History":
                button.addActionListener(e -> commandsModel.history());
                break;
            case "Info":
                button.addActionListener(e -> commandsModel.info());
                break;
            case "PrintDescending":
                button.addActionListener(e -> commandsModel.printDescending());
                break;
            case "RemoveById":
                button.addActionListener(e -> commandsModel.removeById());
                break;
            case "RemoveHead":
                button.addActionListener(e -> commandsModel.removeHead());
                break;
            case "RemoveLower":
                button.addActionListener(e -> commandsModel.removeLower());
                break;
            case "ShowTable":
                button.addActionListener(e -> showTableDialog());
                break;
        }
    }

    public String getInputForCommand(String commandName) {
        return switch (commandName) {
            case "FilterGreaterThanFullName" -> filterGreaterThanFullNameField.getText();
            case "FilterLessThanFullName" -> filterLessThanFullNameField.getText();
            case "RemoveById" -> removeByIdField.getText();
            case "RemoveLower" -> removeLowerField.getText();
            default -> "";
        };
    }

    public void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> outputArea.append(text + "\n"));
    }

    private void openAddCommandController() {
        AddCommandModel addCommandModel = new AddCommandModel(client, this);
        new AddCommandController(this, addCommandModel);
    }

    private void showTableDialog() {
        new TableController(this, client, new TextAreaConsole(outputArea)); // The table dialog will block until closed
    }
}