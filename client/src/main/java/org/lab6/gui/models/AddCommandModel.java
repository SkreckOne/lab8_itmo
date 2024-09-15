package org.lab6.gui.models;

import common.models.Organization;
import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import org.lab6.Client;
import org.lab6.commands.commands.Add;
import org.lab6.utils.Runner;
import org.lab6.utils.SessionHandler;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AddCommandModel {
    private final Client client;
    private final Runner runner;
    private final JFrame parentFrame;

    public AddCommandModel(Client client, JFrame parentFrame) {
        this.client = client;
        this.runner = new Runner(null, client);
        this.parentFrame = parentFrame;
    }

    public void sendAddRequest(Organization organization) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {

                    Map<ArgumentType, Object> args = new HashMap<>();
                    args.put(ArgumentType.ORGANIZATION, organization);
                    args.put(ArgumentType.SESSION, SessionHandler.getSession());

                    Response response = client.sendAndReceiveCommand(new Request(Request.RequestType.DEFAULT, new Add(), args));

                    if (response.isSuccess()) {
                        showSuccessDialog("Organization added successfully!");
                    } else {
                        showErrorDialog("Failed to add organization.");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    showErrorDialog("Error executing add command: " + e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}