package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.commands.CommandManager;
import java.util.ArrayList;
import java.util.Map;

public class History extends Command  {
    private static final long serialVersionUID = 452275L;

    public History() {
        super("history", "напечатать историю последних девяти команд");
    }
    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        return new Response(true, "История:");
    }
}
