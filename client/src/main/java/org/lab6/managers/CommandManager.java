package org.lab6.managers;
import common.console.StandardConsole;
import common.utils.Command;
import org.lab6.commands.commands.*;

import java.util.*;


public class CommandManager {
    private final Map<String, Command> commands;
    private final ArrayDeque<String> history = new ArrayDeque<>();

    public CommandManager(){
        this.commands = new LinkedHashMap<>();
        commands.put("help", new Help());
        commands.put("exit", new Exit());
        commands.put("add", new Add());
        commands.put("show", new Show());
        commands.put("clear", new Clear());
        commands.put("info", new Info());
        commands.put("remove_by_id", new RemoveById());
        commands.put("update", new Update());
        commands.put("remove_head", new RemoveHead());
        commands.put("remove_lower", new RemoveLower());
        commands.put("filter_greater_than_full_name", new FilterGreaterThanFullName());
        commands.put("filter_less_than_full_name", new FilterLessThanFullName());
        commands.put("print_descending", new PrintDescending());
        commands.put("history", new History());
        commands.put("execute_script", new ExecuteScript(new StandardConsole()));
        commands.put("set_creds", new SetCredentials(new StandardConsole()));
        commands.put("logout", new Logout());
        commands.put("check_creds", new CheckCredentials());
        commands.put("login", new Login());
        commands.put("register", new Register());

    }

    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public void addHistory(String hist_piece){
        if (history.size() == 9){
            history.pop();
        }
        history.push(hist_piece);
    }


    public String getHistory(){
        Iterator<String> nEl = history.iterator();
        StringBuilder req = new StringBuilder();
        while (nEl.hasNext()){
            req.append(nEl.next()).append("\n");
        }
        return req.toString();
    }
}