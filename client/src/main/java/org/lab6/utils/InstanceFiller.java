package org.lab6.utils;


import common.console.Console;
import common.exceptions.*;
import common.models.*;


public class InstanceFiller {


    public static Coordinates fillCoordinates(Console console){
        Integer x;
        while (true) {
            console.print("Coordinates.x: ");
            var line = console.readln().trim();
            try{
                if (!line.isEmpty()){
                    x = Integer.parseInt(line);
                    if (x <= -465) throw new ValidateExeption("Coordinates.x must be bigger than -465.");
                    else break;
                }
                else throw new ValidateExeption("Coordinates.x musn't be null.");
            }
            catch (ValidateExeption | NumberFormatException e) {
                console.printError(e.getMessage());
            }
        }

        long y;
        while (true) {
            console.print("Coordinates.y: ");
            var line = console.readln().trim();
            try {
                y = Long.parseLong(line);
                if (y <= -493) throw new ValidateExeption("Coordinates.y must be bigger than -493.");
                else break;
            }
            catch (ValidateExeption | NumberFormatException e) {
                console.printError(e.getMessage());
            }
        }

        return new Coordinates(x, y);
    }


    public static Address fillAddress(Console console){
        String zipCode;
        while (true) {
            console.print("Address.zipCode: ");
            zipCode = console.readln().trim();
            try{
                if (!zipCode.isEmpty()){
                    if (zipCode.length() < 8) throw new ValidateExeption("Address.zipCode.length() must be big or equal than 8.");
                    else break;
                }
                else throw new ValidateExeption("Address.zipCode musn't be null.");
            }
            catch (ValidateExeption | NumberFormatException e) {
                console.printError(e.getMessage());
            }
        }
        Location town = fillLocation(console);
        return new Address(zipCode, town);
    }


    public static Location fillLocation(Console console){
        long x, y, z;
        while (true){
            console.print("Location.x: ");
            var line = console.readln().trim();
            try{
                x = Long.parseLong(line);
                break;
            }
            catch (NumberFormatException e){
                console.printError(e.getMessage());
            }
        }

        while (true){
            console.print("Location.y: ");
            var line = console.readln().trim();
            try{
                y = Long.parseLong(line);
                break;
            }
            catch (NumberFormatException e){
                console.printError(e.getMessage());
            }
        }

        while (true){
            console.print("Location.z: ");
            var line = console.readln().trim();
            try{
                z = Long.parseLong(line);
                break;
            }
            catch (NumberFormatException e){
                console.printError(e.getMessage());
            }
        }
        return new Location(x, y, z);
    }


    public static OrganizationType fillOrganizationType(Console console){
        OrganizationType r;
        while (true) {
            console.print("OrganizationType ("+OrganizationType.names()+"): ");
            var line = console.readln().trim();
            line = line.toUpperCase();
            try {
                r = OrganizationType.valueOf(line);
                break;
            } catch (IllegalArgumentException e) {
                console.printError(e.getMessage());
            }
        }
        return r;
    }

    public static Organization fillOrganization(Console console, Integer userId){
        console.println("Создание организации.");

        String name;
        while (true){
            console.print("Name: ");
            name = console.readln().trim();
            try{
                if (name.isEmpty()) throw new ValidateExeption("Name mustn't be null.");
                else break;
            }
            catch (ValidateExeption e){
                console.printError(e.getMessage());
            }
        }
        Coordinates coordinates = fillCoordinates(console);
        OrganizationType type = fillOrganizationType(console);
        Address postalAddress = fillAddress(console);
        Long annualTurnover;
        while (true){
            console.print("annualTurnover: ");
            var line = console.readln().trim();
            try{
                if (line.isEmpty()) throw new ValidateExeption("annualTurnover mustn't be null.");
                else {
                    annualTurnover = Long.parseLong(line);
                    if (annualTurnover < 0) throw new ValidateExeption("annualTurnover must be bigger than 0.");
                    else break;
                }
            }
            catch (ValidateExeption | NumberFormatException  e){
                console.printError(e.getMessage());
            }
        }
        String fullname;
        while (true){
            console.print("fullname: ");
            fullname = console.readln().trim();
            try{
                if (fullname.isEmpty()) throw new ValidateExeption("fullname mustn't be null.");
                else {
                    if (fullname.length() > 1322) throw new ValidateExeption("fullname.length() must be lower than 1322.");
                    else break;
                }
            }
            catch (ValidateExeption e){
                console.printError(e.getMessage());
            }
        }

        return new Organization(name, coordinates, annualTurnover, fullname, type, postalAddress, userId);
    }
}