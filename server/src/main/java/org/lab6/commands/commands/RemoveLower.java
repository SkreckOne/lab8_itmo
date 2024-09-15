package org.lab6.commands.commands;

import common.console.Console;
import common.models.Organization;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.CollectionManager;

import java.util.*;

public class RemoveLower extends Command {
    private static final long serialVersionUID = 309833L;
    private transient final Console console;
    private transient final CollectionManager collectionManager;

    public RemoveLower(Console console, CollectionManager collectionManager) {
        super("remove_lower {element}", "удалить все элементы из коллекции, которые меньше данного");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        String organization = (String) args.get(ArgumentType.FULLNAME);
        Session session = (Session) args.get(ArgumentType.SESSION);
        var res = collectionManager.removeLower(organization, session.getUserId());
        if (res)
            return new Response(Response.ResponseType.DEFAULT,res, "Организации успешно удалены.");
        else
            return new Response(Response.ResponseType.DEFAULT,res, "Что-то пошло не так");
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.FULLNAME));
    }
}
