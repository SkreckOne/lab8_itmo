package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveHead extends Command {
    private static final long serialVersionUID = 595733L;
    public RemoveHead() {
        super("remove_head", "rem 1 el in col and print it");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}