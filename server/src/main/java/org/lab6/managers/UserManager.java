package org.lab6.managers;


public class UserManager {
    final private DatabaseManager dbManager;

    public UserManager(DatabaseManager dbManager){
        this.dbManager = dbManager;
    }

    public Integer login(String username, String password){
        return dbManager.getUserIdIfExist(username, password);
    }

    public Boolean register(String username, String password){
        return dbManager.addUser(username, password);
    }

}
