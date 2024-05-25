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
                    handle_request(key);
                }

                iter.remove();
            }
            executeServerCommand();
        }
        stop();
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

                    Request request = new Request(Request.RequestType.LOCAL, command , args);
                    Response response = invoker.handle(request);

                    System.out.println(response.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read command from server", e);
        }
    }

    private void handle_request(SelectionKey key) throws IOException {
        System.out.println("req recived");
        DatagramChannel channel = (DatagramChannel) key.channel();

        buffer.clear();

        var clientAddress = channel.receive(buffer);
        if (clientAddress == null) {
            return;
        }

        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        try {
            Request request = (Request) ois.readObject();
            System.out.println(request + " " + request.getRequestType());
            logger.info("Processing request: " + request);
            System.out.println(request.getCommand());
            if (request.getRequestType() != Request.RequestType.LOCAL){
            Response response;
            response = invoker.handle(request);

            System.out.println(response.getCommands());

            oos.writeObject(response);
            oos.flush();

            buffer.clear();
            buffer.put(bos.toByteArray());
            buffer.flip();
            System.out.println(clientAddress);
            channel.send(buffer, clientAddress);
            System.out.println(response.getCommands());}
        } catch (ClassNotFoundException | InvalidClassException e) {
            logger.error("Failed to read object", e);
        }
    }


    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Failed to close server socket when stopping: " + e.getMessage(), e);
        }
    }
}