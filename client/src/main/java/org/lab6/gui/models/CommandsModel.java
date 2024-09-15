package org.lab6.gui.models;

import common.console.Console;
import common.exceptions.ValidateExeption;
import org.lab6.Client;
import org.lab6.gui.controllers.AuthFormController;
import org.lab6.gui.controllers.MainFormController;
import org.lab6.utils.Runner;

import javax.swing.*;
import java.io.IOException;

public class CommandsModel {
    private final MainFormController controller;
    private final Client client;
    private final Runner runner;

    public CommandsModel(MainFormController controller, Client client, Console console) {
        this.controller = controller;
        this.client = client;
        this.runner = new Runner(console, client);
    }

    public void handleCommand(String commandName) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String userInput = getUserInput(commandName);
                    String[] userCommand = {commandName, userInput};

                    Runner.ExitCode exitCode = runner.launchCommand(userCommand);

                    if (exitCode == Runner.ExitCode.ERROR) {
                        controller.appendToOutput("Command execution failed: " + commandName);
                    } else {
                        controller.appendToOutput("Command executed successfully: " + commandName);
                    }
                } catch (IOException | ClassNotFoundException | ValidateExeption e) {
                    controller.appendToOutput("Error executing command: " + e.getMessage());
                }
                return null;
            }
        };

        worker.execute();
    }

    public void logout() {
        SwingUtilities.invokeLater(() -> {
            controller.dispose();
            new AuthFormController(client).setVisible(true);
        });
    }

    public void clear() {
        handleCommand("clear");
    }

    public void filterGreaterThanFullName() {
        handleCommand("filter_greater_than_full_name");
    }

    public void filterLessThanFullName() {
        handleCommand("filter_less_than_full_name");
    }

    public void removeById() {
        handleCommand("remove_by_id");
    }

    public void removeLower() {
        handleCommand("remove_lower");
    }

    public void help() {
        handleCommand("help");
    }

    public void history() {
        handleCommand("history");
    }

    public void info() {
        handleCommand("info");
    }

    public void printDescending() {
        handleCommand("print_descending");
    }

    public void removeHead() {
        handleCommand("remove_head");
    }

    private String getUserInput(String commandName) {
        return controller.getInputForCommand(commandName);
    }
}