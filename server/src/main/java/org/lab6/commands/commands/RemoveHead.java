package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.CollectionManager;

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
        var firstElement = collectionManager.getFirstElement();
        if (firstElement == null){
            return new Response(Response.ResponseType.DEFAULT,false, "Коллекция пуста.");
        }
        var res = true;
//        var res = collectionManager.remove(firstElement.getId());
        return new Response(Response.ResponseType.DEFAULT,res, "Организация успешно удалена.", new PriorityQueue<>(Collections.singletonList(firstElement)));
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>();
    }
}