package common.transfer;



import common.utils.ArgumentType;
import common.utils.Command;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;


public class Request implements Serializable {
    public enum RequestType {
        SYNC,
        DEFAULT,
        LOCAL,
        ACK
    }

    private static final long serialVersionUID = 999L;
    private Command command;
    private Map<ArgumentType, Object> args;
    private RequestType requestType;


    public Request(RequestType requestType, Command command, Map<ArgumentType, Object> args) {
        this.requestType = requestType;
        this.command = command;
        this.args = args;
    }


    public Command getCommand() {
        return command;
    }
    public RequestType getRequestType(){return  requestType;}

    public Map<ArgumentType, Object> getArguments() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request response = (Request) o;
        return Objects.equals(command, response.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
    }

    @Override
    public String toString() {
        return "Request{" +
                "command='" + command + '\'' +
                '}';
    }
}