package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterGreaterThanFullName extends Command {
    private static final long serialVersionUID = 255243L;
    private transient final Console console;
    private transient final CollectionManager collectionManager;

    public FilterGreaterThanFullName(Console console, CollectionManager collectionManager) {
        super("filter_greater_than_full_name fullName", "вывести все организации, полное имя которых больше чем данное");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
            String username = (String) args.get(ArgumentType.USERNAME);
            String fullname = (String) args.get(ArgumentType.FULLNAME);
            return new Response(true, null, collectionManager.getGreaterThan(username, fullname));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.USERNAME, ArgumentType.FULLNAME));
    }
}
