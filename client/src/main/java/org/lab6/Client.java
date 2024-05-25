package org.lab6;

import org.apache.logging.log4j.Logger;
import common.transfer.*;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {

    private final DatagramChannel client;
    private final InetSocketAddress serverAddress;
    private final Logger logger = getLogger(ClientMain.class);
    private final ByteBuffer buffer;
    private boolean waitingForAck = false;

    public Client(InetAddress hostname, int port) throws IOException {
        this.buffer = ByteBuffer.allocate(4096);
        this.serverAddress = new InetSocketAddress(hostname, port);
        this.client = DatagramChannel.open();
        this.client.configureBlocking(false);
        logger.info("DatagramChannel opened connection to " + serverAddress);
    }

    public synchronized Response sendAndReceiveCommand(Request request) throws IOException, ClassNotFoundException {
        while (waitingForAck) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for ACK", e);
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(request);
        oos.flush();
        byte[] requestBytes = bos.toByteArray();

        buffer.clear();
        buffer.put(requestBytes);
        buffer.flip();

        waitingForAck = true;
        client.send(buffer, serverAddress);
        logger.info("Request sent to server: " + request);

        long startTime = System.currentTimeMillis();
        boolean ackReceived = false;

        // Loop to wait for ACK with a timeout of 3 seconds
        while (!ackReceived) {
            buffer.clear();
            InetSocketAddress responseAddress = (InetSocketAddress) client.receive(buffer);
            if (responseAddress != null) {
                buffer.flip();
                Response receivedResponse = deserializeResponse();
                if (receivedResponse.getResponseType() == Response.ResponseType.ACK) {
                    logger.info("Received ACK from server");
                    ackReceived = true;
                    waitingForAck = false;
                    notifyAll();
                }
            }

            if (!ackReceived && System.currentTimeMillis() - startTime >= 3000) {
                logger.info("ACK not received, resending request");
                buffer.clear();
                buffer.put(requestBytes);
                buffer.flip();
                client.send(buffer, serverAddress);
                startTime = System.currentTimeMillis();
            }
        }

        // Wait for the actual response after receiving the ACK
        Response response = null;
        while (response == null) {
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
        logger.info("Connection to server " + serverAddress + " closed.");
    }
}