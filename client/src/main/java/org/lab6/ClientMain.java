package org.lab6;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lab6.utils.Runner;
import common.console.StandardConsole;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

public class ClientMain {

    private static final int PORT = 2222;
    public static final Logger logger = LogManager.getLogger("ClientLogger");

    public static void main(String[] args) {
        var console = new StandardConsole();
        try {
            var client = new Client(InetAddress.getByName("127.0.0.1"), PORT);
            new Runner(console, client).interactiveMode();
            client.close();
        } catch (ConnectException e) {
            logger.error("Сервер недоступен");
        } catch (IOException e) {
            logger.error(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}