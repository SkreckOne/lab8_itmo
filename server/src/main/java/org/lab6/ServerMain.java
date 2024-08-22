package org.lab6;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.console.*;
import org.lab6.collection.CollectionManager;
import org.lab6.collection.DatabaseManager;
import org.lab6.commands.CommandManager;
import org.lab6.commands.Invoker;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerMain {
    public static final int PORT = 2222;

    public static Logger logger = LogManager.getLogger("ServerLogger");

    public static void main(String[] args) {
        Console console = new StandardConsole();

        String dbUrl = System.getenv("DBURL");
        String dbUser = System.getenv("DBUSER");
        String dbPass = System.getenv("DBPASS");

        var dbManager = new DatabaseManager(dbUrl, dbUser, dbPass, console);
        var collectionManager = new CollectionManager(dbManager);
        if (!collectionManager.init()) { System.exit(1); }

        collectionManager.validateAll(console);
        Runtime.getRuntime().addShutdownHook(new Thread(collectionManager::saveCollection));
        var commandManager = new CommandManager(collectionManager, console);

        try {
            var server = new Server(PORT, new Invoker(commandManager));
            server.run();
        } catch (SocketException e) {
            logger.fatal("Случилась ошибка сокета", e);
        } catch (UnknownHostException e) {
            logger.fatal("Неизвестный хост", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}