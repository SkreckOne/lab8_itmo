package org.lab6.gui.controllers;

import org.lab6.Client;
import org.lab6.gui.localization.LanguagesEnum;
import org.lab6.gui.models.AuthModel;

import javax.swing.*;
import java.awt.*;

public class AuthFormController extends JFrame {

    private final AuthModel model;
    private JTextField loginUsernameInput;
    private JPasswordField loginPasswordInput;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<LanguagesEnum> languagesComboBox;

    public AuthFormController(Client client) {
        this.model = new AuthModel(client, this);
        initUI();
    }

    private void initUI() {
        // Set up the frame
        setTitle("Authentication Form");
        setSize(500, 250);  // Increased width for larger fields
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Reduced padding
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Allow components to stretch horizontally

        // Row 1: Label "Select Language:" and ComboBox for language selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;  // Less space for label
        panel.add(new JLabel("Select Language:"), gbc);

        languagesComboBox = new JComboBox<>(LanguagesEnum.values());
        languagesComboBox.addActionListener(e -> changeLanguage());

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;  // More space for ComboBox to make it wider
        panel.add(languagesComboBox, gbc);

        // Row 2: Label "Username:" and TextField for username input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;  // Less space for label
        panel.add(new JLabel("Username:"), gbc);

        loginUsernameInput = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;  // More space for TextField to make it wider
        panel.add(loginUsernameInput, gbc);

        // Row 3: Label "Password:" and PasswordField for password input
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;  // Less space for label
        panel.add(new JLabel("Password:"), gbc);

        loginPasswordInput = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.9;  // More space for PasswordField to make it wider
        panel.add(loginPasswordInput, gbc);

        // Row 4: Buttons for login and register (both wider)
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;  // Half of the available space for Login button
        gbc.fill = GridBagConstraints.BOTH;  // Make buttons fill horizontally and vertically
        panel.add(loginButton, gbc);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> register());

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.5;  // Half of the available space for Register button
        gbc.fill = GridBagConstraints.BOTH;  // Make buttons fill horizontally and vertically
        panel.add(registerButton, gbc);


        add(panel);
    }

    private void changeLanguage() {
        LanguagesEnum selectedLanguage = (LanguagesEnum) languagesComboBox.getSelectedItem();
        // Logic to change the UI language
    }

    private void register() {
        registerButton.setEnabled(false);  // Disable the button while registering

        boolean success = model.register(loginUsernameInput.getText(), new String(loginPasswordInput.getPassword()));
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Transition to the next form (optional)
        } else {
            JOptionPane.showMessageDialog(this, "Error occurred while registering", "Error", JOptionPane.ERROR_MESSAGE);
        }

        registerButton.setEnabled(true);
    }

    private void login() {
        loginButton.setEnabled(false);  // Disable the button while logging in

        boolean success = model.login(loginUsernameInput.getText(), new String(loginPasswordInput.getPassword()));
        if (success) {
            JOptionPane.showMessageDialog(this, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Transition to the next form (optional)
        } else {
            JOptionPane.showMessageDialog(this, "Error occurred while authorizing", "Error", JOptionPane.ERROR_MESSAGE);
        }

        loginButton.setEnabled(true);
    }
}