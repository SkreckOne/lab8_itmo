package org.lab6.commands.commands;


import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Clear extends Command {
    private static final long serialVersionUID = 392616;
    public Clear() {
        super("clear", "clear collection elements");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() { return new ArrayList<>(List.of(ArgumentType.USERNAME));}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}