package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterLessThanFullName extends Command {
    private static final long serialVersionUID = 515971;
    private transient final Console console;
    private transient final CollectionManager collectionManager;

    public FilterLessThanFullName(Console console, CollectionManager collectionManager) {
        super("filter_less_than_full_name fullName", "вывести все организации, полное имя которых меньше чем данное");
        this.console = console;
        this.collectionManager = collectionManager;
    }


    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        try {
            String fullname = (String) args.get(ArgumentType.FULLNAME);
            return new Response(Response.ResponseType.DEFAULT,true, null, collectionManager.lowerGreaterThan(fullname));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.FULLNAME));
    }
}
