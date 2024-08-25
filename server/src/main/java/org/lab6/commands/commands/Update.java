package org.lab6.commands.commands;

import common.console.Console;
import common.models.Organization;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.CollectionManager;
import java.util.ArrayList;
import java.util.Map;

public class Update extends Command {
    private transient final Console console;
    private transient final CollectionManager collectionManager;
    private static final long serialVersionUID = 661109;


    public Update(Console console, CollectionManager collectionManager) {
        super("update <ID> {element}", "обновить значение элемента коллекции по ID");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
            int id = (int) args.get(ArgumentType.ID);
            var old = collectionManager.getById(id);
            if (old == null || !collectionManager.getCollection().contains(old)) {
                return new Response(Response.ResponseType.DEFAULT,false, "Передан несуществующий ID!");
            }

            Session session = (Session) args.get(ArgumentType.SESSION);
            if (old.getOwnerId() != session.getUserId()){return new Response(Response.ResponseType.DEFAULT,false, "Это не ваша организация");}
            Organization d = (Organization) args.get(ArgumentType.ORGANIZATION);
            d.setId(id);
            if (d.validate()) {
                collectionManager.add(d);
                return new Response(Response.ResponseType.DEFAULT,true, null);
            } else {
                return new Response(Response.ResponseType.DEFAULT,false, "Поля не валидны! Организация не создана");
            }
        } catch (Exception e) {
            console.printError(e.toString());
            return new Response(Response.ResponseType.DEFAULT,false, e.toString());
        }
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(java.util.List.of(ArgumentType.ID, ArgumentType.ORGANIZATION));
    }
}