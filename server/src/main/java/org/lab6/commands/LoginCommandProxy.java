package org.lab6.commands;

import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import common.utils.Command;

import java.util.ArrayList;
import java.util.Map;

public class LoginCommandProxy extends Command {
    final private Command command;

    public LoginCommandProxy(Command command){
        super("proxy", "login command proxy");
        this.command = command;
    }

    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        Session session = (Session) args.get(ArgumentType.SESSION);
        System.out.println(session);
        if (session == null) {return new Response(Response.ResponseType.DEFAULT,false, "Please login before use commands.");}
        Integer id = session.getUserId();
        if (id == null) {return new Response(Response.ResponseType.DEFAULT,false, "Please login before use commands.");}
        return command.apply(args);
    }

    public Command getObject() {
        return command;
    }

}
