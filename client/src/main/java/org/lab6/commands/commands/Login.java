package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Login extends Command {
    private static final long serialVersionUID = 8055291282570239288L;
    public Login() {
        super("login", "login user");
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.SESSION));
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}
}
