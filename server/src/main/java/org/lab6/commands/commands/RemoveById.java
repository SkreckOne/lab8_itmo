package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveById extends Command {
    private transient final Console console;
    private transient final CollectionManager collectionManager;
    private static final long serialVersionUID = 497570;

    public RemoveById(Console console, CollectionManager collectionManager) {
        super("remove_by_id <ID>", "удалить элемент из коллекции по ID");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        int id = (int) args.get(ArgumentType.ID);
        var res = collectionManager.remove(id);
        if (res)
            return new Response(Response.ResponseType.DEFAULT,res, "Организация успешно удалена.");
        else
            return new Response(Response.ResponseType.DEFAULT,res, "Передан несуществующий ID! Организация не удалена.");
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.ID));
    }
}