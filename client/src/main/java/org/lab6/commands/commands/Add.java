package org.lab6.commands.commands;



import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Add extends Command {
    private static final long serialVersionUID = 475358;
    public Add() {
        super("add", "add element");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() { return new ArrayList<>(List.of(ArgumentType.ORGANIZATION));}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}
