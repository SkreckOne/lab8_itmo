package org.lab6.gui.controllers;

import common.models.*;
import org.lab6.gui.models.AddCommandModel;
import org.lab6.utils.SessionHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AddCommandController extends JDialog {

    private JTextField nameField;
    private JTextField annualTurnoverField;
    private JTextField fullNameField;
    private final AddCommandModel addCommandModel;
    private JComboBox<OrganizationType> organizationTypeComboBox;
    private JTextField zipField;
    private JTextField xField, yField;
    private JTextField locXField, locYField, locZField;

    public AddCommandController(JFrame parent, AddCommandModel addCommandModel) {
        super(parent, "Add Organization", true); // true makes it a modal dialog
        this.addCommandModel = addCommandModel;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel organizationPanel = createOrganizationPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.3;
        add(organizationPanel, gbc);

        JPanel addressPanel = createAddressPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.2;
        add(addressPanel, gbc);

        JPanel coordinatesPanel = createCoordinatesPanel();
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(coordinatesPanel, gbc);

        JPanel locationPanel = createLocationPanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.3;
        add(locationPanel, gbc);

        JButton createButton = new JButton("Create Organization");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createButton, gbc);

        createButton.addActionListener(e -> {
            try {
                Organization organization = createOrganization();
                StringBuilder errorMessage = new StringBuilder();
                if (!organization.validateWithMessages(errorMessage)) {
                    showErrorDialog(errorMessage.toString());
                    return; // Prevent closing the dialog if validation fails
                }

                addCommandModel.sendAddRequest(organization);
                dispose();

            } catch (IllegalArgumentException ex) {
                showErrorDialog(ex.getMessage());
            }
        });

        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createOrganizationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel nameLabel = new JLabel("Name: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel annualTurnoverLabel = new JLabel("Annual Turnover: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(annualTurnoverLabel, gbc);

        annualTurnoverField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(annualTurnoverField, gbc);

        JLabel fullNameLabel = new JLabel("Full Name: ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(fullNameLabel, gbc);

        fullNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(fullNameField, gbc);

        JLabel typeLabel = new JLabel("Type: ");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(typeLabel, gbc);

        organizationTypeComboBox = new JComboBox<>(OrganizationType.values());
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(organizationTypeComboBox, gbc);

        return panel;
    }

    private JPanel createAddressPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel zipLabel = new JLabel("Zip Code: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(zipLabel, gbc);

        zipField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(zipField, gbc);

        return panel;
    }

    private JPanel createCoordinatesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel xLabel = new JLabel("X:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(xLabel, gbc);

        xField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(xField, gbc);

        JLabel yLabel = new JLabel("Y:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(yLabel, gbc);

        yField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(yField, gbc);

        return panel;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createLocationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel locXLabel = new JLabel("X:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(locXLabel, gbc);

        locXField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(locXField, gbc);

        JLabel locYLabel = new JLabel("Y:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(locYLabel, gbc);

        locYField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(locYField, gbc);

        JLabel locZLabel = new JLabel("Z:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(locZLabel, gbc);

        locZField = new JTextField(5);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(locZField, gbc);

        return panel;
    }

    public Organization createOrganization() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        String annualTurnoverStr = annualTurnoverField.getText();
        if (annualTurnoverStr == null || annualTurnoverStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Annual Turnover cannot be empty.");
        }

        String fullName = fullNameField.getText();
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name cannot be empty.");
        }

        Long annualTurnover;
        try {
            annualTurnover = Long.parseLong(annualTurnoverStr);
            if (annualTurnover <= 0) {
                throw new IllegalArgumentException("Annual Turnover must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Annual Turnover must be a valid number.");
        }

        OrganizationType type = (OrganizationType) organizationTypeComboBox.getSelectedItem();
        if (type == null) {
            throw new IllegalArgumentException("Organization Type cannot be null.");
        }

        String xStr = xField.getText();
        String yStr = yField.getText();
        if (xStr == null || xStr.trim().isEmpty() || yStr == null || yStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Coordinates cannot be empty.");
        }
        Coordinates coordinates;
        try {
            coordinates = new Coordinates(Integer.parseInt(xStr), Integer.parseInt(yStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid numbers.");
        }

        String locXStr = locXField.getText();
        String locYStr = locYField.getText();
        String locZStr = locZField.getText();
        if (locXStr == null || locXStr.trim().isEmpty() ||
                locYStr == null || locYStr.trim().isEmpty() ||
                locZStr == null || locZStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Location fields cannot be empty.");
        }
        Location town;
        try {
            town = new Location(Long.parseLong(locXStr), Long.parseLong(locYStr), Long.parseLong(locZStr));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Location fields must be valid numbers.");
        }

        String zipCode = zipField.getText();
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Zip Code cannot be empty.");
        }

        Address postalAddress = new Address(zipCode, town);

        return new Organization(name, coordinates, annualTurnover, fullName, type, postalAddress, SessionHandler.getSession().getUserId());
    }
}