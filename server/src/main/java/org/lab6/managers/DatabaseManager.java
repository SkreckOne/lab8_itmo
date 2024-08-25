package org.lab6.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import common.console.Console;
import common.models.*;
import org.lab6.utils.PasswordManager;
import org.lab6.utils.SqlExceptionHandler;

public class DatabaseManager {
    final private String dbUrl;
    private Connection connection;
    final private Console console;

    public DatabaseManager(String dbUrl, Console console) {
        this.dbUrl = dbUrl;
        this.console = console;
    }

    public void connect(){
        try {
            Class.forName("org.postgresql.Driver");
            Properties info = new Properties();
            info.load(new FileInputStream("db.cfg"));
            connection = DriverManager.getConnection(dbUrl, info);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            console.printError(e.getMessage());
            console.printError("Connection failed.\n");
        }
        catch (IOException e){
            console.printError(e.getMessage());
            console.printError("Configutation file failed to load.\n");
        }
        catch (ClassNotFoundException e){
            console.printError(e.getMessage());
            console.printError("DB driver not found.\n");
        }
    }


    public void readCollection(PriorityQueue<Organization> organizations){
        final String request = "SELECT * FROM lab7.Organisation JOIN lab7.Address ON lab7.Organisation.address_id = lab7.Address.address_id" +
                " JOIN lab7.Location ON lab7.Location.location_id = lab7.Address.town" +
                " JOIN lab7.Coordinates ON lab7.Coordinates.coordinates_id = lab7.Organisation.coordinates_id";
        try (Statement data = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet resultSet = data.executeQuery(request)){
            while(resultSet.next()){
                Location cLocation = new Location(resultSet.getLong("location_x"), resultSet.getLong("location_y"), resultSet.getLong("location_z"));
                Address cAddress = new Address(resultSet.getString("zip_code"), cLocation);
                Coordinates cCoordinates = new Coordinates(resultSet.getInt("coordinates_x"), resultSet.getLong("coordinates_y"));
                Organization organization = new Organization(resultSet.getInt("organisation_id"), resultSet.getString("name"), cCoordinates, resultSet.getDate("creation_date"),
                        resultSet.getLong("annual_turnover"), resultSet.getString("full_name"), OrganizationType.valueOf(resultSet.getString("organisation_type")), cAddress, resultSet.getInt("user_id"));
                organizations.add(organization);
            }
        } catch (SQLException e){
            console.printError(e);
        }
    }


    public void writeCollection(PriorityQueue<Organization> organizations) {
        organizations.stream().forEach(this::writerProvider);
    }

