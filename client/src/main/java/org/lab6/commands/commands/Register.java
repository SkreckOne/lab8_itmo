package org.lab6.commands.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Register extends Command {
    private static final long serialVersionUID = 8151431339829482993L;
    public Register() {
        super("register", "register user");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.SESSION));
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}
}
