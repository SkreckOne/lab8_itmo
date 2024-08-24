package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.CollectionManager;

import java.util.ArrayList;
import java.util.Map;

public class Show extends Command {
    private static final long serialVersionUID = 315331L;
    private transient final Console console;
    private transient final CollectionManager collectionManager;

    public Show(Console console, CollectionManager collectionManager) {
        super("show", "вывести все элементы коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
            return new Response(Response.ResponseType.DEFAULT,true, null, collectionManager.getCollection());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>();
    }
}