package org.lab6.commands.commands;



import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import java.util.ArrayList;
import java.util.Map;

public class Help extends Command {
    private static final long serialVersionUID = 1000L;
    public Help() {
        super("help", "list of all commands available");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}
