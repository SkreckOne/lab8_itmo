package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterGreaterThanFullName extends Command {
    private static final long serialVersionUID = 255243L;
    public FilterGreaterThanFullName() {
        super("filter_greater_than_full_name fullName", "rt");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>(List.of(ArgumentType.FULLNAME));}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}