package common.console;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StandardConsole implements Console {
    private static final String PROMPT = "$ ";

    private static Scanner fileScanner = null;
    private static Scanner defScanner = new Scanner(System.in);


    @Override
    public void print(Object obj) {
        System.out.print(obj);
    }


    @Override
    public void println(Object obj) {
        System.out.println(obj);
    }


    @Override
    public void printError(Object obj) {
        System.err.println("Error: " + obj);
    }


    @Override
    public String readln() throws NoSuchElementException, IllegalStateException {
        return (fileScanner!=null?fileScanner:defScanner).nextLine();
    }

    @Override
    public void prompt() {
        print(PROMPT);
    }


    @Override
    public void selectConsoleScanner() {
        this.fileScanner = null;
    }
}
