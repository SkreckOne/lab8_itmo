package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveById extends Command {
    private static final long serialVersionUID = 497570;
    public RemoveById() {
        super("remove_by_id", "rem element by id");
    }

    @Override
        public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>(List.of(ArgumentType.ID, ArgumentType.USERNAME));}

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

}