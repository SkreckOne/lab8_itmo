package org.lab6.commands.commands;

import common.console.Console;
import common.models.Organization;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;
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
            String username = (String) args.get(ArgumentType.USERNAME);
            var old = collectionManager.getById(id, username);
            if (old == null || !collectionManager.getCollection(username).contains(old)) {
                return new Response(false, "Передан несуществующий ID!");
            }
            Organization d = (Organization) args.get(ArgumentType.ORGANIZATION);
            d.setId(id);
            if (d.validate()) {
                collectionManager.remove(old.getId(), username);
                collectionManager.add(d);
                return new Response(true, null);
            } else {
                return new Response(false, "Поля не валидны! Организация не создана");
            }
        } catch (Exception e) {
            console.printError(e.toString());
            return new Response(false, e.toString());
        }
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(java.util.List.of(ArgumentType.ID, ArgumentType.USERNAME, ArgumentType.ORGANIZATION));
    }
}