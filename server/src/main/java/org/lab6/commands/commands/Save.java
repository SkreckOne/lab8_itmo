package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.collection.CollectionManager;

import java.util.ArrayList;
import java.util.Map;

public  class Save extends Command {
    private transient final Console console;
    private transient final CollectionManager collectionManager;
    public Save(Console console, CollectionManager collectionManager) {
        super("save", "сохранить коллекцию в файл");
        this.collectionManager = collectionManager;
        this.console = console;
    }
    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        collectionManager.saveCollection();
        return new Response(true, "Коллекция была сохранена.");
    }
}
