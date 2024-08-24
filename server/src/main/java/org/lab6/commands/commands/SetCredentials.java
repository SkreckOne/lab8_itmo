package org.lab6.commands.commands;


import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.lab6.commands.CommandManager;
import java.util.ArrayList;
import java.util.Map;

public class SetCredentials extends Command  {
    private static final long serialVersionUID = 5218295901L;

    public SetCredentials() {
        super("set_creds", "Set credentials for your user(login, password). " +
                "You must set credentials before using login, logout, register commands. " +
                "NOTE: if you use this command again you will create new session and automatically disconnect from this.");
    }
    @Override
    public ArrayList<ArgumentType> getArgumentType() {return new ArrayList<>();}

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        return new Response(Response.ResponseType.DEFAULT,true, "Session applied.");
    }
}
