package org.lab6.commands;

import common.console.Console;
import common.utils.Command;
import org.lab6.managers.CollectionManager;
import org.lab6.commands.commands.*;
import org.lab6.managers.UserManager;

import java.util.*;

public class CommandManager {
    private final Map<String, Command> commands;

    public CommandManager(CollectionManager collectionManager, Console console, UserManager userManager){
        this.commands = new LinkedHashMap<>();
        commands.put("help", new Help(this));
        commands.put("exit", new Exit());
        commands.put("add",  new LoginCommandProxy(new Add(console, collectionManager)));
        commands.put("show",  new LoginCommandProxy(new Show(console, collectionManager)));
        commands.put("clear",  new LoginCommandProxy(new Clear(console, collectionManager)));
        commands.put("info",  new LoginCommandProxy(new Info(console, collectionManager)));
        commands.put("remove_by_id",  new LoginCommandProxy(new RemoveById(console, collectionManager)));
        commands.put("update",  new LoginCommandProxy(new Update(console, collectionManager)));
        commands.put("remove_head",  new LoginCommandProxy(new RemoveHead(console, collectionManager)));
        commands.put("remove_lower",  new LoginCommandProxy(new RemoveLower(console, collectionManager)));
        commands.put("filter_greater_than_full_name",  new LoginCommandProxy(new FilterGreaterThanFullName(console, collectionManager)));
        commands.put("filter_less_than_full_name",  new LoginCommandProxy(new FilterLessThanFullName(console, collectionManager)));
        commands.put("print_descending",  new LoginCommandProxy(new PrintDescending(console, collectionManager)));
        commands.put("history", new History());
        commands.put("save", new Save(console, collectionManager));
        commands.put("set_creds",  new LoginCommandProxy(new SetCredentials()));
        commands.put("logout", new Logout());
        commands.put("check_creds", new CheckCredentials());
        commands.put("login", new Login(console, userManager));
        commands.put("register", new Register(console, userManager));
    }

    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public List<Command> getCommandsList(){
        List<Command> res = new ArrayList<>();
        for (String key: commands.keySet()) {
            res.add(commands.get(key));
        }
        return res;
    }

}