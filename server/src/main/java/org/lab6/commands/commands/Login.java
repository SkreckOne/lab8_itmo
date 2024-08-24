package org.lab6.commands.commands;

import common.console.Console;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.managers.CollectionManager;
import org.lab6.managers.UserManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Login extends Command {
    private static final long serialVersionUID = 8055291282570239288L;
    private transient final Console console;
    private transient final UserManager userManager;

    public Login(Console console, UserManager userManager) {
        super("login", "login user");
        this.console = console;
        this.userManager = userManager;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        Session session = (Session) args.get(ArgumentType.SESSION);
            Integer id = userManager.login(session.getUsername(), session.getPassword());
            if (id == null) {
                console.println("Login failed: " + session.getUsername() + " " + session.getPassword());
                return new Response(Response.ResponseType.DEFAULT,false, "Login failed", session);
            } else {
                console.println("Login success: " + session.getUsername() + " " + session.getPassword());
                session.setUserId(id);
                return new Response(Response.ResponseType.DEFAULT,true, "Login success", session);
            }
    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>(List.of(ArgumentType.SESSION));
    }
}
