package org.lab6.gui.controllers;

import common.console.Console;
import common.models.Organization;
import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import org.lab6.Client;
import org.lab6.commands.commands.Show;
import org.lab6.commands.commands.Update;
import org.lab6.gui.models.CustomTableModel;
import org.lab6.utils.SessionHandler;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TableController extends JDialog {
    private final Client client;
    private final Console outputArea;
    private JTable table;
    private CustomTableModel tableModel;
    private JButton applyButton;
    private List<Organization> organizations;
    private boolean changesMade = false;

    public  TableController(JFrame parent, Client client, Console outputArea) {
        super(parent, "Organization Table", true);
        this.client = client;
        this.outputArea = outputArea;

        try {
            fetchOrganizations();
        } catch (IOException | ClassNotFoundException e) {
            appendError("Error fetching organizations: " + e.getMessage());
            return;
        }

        tableModel = new CustomTableModel(organizations);

        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);

        TableRowSorter<CustomTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        sorter.setComparator(0, Comparator.comparingInt(o -> (int) o));
        sorter.setComparator(2, Comparator.comparingInt(o -> (Integer) o));
        sorter.setComparator(3, Comparator.comparingLong(o -> (Long) o));
        sorter.setComparator(5, Comparator.comparingLong(o -> (Long) o));
        sorter.setComparator(8, Comparator.comparingLong(o -> (Long) o));
        sorter.setComparator(9, Comparator.comparingLong(o -> (Long) o));
        sorter.setComparator(10, Comparator.comparingLong(o -> (Long) o));

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel filterPanel = createFilterPanel();

        applyButton = new JButton("Apply Changes");
        applyButton.setEnabled(false);
        applyButton.addActionListener(e -> applyChanges());


        table.getModel().addTableModelListener(e -> {
            changesMade = true;
            applyButton.setEnabled(true);
        });


        setupLayout(scrollPane, filterPanel);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void fetchOrganizations() throws IOException, ClassNotFoundException {
        Request request = new Request(Request.RequestType.DEFAULT, new Show(), Map.of(ArgumentType.SESSION, SessionHandler.getSession()));
        Response response = client.sendAndReceiveCommand(request);
        organizations = new ArrayList<>(response.getOrganizations());
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridLayout(1, tableModel.getColumnCount()));

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            JTextField filterField = new JTextField();
            int colIndex = i;
            filterField.addCaretListener(e -> applyFilter(filterField.getText(), colIndex));
            filterPanel.add(filterField);
        }

        return filterPanel;
    }

    private void applyFilter(String text, int colIndex) {
        RowFilter<CustomTableModel, Object> rowFilter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends CustomTableModel, ? extends Object> entry) {
                String columnValue = entry.getStringValue(colIndex);
                return columnValue.contains(text);
            }
        };

        TableRowSorter<CustomTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setRowFilter(rowFilter);
        table.setRowSorter(sorter);
    }

    private void setupLayout(JScrollPane scrollPane, JPanel filterPanel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(applyButton);

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void applyChanges() {
        if (changesMade) {
            List<Organization> modifiedOrganizations = tableModel.getModifiedOrganizations();

            if (!modifiedOrganizations.isEmpty()) {
                applyButton.setEnabled(false);

                SwingWorker<Void, Void> saveWorker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            for (Organization org : modifiedOrganizations) {
                                if (org == null) {
                                    throw new IOException("Organization object is null.");
                                }

                                Map<ArgumentType, Object> args = new HashMap<>();
                                args.put(ArgumentType.ORGANIZATION, org);
                                args.put(ArgumentType.SESSION, SessionHandler.getSession());
                                args.put(ArgumentType.ID, org.getId());
                                System.out.println(org.getFullName());

                                Request request = new Request(Request.RequestType.DEFAULT, new Update(), args);
                                Response response = client.sendAndReceiveCommand(request);

                                if (!response.isSuccess()) {
                                    System.out.println("Error for organization ID " + org.getId() + ": " + response.getMessage());
                                    throw new IOException("Failed to apply changes: " + response.getMessage());
                                }
                            }
                            return null;
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                            throw new Exception("Error sending update request: " + e.getMessage());
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(TableController.this,
                                    "Changes applied successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            changesMade = false;
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(TableController.this,
                                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            applyButton.setEnabled(false);
                        }
                    }
                };

                saveWorker.execute();
            }
        }
    }

    private void appendError(String errorMessage) {
        SwingUtilities.invokeLater(() -> outputArea.printError(errorMessage));
    }
}