package org.lab6.utils;

import common.exceptions.ScriptRecursionException;
import common.transfer.Session;
import org.lab6.Client;
import org.lab6.commands.LoginRequiredProxy;
import org.lab6.managers.CommandManager;
import org.lab6.managers.ResponseHandler;
import common.console.Console;
import common.exceptions.ValidateExeption;
import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.io.IOException;
import java.util.*;

public class Runner {
    private final Client client;

    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
    }

    private final Console console;
    private final CommandManager commandManager;
    private final ResponseHandler responseHandler;
    private final List<String> scriptStack = new ArrayList<>();

    public Runner(Console console, Client client){
        this.client = client;
        this.console = console;
        this.commandManager = new CommandManager();
        this.responseHandler = new ResponseHandler();
    }


    public Map<ArgumentType, Object> handleArguments(ArrayList<ArgumentType> argumentTypes, String[] userCommand) throws ValidateExeption {
        Map<ArgumentType, Object> args = new HashMap<>();
        if (argumentTypes == null) return args;
        for (ArgumentType argumentType : argumentTypes) {
            switch (argumentType) {
                case ID:
                    try {
                        args.put(ArgumentType.ID, Integer.parseInt(userCommand[1]));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("ID не распознан");
                    }
                    break;
                case ORGANIZATION:
//                    args.put(ArgumentType.ORGANIZATION, InstanceFiller.fillOrganization(console, session.getUserId()));
                    break;
                case SCRIPT_NAME:
                    args.put(ArgumentType.SCRIPT_NAME, userCommand[1]);
                    break;
                case SESSION:
                    args.put(ArgumentType.SESSION, SessionHandler.getSession());
                    break;
                case FULLNAME:
                    args.put(ArgumentType.FULLNAME, userCommand[1]);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported ArgumentType: " + argumentType);
            }
        }
        return args;
    }

    public ExitCode launchCommand(String[] userCommand) throws ValidateExeption, IOException, ClassNotFoundException {
        if (userCommand[0].isEmpty()) return ExitCode.OK;
        commandManager.addHistory(userCommand[0]);
        Command command = commandManager.getCommands().get(userCommand[0]);
        if (command == null) {
            console.printError("Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
            return ExitCode.ERROR;
        }

        Map<ArgumentType, Object> args;
        try {
            args = handleArguments(command.getArgumentType(), userCommand);
        } catch (IllegalArgumentException e) {
            console.printError(e.getMessage());
            return ExitCode.ERROR;
        }

        switch (userCommand[0]) {
            case "exit" -> {
                return ExitCode.EXIT;
            }
            case "history" -> {
                console.println(commandManager.getHistory());
                return ExitCode.OK;
            }
            case "logout" -> {
                SessionHandler.setSession(null);
                console.println("Logged out successfully.");
                return  ExitCode.OK;
            }
            default -> {
                Response response = client.sendAndReceiveCommand(new Request(Request.RequestType.DEFAULT, command.getObject(), args));
                if (response == null || !response.isSuccess()) {
                    console.printError(response != null ? response.getMessage() : "Не удалось получить ответ от сервера");
                    return ExitCode.ERROR;
                }
                responseHandler.handle(console, response, SessionHandler.getSession());
            }
        }

        return ExitCode.OK;
    }
}