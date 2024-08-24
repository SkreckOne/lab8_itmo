package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.apache.logging.log4j.Logger;
import org.lab6.ServerMain;
import org.lab6.managers.CollectionManager;

import java.util.ArrayList;
import java.util.Map;

public class Clear extends Command {
    private static final long serialVersionUID = 392616;
    private transient final Console console;
    private transient final CollectionManager collectionManager;
    private final Logger logger = ServerMain.logger;

    public Clear(Console console, CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
//            collectionManager.clear();
            return new Response(Response.ResponseType.DEFAULT,true, "Коллекция очищена.");
        } catch (Exception e) {
            return new Response(Response.ResponseType.DEFAULT,false, "Ошибка при очистке коллекции!");
        }
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>();
    }
}