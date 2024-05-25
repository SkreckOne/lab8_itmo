package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveLower extends Command {
    private static final long serialVersionUID = 309833L;
    public RemoveLower() {
        super("remove_lower", "rem el which lower than given");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() { return new ArrayList<>(List.of(ArgumentType.ORGANIZATION, ArgumentType.USERNAME));}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}