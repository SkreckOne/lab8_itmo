package org.lab6.collection;

import java.sql.*;
import java.util.PriorityQueue;

import common.console.Console;
import common.models.Organization;

public class DatabaseManager {
    final private String dbUrl;
    final private String dbUser;
    final private String dbPassword;
    private Connection connection;
    final private Console console;

    public DatabaseManager(String dbUrl, String dbUser, String dbPassword, Console console) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.console = console;
    }

    public void connect(){
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            console.printError(e.getMessage());
            console.printError("Connection failed.");
        }
    }


    public void readCollection(PriorityQueue<Organization> organizations){

    }


    public void writeCollection(PriorityQueue<Organization> organizations){

    }


    public void writeOrganization(Organization organization){

    }

    public Boolean checkUser(String username, String password){
        return true;
    }

    public Boolean addUser(String username, String password){
        return true;
    }


    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            console.printError(e.getMessage());
            console.printError("Failed to close connection.");
        }
    }
}
