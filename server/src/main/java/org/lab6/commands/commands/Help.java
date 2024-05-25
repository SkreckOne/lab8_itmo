package org.lab6.commands.commands;


import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.commands.CommandManager;
import java.util.ArrayList;
import java.util.Map;

public class Help extends Command  {
    private static final long serialVersionUID = 1000L;
    private transient final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        super("help", "список всех команд");
        this.commandManager = commandManager;
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        ArrayList<Command> commandList = new ArrayList<>();
        for (Map.Entry<String, Command> entry : commandManager.getCommands().entrySet()) {
            if (!entry.getKey().equals("save")) {
                commandList.add(entry.getValue());
            }
        }
        return new Response(Response.ResponseType.DEFAULT,true, "Справка по командам:", commandList);
    }
}
