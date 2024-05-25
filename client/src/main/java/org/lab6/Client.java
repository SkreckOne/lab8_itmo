package org.lab6;

import org.apache.logging.log4j.Logger;
import common.transfer.*;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {

    private final DatagramChannel client;
    private final InetSocketAddress serverAddress;
    private final Logger logger = getLogger(ClientMain.class);
    private final ByteBuffer buffer;

    public Client(InetAddress hostname, int port) throws IOException {
        this.buffer = ByteBuffer.allocate(4096);
        this.serverAddress = new InetSocketAddress(hostname, port);
        this.client = DatagramChannel.open();
//        this.client.configureBlocking(false);
        logger.info("DatagramChannel opened connection to " + serverAddress);
    }

    public Response sendAndReceiveCommand(Request request) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(request);
        oos.flush();

        buffer.clear();
        buffer.put(bos.toByteArray());
        buffer.flip();
        client.send(buffer, serverAddress);

        buffer.clear();

        client.receive(buffer);

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);

        Response response = (Response) ois.readObject();
        logger.info("Received response from server: " + response);
        return response;
    }


    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
        logger.info("Connection to server " + serverAddress + " closed.");
    }
}