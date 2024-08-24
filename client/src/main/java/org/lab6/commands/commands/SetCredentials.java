package org.lab6.commands.commands;

import common.exceptions.ValidateExeption;
import common.transfer.Session;
import common.utils.Command;


import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;


import common.console.Console;
import java.util.ArrayList;
import java.util.Map;


public class SetCredentials extends Command {
    private static final long serialVersionUID = 5218295901L;
    private final Console console;

    public SetCredentials(Console console) {
        super("set_creds", "Set credentials for your user(login, password). " +
                "You must set credentials before using login, logout, register commands. " +
                "NOTE: if you use this command again you will create new session and automatically disconnect from this.");
        this.console = console;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args) {
        String username;
        String password;
        while (true){
            console.print("Username: ");
            username = console.readln().trim();
            try{
                if (username.isEmpty()) throw new ValidateExeption("Username mustn't be null.");
                else break;
            }
            catch (ValidateExeption e){
                console.printError(e.getMessage());
            }
        }
        while (true){
            console.print("Password: ");
            password = console.readln().trim();
            try{
                if (password.isEmpty()) throw new ValidateExeption("Password mustn't be null.");
                else break;
            }
            catch (ValidateExeption e){
                console.printError(e.getMessage());
            }
        }
        Session session = new Session(username, password);
        return new Response(Response.ResponseType.DEFAULT,true, "Session created successfully", session);

    }

    public ArrayList<ArgumentType> getArgumentType() {
        return new ArrayList<>();
    };

}

