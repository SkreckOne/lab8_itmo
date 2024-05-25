package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Show extends Command {
    private static final long serialVersionUID = 315331L;
    public Show() {
        super("help", "list of all commands available");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>(List.of(ArgumentType.USERNAME));}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}