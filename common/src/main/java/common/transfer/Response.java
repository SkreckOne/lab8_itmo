package common.transfer;


import common.models.Organization;
import common.utils.Command;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class Response implements Serializable {
    public enum ResponseType {
        DEFAULT,
        ACK
    }
    private static final long serialVersionUID = 666;
    private final boolean success;
    private final String message;
    private final PriorityQueue<Organization> organizations;
    private final ArrayList<Command> commands;
    private final ResponseType responseType;
    private final Session session;

    public Response(ResponseType responseType,boolean success, String message, PriorityQueue<Organization> organizations) {
        this.success = success;
        this.message = message;
        this.organizations = organizations;
        this.commands = null;
        this.responseType = responseType;
        this.session = null;
    }

    public Response(ResponseType responseType, boolean success, String message, ArrayList<Command> commands) {
        this.success = success;
        this.message = message;
        this.organizations = null;
        this.commands = commands;
        this.responseType = responseType;
        this.session = null;
    }

    public Response(ResponseType responseType, boolean success, String message) {
        this.success = success;
        this.message = message;
        this.organizations = null;
        this.commands = null;
        this.responseType = responseType;
        this.session = null;
    }

    public Response(ResponseType responseType, boolean success, String message, Session session) {
        this.success = success;
        this.message = message;
        this.organizations = null;
        this.commands = null;
        this.responseType = responseType;
        this.session = session;
    }

    public PriorityQueue<Organization> getOrganizations() {
        return this.organizations;
    }

    public String getMessage() {
        return message;
    }

    public Session getSession(){return session;}

    public ResponseType getResponseType() {return this.responseType;}

    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return Objects.equals(message, response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, organizations);
    }

    @Override
    public String toString() {
        return "Response{" +
                "success='" + success + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

}