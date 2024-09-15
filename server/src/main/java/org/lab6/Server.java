package org.lab6;

import common.transfer.Request;
import common.transfer.Response;
import common.utils.ArgumentType;
import common.utils.Command;
import org.apache.logging.log4j.Logger;
import org.lab6.commands.Invoker;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class Server {
    private final Selector selector;
    private final DatagramChannel serverSocket;
    private final Logger logger = ServerMain.logger;
    private final ByteBuffer receiveBuffer;
    private final ByteBuffer sendBuffer;
    private final Object receiveBufferLock = new Object();
    private final Object sendBufferLock = new Object();
    private volatile boolean running = true;

    private final ExecutorService acceptExecutor = new ForkJoinPool();
    private final ExecutorService processExecutor = Executors.newCachedThreadPool();
    private final ExecutorService sendExecutor = new ForkJoinPool();
    private final Invoker invoker;

    private final BlockingQueue<RequestContext> requestQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<ResponseContext> responseQueue = new LinkedBlockingQueue<>();

    private static class ResponseContext {
        private final Response response;
        private final DatagramChannel channel;
        private final InetSocketAddress clientAddress;

        public ResponseContext(Response response, DatagramChannel channel, InetSocketAddress clientAddress) {
            this.response = response;
            this.channel = channel;
            this.clientAddress = clientAddress;
        }

        public Response getResponse() {
            return response;
        }

        public DatagramChannel getChannel() {
            return channel;
        }

        public InetSocketAddress getClientAddress() {
            return clientAddress;
        }
    }

    private static class RequestContext {
        private final Request request;
        private final DatagramChannel channel;
        private final InetSocketAddress clientAddress;

        public RequestContext(Request request, DatagramChannel channel, InetSocketAddress clientAddress) {
            this.request = request;
            this.channel = channel;
            this.clientAddress = clientAddress;
        }

        public Request getRequest() {
            return request;
        }

        public DatagramChannel getChannel() {
            return channel;
        }

        public InetSocketAddress getClientAddress() {
            return clientAddress;
        }
    }

    public Server(int port, Invoker invoker) throws IOException {
        this.receiveBuffer = ByteBuffer.allocate(4096);
        this.sendBuffer = ByteBuffer.allocate(4096);
        this.selector = Selector.open();
        this.serverSocket = DatagramChannel.open();
        this.serverSocket.bind(new InetSocketAddress(port));
        this.serverSocket.configureBlocking(false);
        this.serverSocket.register(selector, SelectionKey.OP_READ);
        this.invoker = invoker;
    }

    public void run() throws IOException {
        logger.info("UDP Server started on port " + serverSocket.socket().getLocalPort());

        acceptExecutor.submit(this::acceptRequests);
        processExecutor.submit(this::processRequests);
        sendExecutor.submit(this::sendResponses);

        while (running) {
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

                    Request request = new Request(Request.RequestType.LOCAL, command, args);
                    Response response = invoker.handle(request);

                    System.out.println(response.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read command from server", e);
        }
    }

    private void acceptRequests() {
        while (running) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if (key.isReadable()) {
                        DatagramChannel channel = (DatagramChannel) key.channel();
                        InetSocketAddress clientAddress;

                        synchronized (receiveBufferLock) {
                            receiveBuffer.clear();
                            clientAddress = (InetSocketAddress) channel.receive(receiveBuffer);
                            if (clientAddress == null) {
                                continue;
                            }

                            receiveBuffer.flip();
                        }
                        Request request = deserializeRequest(receiveBuffer);
                        if (request == null) {
                            continue;
                        }
                        logger.info("Received request from client: " + request);

                        requestQueue.put(new RequestContext(request, channel, clientAddress));
                    }

                    iter.remove();
                }
            } catch (IOException | InterruptedException e) {
                logger.error("Failed to accept request", e);
            }
        }
    }

    private void processRequests() {
        while (running) {
            try {
                RequestContext requestContext = requestQueue.take();
                Request request = requestContext.getRequest();
                System.out.println("processRequest: " + request);


                Response response = processRequest(request);

                responseQueue.put(new ResponseContext(response, requestContext.getChannel(), requestContext.getClientAddress()));
            } catch (InterruptedException e) {
                logger.error("Processing interrupted", e);
            }
        }
    }

    private void sendResponses() {
        while (running) {
            try {
                ResponseContext responseContext = responseQueue.take();
                Response response = responseContext.getResponse();
                System.out.println("sendResponse: " + response.getCommands() + " " + response.getMessage());
                DatagramChannel channel = responseContext.getChannel();
                InetSocketAddress clientAddress = responseContext.getClientAddress();


                Response ackResponse = new Response(Response.ResponseType.ACK, true, "ACK");
                sendResponse(channel, clientAddress, ackResponse);

                sendResponse(channel, clientAddress, response);
            } catch (InterruptedException | IOException e) {
                logger.error("Failed to send response", e);
            }
        }
    }

    private Response processRequest(Request request) {
        return invoker.handle(request);
    }

    private Request deserializeRequest(ByteBuffer buffer) throws IOException {
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


        synchronized (sendBufferLock) {
            sendBuffer.clear();
            sendBuffer.put(bos.toByteArray());
            sendBuffer.flip();
        }

        channel.send(sendBuffer, address);
        logger.info("Sent response to client: " + response);
    }

    public void stop() {
        running = false;
        acceptExecutor.shutdown(); try {
            serverSocket.close();
            selector.close();
        } catch (IOException e) {
            logger.error("Failed to close server socket when stopping: " + e.getMessage(), e);
        }
    }
}