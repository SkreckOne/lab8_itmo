package org.lab6;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lab6.utils.Runner;
import common.console.StandardConsole;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

public class ClientMain {
//    inc_ace_, [09.09.2024 13:41]
//    KhNI-4331
//
//    inc_ace_, [09.09.2024 13:41]
//    s409178
    private static final int PORT = 9999;
    public static final Logger logger = LogManager.getLogger("ClientLogger");

    public static void main(String[] args) {
        var console = new StandardConsole();

        int PORT = 6666;
        String host = "127.0.0.1"; //192.168.10.80
        //KhNI-4331
        //
        //s409178
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
            var client = new Client(InetAddress.getByName("192.168.10.80"), PORT);
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