package org.lab6.gui.models;

import common.models.Organization;
import common.models.OrganizationType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CustomTableModel extends AbstractTableModel {
    private final List<Organization> organizations;
    private final List<Organization> modifiedOrganizations;
    private final String[] columnNames = {"ID", "Name", "Coordinates X", "Coordinates Y", "Creation Date", "Annual Turnover", "Full Name", "Type", "Town X", "Town Y", "Town Z", "ZipCode"};

    public CustomTableModel(List<Organization> organizations) {
        this.organizations = new ArrayList<>(organizations);
        this.modifiedOrganizations = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return organizations.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Organization org = organizations.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> org.getId();
            case 1 -> org.getName();
            case 2 -> org.getCoordinates() != null ? org.getCoordinates().getX() : null;
            case 3 -> org.getCoordinates() != null ? org.getCoordinates().getY() : null;
            case 4 -> org.getCreationDate();
            case 5 -> org.getAnnualTurnover();
            case 6 -> org.getFullName();
            case 7 -> org.getType();
            case 8 ->
                    org.getPostalAddress() != null && org.getPostalAddress().getTown() != null ? org.getPostalAddress().getTown().getX() : null;
            case 9 ->
                    org.getPostalAddress() != null && org.getPostalAddress().getTown() != null ? org.getPostalAddress().getTown().getY() : null;
            case 10 ->
                    org.getPostalAddress() != null && org.getPostalAddress().getTown() != null ? org.getPostalAddress().getTown().getZ() : null;
            case 11 -> org.getPostalAddress() != null ? org.getPostalAddress().getZipCode() : null;
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 0;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Organization org = organizations.get(rowIndex);
        try {
            switch (columnIndex) {
                case 1:
                    org.setName((String) value);
                    break;
                case 2:
                    org.getCoordinates().setX(Integer.parseInt((String) value));
                    break;
                case 3:
                    org.getCoordinates().setY(Long.parseLong((String) value));
                    break;
                case 5:
                    org.setAnnualTurnover(Long.parseLong((String) value));
                    break;
                case 6:
                    org.setFullName((String) value);
                    break;
                case 7:
                    org.setType(OrganizationType.valueOf((String) value));
                    break;
                case 8:
                    org.getPostalAddress().getTown().setX(Long.parseLong((String) value));
                    break;
                case 9:
                    org.getPostalAddress().getTown().setY(Long.parseLong((String) value));
                    break;
                case 10:
                    org.getPostalAddress().getTown().setZ(Long.parseLong((String) value));
                    break;
                case 11:
                    org.getPostalAddress().setZipCode((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid column index");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!modifiedOrganizations.contains(org)) {
            modifiedOrganizations.add(org);
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public List<Organization> getModifiedOrganizations() {
        return modifiedOrganizations;
    }
}