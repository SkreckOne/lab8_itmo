package org.lab6.gui.controllers;

import org.lab6.Client;
import org.lab6.gui.localization.LanguagesEnum;
import org.lab6.gui.models.AuthModel;
import org.lab6.utils.SessionHandler;

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
        setTitle("Authentication Form");
        setSize(500, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        panel.add(new JLabel("Select Language:"), gbc);

        languagesComboBox = new JComboBox<>(LanguagesEnum.values());
        languagesComboBox.addActionListener(e -> changeLanguage());

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        panel.add(languagesComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        panel.add(new JLabel("Username:"), gbc);

        loginUsernameInput = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        panel.add(loginUsernameInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        panel.add(new JLabel("Password:"), gbc);

        loginPasswordInput = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.9;
        panel.add(loginPasswordInput, gbc);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(loginButton, gbc);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> register());

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(registerButton, gbc);


        add(panel);
    }

    private void changeLanguage() {
        LanguagesEnum selectedLanguage = (LanguagesEnum) languagesComboBox.getSelectedItem();
    }

    private void register() {
        registerButton.setEnabled(false);

        boolean success = model.register(loginUsernameInput.getText(), new String(loginPasswordInput.getPassword()));
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error occurred while registering", "Error", JOptionPane.ERROR_MESSAGE);
        }

        registerButton.setEnabled(true);
    }

    private void login() {
        loginButton.setEnabled(false);

        boolean success = model.login(loginUsernameInput.getText(), new String(loginPasswordInput.getPassword()));
        if (success) {
            SwingUtilities.invokeLater(() -> {
                MainFormController mainForm = new MainFormController(SessionHandler.getSession().getUsername(), SessionHandler.getSession().getUserId());
                mainForm.setVisible(true);
            });
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error occurred while authorizing", "Error", JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(true);
        }
    }
}