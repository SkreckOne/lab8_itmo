package org.lab6;

import org.apache.logging.log4j.Logger;
import common.transfer.*;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Client {

    private final DatagramChannel client;
    private final InetSocketAddress serverAddress;
    private final Logger logger = getLogger(ClientMain.class);
    private final ByteBuffer buffer;
    private final Selector selector;

    public Client(InetAddress hostname, int port) throws IOException {
        this.buffer = ByteBuffer.allocate(4096);
        this.serverAddress = new InetSocketAddress(hostname, port);
        this.client = DatagramChannel.open();
        this.client.configureBlocking(false);
        this.selector = Selector.open();
        this.client.register(selector, SelectionKey.OP_READ);
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