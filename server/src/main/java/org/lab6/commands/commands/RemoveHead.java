package org.lab6.commands.commands;

import common.console.Console;
import common.models.Organization;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;

import java.util.*;

public class RemoveHead extends Command {
    private static final long serialVersionUID = 595733L;
    private transient final Console console;
    private transient final CollectionManager collectionManager;

    public RemoveHead(Console console, CollectionManager collectionManager) {
        super("remove_head", "вывести и удалить первый элемент в коллекции");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        String username = (String) args.get(ArgumentType.USERNAME);
        var firstElement = collectionManager.getFirstElement(username);
        if (firstElement == null){
            return new Response(Response.ResponseType.DEFAULT,false, "Коллекция пуста.");
        }
        var res = collectionManager.remove(firstElement.getId(), username);
        return new Response(Response.ResponseType.DEFAULT,res, "Организация успешно удалена.", new PriorityQueue<>(Collections.singletonList(firstElement)));
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.USERNAME));
    }
}