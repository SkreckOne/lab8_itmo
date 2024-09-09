package common.console;


import java.util.Scanner;

public interface Console {


    void print(Object obj);
    void println(Object obj);
    String readln();
    void printError(Object obj);
    void prompt();
    void selectConsoleScanner();
}
