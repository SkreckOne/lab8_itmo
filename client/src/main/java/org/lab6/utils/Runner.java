package org.lab6.utils;

import common.exceptions.ScriptRecursionException;
import org.lab6.Client;
import org.lab6.managers.CommandManager;
import org.lab6.managers.ResponseHandler;
import common.console.Console;
import common.exceptions.ValidateExeption;
import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;

import java.io.File;
import java.io.FileNotFoundException;
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
    private String username;


    public Runner(Console console, Client client) throws IOException, ClassNotFoundException {
        this.client = client;
        this.console = console;
        this.commandManager = new CommandManager();
        this.responseHandler = new ResponseHandler();
//        console.println(this.commandManager.getCommands());
    }

    public void login(){
        String username;
        while (true){
            console.print("Введите ваше пользовательское имя: ");
            username = console.readln().trim();
            try{
                if (username.isEmpty()) throw new ValidateExeption("fullname mustn't be null.");
                else break;
            }
            catch (ValidateExeption e){
                console.printError(e.getMessage());
            }
        }

        this.username = username;
        interactiveMode();

    }


    public void interactiveMode() {
        try {
            ExitCode commandStatus;
            String[] userCommand = {"", ""};

            do {
                console.prompt();
                userCommand = (console.readln().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                commandManager.addHistory(userCommand[0]);
                commandStatus = launchCommand(userCommand);
            } while (commandStatus != ExitCode.EXIT);

        } catch (NoSuchElementException exception) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (RuntimeException e) {
            console.printError("Непредвиденная ошибка!");
        } catch (ClassNotFoundException | IOException | ValidateExeption e) {
            throw new RuntimeException(e);
        }
    }



    public ExitCode scriptMode(String argument) {
        String[] userCommand = {"", ""};
        ExitCode commandStatus;
        scriptStack.add(argument);
        try (Scanner scriptScanner = new Scanner(new File(argument))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            console.selectFileScanner(scriptScanner);

            do {
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (scriptScanner.hasNextLine() && userCommand[0].isEmpty()) {
                    userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                if (userCommand[0].equals("execute_script")) {
                    for (String script : scriptStack) {
                        if (userCommand[1].equals(script))
                        {
                            console.selectConsoleScanner();
                            throw new ScriptRecursionException();
                        }
                    }
                }
                commandStatus = launchCommand(userCommand);
            } while (commandStatus == ExitCode.OK && scriptScanner.hasNextLine());

            console.selectConsoleScanner();
            if (commandStatus == ExitCode.ERROR && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                console.println("Проверьте скрипт на корректность введенных данных!");
            }

            return commandStatus;

        } catch (FileNotFoundException exception) {
            console.printError("Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            console.printError("Файл со скриптом пуст!");
        } catch (ScriptRecursionException exception) {
            console.printError("Скрипты не могут вызываться рекурсивно!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
            System.exit(0);
        } catch (ValidateExeption | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return ExitCode.ERROR;
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
                    args.put(ArgumentType.ORGANIZATION, InstanceFiller.fillOrganization(console, username));
                    break;
                case SCRIPT_NAME:
                    args.put(ArgumentType.ORGANIZATION, userCommand[1]);
                    break;
                case USERNAME:
                    args.put(ArgumentType.USERNAME, username);
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

    public Console getConsole(){return this.console;}


    private ExitCode launchCommand(String[] userCommand) throws  ValidateExeption, IOException, ClassNotFoundException {
        if (userCommand[0].isEmpty()) return ExitCode.OK;
        Command command = commandManager.getCommands().get(userCommand[0]);
        if (command == null) {
            console.printError("Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
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
            case "execute_script" -> {
                if (!commandManager.getCommands().get("execute_script").apply(new HashMap<>()).isSuccess()) return ExitCode.ERROR;
                else return scriptMode(userCommand[1]);
            }
            default -> {
                Map<ArgumentType, Object> args;
                try {
                    args = handleArguments(command.getArgumentType(), userCommand);
                } catch (IllegalArgumentException e) {
                    console.printError(e.getMessage());
                    return ExitCode.ERROR;
                }
                var response = (Response) client.sendAndReceiveCommand(new Request(Request.RequestType.DEFAULT,command, args));
                if (!response.isSuccess()) {
                    console.printError(response.getMessage());
                    return ExitCode.ERROR;
                }
                responseHandler.handle(console, response);
            }
        };

        return ExitCode.OK;
    }
}