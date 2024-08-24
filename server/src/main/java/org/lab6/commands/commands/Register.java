package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Register extends Command {
    private static final long serialVersionUID = 8151431339829482993L;
    private transient final Console console;
    private transient final UserManager userManager;

    public Register(Console console, UserManager userManager) {
        super("register", "register user");
        this.console = console;
        this.userManager = userManager;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        Session session = (Session) args.get(ArgumentType.SESSION);
        Boolean status = userManager.register(session.getUsername(), session.getPassword());
        return new Response(Response.ResponseType.DEFAULT,status, "Registration process done");
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.SESSION));
    }
}
