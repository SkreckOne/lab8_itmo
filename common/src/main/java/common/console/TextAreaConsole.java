package common.console;
import javax.swing.JTextArea;

public class TextAreaConsole implements Console{
    private JTextArea outputArea;

    public TextAreaConsole(JTextArea outputArea) {
        this.outputArea = outputArea;
    }

    @Override
    public void print(Object obj) {
        outputArea.append(obj.toString());
    }

    @Override
    public void println(Object obj) {
        outputArea.append(obj.toString() + "\n");
    }

    @Override
    public String readln() {
        return "";
    }

    @Override
    public void printError(Object obj) {
        outputArea.append("[ERROR] " + obj.toString() + "\n");
    }

    @Override
    public void prompt() {

    }

    @Override
    public void selectConsoleScanner() {

    }
}
