package org.lab6.commands;

import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginRequiredProxy extends Command {
    final private Command command;

    public LoginRequiredProxy(Command command){
        super("login_req_proxy", "blablabla");
        this.command = command;
    }

    @Override
    public Response apply(Map<ArgumentType, Object> args){return null;}

    @Override
    public ArrayList<ArgumentType> getArgumentType() {
        var commandArgs = command.getArgumentType();
        commandArgs.add(ArgumentType.SESSION);
        return commandArgs;
    }

    @Override
    public Command getObject() {
        return command;
    }
}
