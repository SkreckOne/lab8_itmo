package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrintDescending extends Command {
    private static final long serialVersionUID = 820105;
    private transient final Console console;
    private transient final CollectionManager collectionManager;

    public PrintDescending(Console console, CollectionManager collectionManager) {
        super("print_descending", "вывести все элементы коллекции в обратном порядке");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
            return new Response(Response.ResponseType.DEFAULT,true, null, collectionManager.getCollectionDescending());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>();
    }
}
