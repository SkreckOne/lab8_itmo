package org.lab6;

import common.utils.ArgumentType;
import common.utils.Command;
import org.apache.logging.log4j.Logger;
import common.transfer.Request;
import common.transfer.Response;
import org.lab6.commands.Invoker;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
    private final Selector selector;
    private final DatagramChannel serverSocket;
    private final Invoker invoker;
    private final Logger logger = ServerMain.logger;
    private final ByteBuffer buffer;
    private boolean running = true;

    public Server(int port, Invoker invoker) throws IOException {
        this.buffer = ByteBuffer.allocate(4096);
        this.selector = Selector.open();
        this.serverSocket = DatagramChannel.open();
        this.serverSocket.bind(new InetSocketAddress(port));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_READ);
        this.invoker = invoker;
    }

    public void run() throws IOException {
        logger.info("UDP Server started on port " + serverSocket.socket().getLocalPort());

        while (running) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                if (key.isReadable()) {
                    handleRequest(key);
                }

                iter.remove();
            }
            executeServerCommand();
        }
        stop();
    }

    private void handleRequest(SelectionKey key) {
        try {
            DatagramChannel channel = (DatagramChannel) key.channel();

            buffer.clear();
            InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);
            if (clientAddress == null) {
                return;
            }

            buffer.flip();
            Request request = deserializeRequest();
            if (request == null) {
                return;
            }
            logger.info("Received request from client: " + request);

            sendResponse(channel, clientAddress, new Response(Response.ResponseType.ACK, true, "ACK"));

            Response response;
            if (request.getRequestType() != Request.RequestType.LOCAL) {
                response = invoker.handle(request);
            } else {
                response = new Response(Response.ResponseType.DEFAULT, false, "Invalid request type");
            }

            sendResponse(channel, clientAddress, response);
        } catch (IOException e) {
            logger.error("Failed to handle request", e);
        }
    }

    private void executeServerCommand() {
        try {
            if (System.in.available() > 0) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String[] inputLine = bufferedReader.readLine().trim().split(" ", 2);

                Command command = invoker.getManager().getCommands().get(inputLine[0]);
                if (command == null) {
                    System.out.println("No such command: " + inputLine[0]);
                } else {
                    Map<ArgumentType, Object> args = new HashMap<>();

                    Request request = new Request(Request.RequestType.LOCAL, command, args);
                    Response response = invoker.handle(request);

                    System.out.println(response.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read command from server", e);
        }
    }

    private Request deserializeRequest() throws IOException {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Request) ois.readObject();
        } catch (ClassNotFoundException e) {
            logger.error("Failed to deserialize request", e);
            return null;
        }
    }

    private void sendResponse(DatagramChannel channel, InetSocketAddress address, Response response) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(response);
        }

        buffer.clear();
        buffer.put(bos.toByteArray());
        buffer.flip();

        channel.send(buffer, address);
        logger.info("Sent response to client: " + response);
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
            selector.close();
        } catch (IOException e) {
            logger.error("Failed to close server socket when stopping: " + e.getMessage(), e);
        }
    }
}
