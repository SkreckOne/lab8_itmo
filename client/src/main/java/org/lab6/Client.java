package org.lab6;

import common.transfer.Request;
import common.transfer.Response;
import common.transfer.Session;
import common.utils.ArgumentType;
import common.utils.Command;
import org.apache.logging.log4j.Logger;
import common.console.Console;
import org.lab6.managers.CommandManager;
import org.lab6.utils.InstanceFiller;
import org.lab6.utils.Runner;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Client {

    private final DatagramChannel client;
    private final InetSocketAddress serverAddress;
    private final Logger logger = getLogger(ClientMain.class);
    private final ByteBuffer buffer;
    private final Selector selector;
    private final CommandManager commandManager = new CommandManager();  // Менеджер команд

    public Client(InetAddress hostname, int port) throws IOException {
        this.buffer = ByteBuffer.allocate(4096);
        this.serverAddress = new InetSocketAddress(hostname, port);
        this.client = DatagramChannel.open();
        this.client.configureBlocking(false);
        this.selector = Selector.open();
        this.client.register(selector, SelectionKey.OP_READ);
        logger.info("DatagramChannel opened connection to " + serverAddress);
    }


    public Response prepareCommand(String commandName, String userInput, Session session) throws IOException, ClassNotFoundException {

        String[] userCommand = (userInput.trim() + " ").split(" ", 2);

        if (userCommand.length < 2 || userCommand[1].trim().isEmpty()) {
            userCommand = new String[]{userCommand[0], ""};
        }

        Command command = commandManager.getCommands().get(commandName);
        if (command == null) {
            return new Response(Response.ResponseType.DEFAULT, false, "Команда '" + commandName + "' не найдена.");
        }

        Map<ArgumentType, Object> args;
        try {
            args = handleArguments(command.getArgumentType(), userCommand, session);
        } catch (IllegalArgumentException e) {
            return new Response(Response.ResponseType.DEFAULT, false, e.getMessage());
        }

        Request request = new Request(Request.RequestType.DEFAULT, command.getObject(), args);

        Response response = sendAndReceiveCommand(request);

        if (response == null) {
            return new Response(Response.ResponseType.DEFAULT, false, "Не удалось получить ответ от сервера.");
        }

        return response;
    }

    private Map<ArgumentType, Object> handleArguments(ArrayList<ArgumentType> argumentTypes, String[] userCommand, Session session) throws IllegalArgumentException {
        Map<ArgumentType, Object> args = new HashMap<>();
        if (argumentTypes == null) return args;

        for (ArgumentType argumentType : argumentTypes) {
            switch (argumentType) {
                case ID:
                    try {
                        args.put(ArgumentType.ID, Integer.parseInt(userCommand[1]));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("ID не распознан");
                    }
                    break;
                case ORGANIZATION:
//                    args.put(ArgumentType.ORGANIZATION, InstanceFiller.fillOrganization(console, session.getUserId()));
                    break;
                case SCRIPT_NAME:
                    args.put(ArgumentType.SCRIPT_NAME, userCommand[1]);
                    break;
                case SESSION:
                    args.put(ArgumentType.SESSION, session);
                    break;
                case FULLNAME:
                    args.put(ArgumentType.FULLNAME, userCommand[1]);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported ArgumentType: " + argumentType);
            }
        }
        return args;
    }

    public Response sendAndReceiveCommand(Request request) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(request);
        oos.flush();

        buffer.clear();
        buffer.put(bos.toByteArray());
        buffer.flip();

        long startTime;
        boolean ackReceived = false;

        while (!ackReceived) {
            client.send(buffer, serverAddress);
            logger.info("Request sent to server: " + request);

            startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 3000 && !ackReceived) {
                selector.select(3000);
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isReadable()) {
                        buffer.clear();
                        InetSocketAddress responseAddress = (InetSocketAddress) client.receive(buffer);
                        if (responseAddress != null) {
                            buffer.flip();
                            Response receivedResponse = deserializeResponse();
                            if (receivedResponse.getResponseType() == Response.ResponseType.ACK) {
                                logger.info("Received ACK from server");
                                ackReceived = true;
                                break;
                            }
                        }
                    }
                    iter.remove();
                }
            }

            if (!ackReceived) {
                logger.info("ACK not received, resending request");
                buffer.flip();
            }
        }

        Response response = null;
        while (response == null) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isReadable()) {
                    buffer.clear();
                    InetSocketAddress responseAddress = (InetSocketAddress) client.receive(buffer);
                    if (responseAddress != null) {
                        buffer.flip();
                        response = deserializeResponse();
                        if (response.getResponseType() != Response.ResponseType.ACK) {
                            logger.info("Received response from server: " + response);
                        }
                    }
                }
                iter.remove();
            }
        }

        return response;
    }

    private Response deserializeResponse() throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);

        return (Response) ois.readObject();
    }

    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
        if (selector != null) {
            selector.close();
        }
        logger.info("Connection to server " + serverAddress + " closed.");
    }
}