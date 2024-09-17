package org.lab6.gui.models;


import common.transfer.Request;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import org.lab6.Client;
import org.lab6.commands.commands.Login;
import org.lab6.commands.commands.Register;
import org.lab6.gui.controllers.AuthFormController;
import org.lab6.utils.SessionHandler;

import java.io.IOException;
import java.util.Map;


public class AuthModel {
    private final Client connectionHandler;
    private final AuthFormController controller;

    public AuthModel(Client connectionHandler, AuthFormController controller) {
        this.connectionHandler = connectionHandler;
        this.controller = controller;
    }

    public boolean register(String login, String password) {
        Request request = new Request(Request.RequestType.DEFAULT,new Register(), Map.of(ArgumentType.SESSION, new Session(login, password)));
        try {
            Response response = connectionHandler.sendAndReceiveCommand(request);
            if (response.isSuccess()){
                SessionHandler.setSession(response.getSession());
                return true;
            }
            else {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    public boolean login(String login, String password) {
        Request request = new Request(Request.RequestType.DEFAULT, new Login(), Map.of(ArgumentType.SESSION, new Session(login, password)));
        try {
            Response response = connectionHandler.sendAndReceiveCommand(request);
            if (response.isSuccess()){
                SessionHandler.setSession(response.getSession());
                System.out.println(SessionHandler.getSession().getUserId());
                return true;
            }
            else {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    public Client getConnectionHandler() {
        return connectionHandler;
    }
}