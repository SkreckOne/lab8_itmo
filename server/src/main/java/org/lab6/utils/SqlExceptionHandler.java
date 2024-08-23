package org.lab6.utils;

import common.console.Console;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlExceptionHandler {
    private final Console console;

    public SqlExceptionHandler(Console console) {
        this.console = console;
    }

    public boolean handleSqlException(SQLException e, Connection connection) {
        console.printError(e);
        if (connection != null) {
            try {
                console.printError(e);
                console.printError("Transaction is being rolled back\n");
                connection.rollback();
                return false;
            } catch (SQLException excep) {
                console.printError(excep);
                return false;
            }
        }
        return true;
    }
}
