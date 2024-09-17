package org.lab6.gui.controllers;

import org.lab6.Client;
import org.lab6.gui.localization.LanguagesEnum;
import org.lab6.gui.models.AuthModel;
import org.lab6.utils.SessionHandler;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthFormController extends JFrame {

    private final AuthModel model;
    private JTextField loginUsernameInput;
    private JPasswordField loginPasswordInput;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<LanguagesEnum> languagesComboBox;
    private ResourceBundle bundle;
    private JLabel userLabel;
    private JLabel passwordLabel;
    private JLabel langLabel;

    public AuthFormController(Client client) {
        this.model = new AuthModel(client, this);
        initUI();
    }

    private void initUI() {
        setTitle("Authentication Form");
        setSize(500, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Locale.setDefault(new Locale("en", "ZA"));
        bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        langLabel = new JLabel(bundle.getString("select_language"));
        panel.add(langLabel, gbc);

        languagesComboBox = new JComboBox<>(LanguagesEnum.values());
        languagesComboBox.addActionListener(e -> changeLanguage());

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        panel.add(languagesComboBox, gbc);

        // Username input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        userLabel = new JLabel(bundle.getString("username"));
        panel.add(userLabel, gbc);

        loginUsernameInput = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.9;
        panel.add(loginUsernameInput, gbc);

        // Password input
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        passwordLabel= new JLabel(bundle.getString("password"));
        panel.add(passwordLabel, gbc);

        loginPasswordInput = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.9;
        panel.add(loginPasswordInput, gbc);

        loginButton = new JButton(bundle.getString("login"));
        loginButton.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(loginButton, gbc);

        registerButton = new JButton(bundle.getString("register"));
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
        Locale.setDefault(selectedLanguage.getLocale());

        bundle = ResourceBundle.getBundle("messages", selectedLanguage.getLocale());

        updateUIWithBundle(bundle);
    }

    private void updateUIWithBundle(ResourceBundle bundle) {
        setTitle(bundle.getString("auth_form_title"));
        loginButton.setText(bundle.getString("login"));
        registerButton.setText(bundle.getString("register"));
        userLabel.setText(bundle.getString("username"));
        passwordLabel.setText(bundle.getString("password"));
        langLabel.setText(bundle.getString("select_language"));
    }

    private void register() {
        registerButton.setEnabled(false);

        boolean success = model.register(loginUsernameInput.getText(), new String(loginPasswordInput.getPassword()));
        if (success) {
            JOptionPane.showMessageDialog(this, bundle.getString("registration_success"), bundle.getString("success"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("registration_error"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
        }

        registerButton.setEnabled(true);
    }

    private void login() {
        loginButton.setEnabled(false);

        boolean success = model.login(loginUsernameInput.getText(), new String(loginPasswordInput.getPassword()));
        if (success) {
            SwingUtilities.invokeLater(() -> {
                MainFormController mainForm = new MainFormController(SessionHandler.getSession().getUsername(), SessionHandler.getSession().getUserId(), model.getConnectionHandler());
                mainForm.setVisible(true);
            });
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("login_error"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            loginButton.setEnabled(true);
        }
    }
}