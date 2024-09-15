package org.lab6;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lab6.gui.controllers.AuthFormController;
import org.lab6.utils.Runner;
import common.console.StandardConsole;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

public class ClientMain {
    public static final Logger logger = LogManager.getLogger("ClientLogger");

    public static void main(String[] args) {
        var console = new StandardConsole();

        int PORT = 6666;
        String host = "127.0.0.1";  // IP адрес по умолчанию

        if (args.length != 0) {
            try {
                host = args[0];
                PORT = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port must be an integer.");
                System.exit(1);
            }
        }
        console.println(host);
        console.println(PORT);

        try {
            var client = new Client(InetAddress.getByName(host), PORT);

            SwingUtilities.invokeLater(() -> {
                AuthFormController form = new AuthFormController(client);
                form.setVisible(true);
            });

        } catch (ConnectException e) {
            logger.error("Сервер недоступен");
        } catch (IOException e) {
            logger.error(e);
        }
    }
}