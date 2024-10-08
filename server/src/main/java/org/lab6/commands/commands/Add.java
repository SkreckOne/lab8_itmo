package org.lab6.commands.commands;

import common.console.Console;
import common.models.Organization;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.CollectionManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Add extends Command {
    private static final long serialVersionUID = 475358;
    private transient final Console console;
    private transient final CollectionManager collectionManager;


    public Add(Console console, CollectionManager collectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
            System.out.println("pass 1");
            Organization organization = (Organization) args.get(ArgumentType.ORGANIZATION);
            if (!organization.validate()) {
                return new Response(Response.ResponseType.DEFAULT,false, "Поля организации не валидны! Организация не создана!");
            }
            System.out.println("pass 2");
            boolean success = collectionManager.add(organization);
            System.out.println("pass 3");
            if (success)
                return new Response(Response.ResponseType.DEFAULT,success, "Организация успешно добавлена!");
            else
                return new Response(Response.ResponseType.DEFAULT,success, "Полное имя не уникально!");
        } catch (Exception e) {
            console.printError(e.toString());
            console.printError(e.fillInStackTrace());
            return new Response(Response.ResponseType.DEFAULT,false, e.toString());
        }
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.ORGANIZATION));
    }

}