    public Map<String, Object> getIdAndDate(String fullName) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        final String query = "SELECT organisation_id, creation_date FROM lab7.Organisation WHERE full_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fullName);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    resultMap.put("organisation_id", resultSet.getInt("organisation_id"));
                    resultMap.put("creation_date", resultSet.getDate("creation_date"));
                }
            }
        } catch (SQLException e) {
            console.printError(e);
        }

        return resultMap;
    }

    public boolean deleteOrganization(Organization organization){
        final String request = "DELETE FROM lab7.Organisation WHERE organisation_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(request)){
            console.println("Rem org step 1: " + organization.getId());
            statement.setInt(1, organization.getId());
            int status = statement.executeUpdate();
            console.println("Rem org status: " + status);
            if (status == 0){console.printError("Object was not removed\n"); return false;}
            connection.commit();
        }catch (SQLException e){
            new SqlExceptionHandler(console).handleSqlException(e, connection);
        }
        return true;
    }


    public boolean writerProvider(Organization organization){
        boolean answ = false;
        final String existRequest = "SELECT EXISTS (SELECT 1 FROM lab7.Organisation JOIN lab7.Address ON lab7.Organisation.address_id = lab7.Address.address_id "+
                "JOIN lab7.Location ON lab7.Location.location_id = lab7.Address.town WHERE lab7.Organisation.organisation_id = ?" +
                ")";
        try (PreparedStatement status = connection.prepareStatement(existRequest);){
            status.setInt(1, organization.getId());
            ResultSet res = status.executeQuery();
            res.next();
            if (res.getBoolean("exists")){answ = updateOrganization(organization);}
            else {answ = writeOrganization(organization);}

        } catch (SQLException e){
            console.printError(e);
        }
        return answ;
    }

    private boolean updateOrganization(Organization organization){
        final String[] requests = new String[]{
                "UPDATE lab7.Organisation SET name = ?, annual_turnover = ?, full_name = ?, organisation_type = ?::lab7.organisationtype WHERE organisation_id = ?",
                "UPDATE lab7.Coordinates SET coordinates_x = ?, coordinates_y = ? WHERE coordinates_id = (SELECT coordinates_id FROM lab7.Organisation" +
                        " WHERE organisation_id = ? LIMIT 1);",
                "UPDATE lab7.Address SET zip_code = ? WHERE address_id = (SELECT address_id FROM lab7.Organisation WHERE organisation_id = ? LIMIT 1);",
                "UPDATE lab7.Location SET location_x = ?, location_y = ?, location_z = ? WHERE location_id = (SELECT town FROM lab7.Address " +
                        "WHERE address_id = (SELECT address_id FROM lab7.Organisation WHERE organisation_id = ? LIMIT 1) LIMIT 1);"

        };
        int status;
        console.println("Trying to update organization: " + organization.getFullName());
        try{
            for (int i = 0; i < 4; i++){
                PreparedStatement statement = connection.prepareStatement(requests[i], Statement.RETURN_GENERATED_KEYS);
                switch (i){
                    case 0 -> {
                        statement.setString(1, organization.getName());
                        statement.setLong(2, organization.getAnnualTurnover());
                        statement.setString(3, organization.getFullName());
                        statement.setString(4, organization.getType().name());
                        statement.setInt(5, organization.getId());
                    }

                    case 1 -> {
                        statement.setInt(1, organization.getCoordinates().getX());
                        statement.setLong(2, organization.getCoordinates().getY());
                        statement.setInt(3, organization.getId());
                    }

                    case 2 -> {
                        statement.setString(1, organization.getPostalAddress().getZipCode());
                        statement.setInt(2, organization.getId());
                    }

                    case 3 -> {
                        statement.setLong(1, organization.getPostalAddress().getTown().getX());
                        statement.setLong(2, organization.getPostalAddress().getTown().getY());
                        statement.setLong(3, organization.getPostalAddress().getTown().getZ());
                        statement.setInt(4, organization.getId());
                    }

                }
                status = statement.executeUpdate();
                if (status == 0){console.printError("Object was not updated.\n"); return false;}
                statement.close();
                connection.commit();
            }
        } catch (SQLException e){
            new SqlExceptionHandler(console).handleSqlException(e, connection);
        }
        return true;
    }

    private boolean writeOrganization(Organization organization){
        final String[] requests = new String[]{
                "INSERT INTO lab7.Location (location_x, location_y, location_z) VALUES (?, ?, ?)",
                "INSERT INTO lab7.Address (zip_code, town) VALUES (?, ?)",
                "INSERT INTO lab7.Coordinates (coordinates_x, coordinates_y) VALUES (?, ?)",
                "INSERT INTO lab7.Organisation (address_id, user_id, coordinates_id, organisation_type, name, annual_turnover, full_name)" +
                        "VALUES (?, ?, ?, ?::lab7.organisationtype, ?, ?, ?)",
        };
        final String[] idNames = new String[]{"location_id", "address_id", "coordinates_id", "organisation_id"};
        int[] ids = new int[4];
        int status;
        ResultSet generatedKeys;
        console.println("Trying to write organization: " + organization.getFullName());

        try{
            for (int i = 0; i < 4; i++){
                PreparedStatement statement = connection.prepareStatement(requests[i], Statement.RETURN_GENERATED_KEYS);
                switch (i){
                    case 0 -> {
                        statement.setLong(1, organization.getPostalAddress().getTown().getX());
                        statement.setLong(2, organization.getPostalAddress().getTown().getY());
                        statement.setLong(3, organization.getPostalAddress().getTown().getZ());
                    }

                    case 1 -> {
                        statement.setString(1, organization.getPostalAddress().getZipCode());
                        statement.setInt(2, ids[0]);
                    }

                    case 2 -> {
                        statement.setInt(1, organization.getCoordinates().getX());
                        statement.setLong(2, organization.getCoordinates().getY());
                    }
                    case 3 -> {
                        statement.setInt(1, ids[1]);
                        statement.setInt(2, organization.getOwnerId());
                        statement.setInt(3, ids[2]);
                        statement.setString(4, organization.getType().name());
                        statement.setString(5, organization.getName());
                        statement.setLong(6, organization.getAnnualTurnover());
                        statement.setString(7, organization.getFullName());
                    }
                }
                status = statement.executeUpdate();
                if (status == 0){ console.printError("Object was not updated.\n"); return false;}
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {ids[i] = generatedKeys.getInt(idNames[i]);}
                generatedKeys.close();
                statement.close();
                connection.commit();

            }
        } catch (SQLException e){
            new SqlExceptionHandler(console).handleSqlException(e, connection);
        }
        return true;
    }

    public Integer getUserIdIfExist(String username, String password){
        final String checkReq = "SELECT salt,password,user_id FROM lab7.User WHERE username = ? LIMIT 1";
        Integer user_id = null;
        try (PreparedStatement creds = connection.prepareStatement(checkReq)){
            creds.setString(1, username);
            ResultSet result = creds.executeQuery();
            if (!result.isBeforeFirst() ) {
                return null;
            }
            result.next();
            String passwordHash = PasswordManager.hashPassword(password, result.getString("salt"));
            String storedPasswordHash = result.getString("password");

            if (!storedPasswordHash.equals(passwordHash)){
                return null;
            }
            user_id = result.getInt("user_id");
            result.close();

        } catch (SQLException e){
            console.printError(e);
        }
        return user_id;
    }


    public Boolean addUser(String username, String password){
        final String addUsrReq = "INSERT INTO lab7.User (username, password, salt) VALUES (?, ?, ?)";
        try (PreparedStatement status = connection.prepareStatement(addUsrReq)) {
            String salt = PasswordManager.generateSalt(30);
            status.setString(1, username);
            status.setString(2, PasswordManager.hashPassword(password, salt));
            status.setString(3, salt);
            int result = status.executeUpdate();
            if (result != 1){return false;}
            connection.commit();

        } catch (SQLException e){
            if (connection != null) {
                try {
                    console.printError("Transaction is being rolled back\n");
                    connection.rollback();
                    return false;
                } catch (SQLException excep) {
                    console.printError(excep);
                }
            }
        }
        return true;
    }


    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            console.printError(e.getMessage());
            console.printError("Failed to close connection.\n");
        }
    }
}
