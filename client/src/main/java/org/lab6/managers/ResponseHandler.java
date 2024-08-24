package org.lab6.managers;


import common.console.Console;
import common.models.Organization;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.Command;

public class ResponseHandler {

    public void handle(Console console, Response response, Session session) {
        if (response.isSuccess()) {
            if (response.getMessage() != null) console.println(response.getMessage());

            if (response.getOrganizations() != null && !response.getOrganizations().isEmpty()) {
                for (Organization organization : response.getOrganizations()) {
                    console.println(organization);
                }
            }
            if (response.getCommands() != null && !response.getCommands().isEmpty()) {

                for (Command command : response.getCommands()) {
                    console.println(command.getName() + " " + command.getDescr());
                }
            }

            if (response.getSession() != null){
                session.setUserId(response.getSession().getUserId());
            }

        } else {
            console.printError(response.getMessage());
        }
    }
}

