package com.practicaldime.zesty.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public abstract class Server {

    private final static Logger LOG = LoggerFactory.getLogger(Server.class);

    public static void main(String... args) {
        try {
            Server server = new Server() {
                @Override
                public void broadcast(byte[] msg) throws IOException {
                    System.out.println("<<<< " + new String(msg));
                }
            };
            //start server
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        //selectable channel for stream-oriented listening socket
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        InetSocketAddress isAddress = new InetSocketAddress("localhost", 4444);

        //bind socket to an ip address and set blocking mode 'false' (applies to any channels)
        serverSocket.bind(isAddress);
        serverSocket.configureBlocking(false);

        //multiplexer for SelectableChannel objects
        Selector selector = Selector.open();

        int ops = serverSocket.validOps();
        SelectionKey selKey = serverSocket.register(selector, ops);

        LOG.info("server listening on port 4444");

        //keep listening to selection keys
        while (serverSocket.isOpen()) {
            //await selection keys from channel that's ready for i/o
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();

            //iterate over returned set of keys and act depending on type
            for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext(); ) {
                SelectionKey key = iter.next();
                //remove selected key from the iterator
                iter.remove();

                //test if this channel is ready to accept connections
                if (key.isAcceptable()) {
                    //accept connection request and set blocking mode to 'false' (applies to any channels)
                    SocketChannel clientSocket = serverSocket.accept();
                    clientSocket.configureBlocking(false);
                    String address = (new StringBuilder(clientSocket.socket().getInetAddress().toString())).append(":").append(clientSocket.socket().getPort()).toString();

                    //set bit in selector as 'this client is ready for reading'
                    clientSocket.register(selector, SelectionKey.OP_READ, address);
                    LOG.info("connection from {} accepted and ready to receive data", address);

                    //acknowledge connection to client
                    clientSocket.write(ByteBuffer.wrap(("Welcome " + address + "! You are now connected!\n").getBytes()));
                }
                //test if the channel is ready for reading
                else if (key.isReadable()) {
                    //create accumulator for incoming bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    //retrieve read-ready channel
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    while (clientSocket.read(buffer) > 0) {
                        buffer.flip();
                        byte[] bytes = new byte[buffer.limit()];
                        buffer.get(bytes);
                        baos.write(bytes);
                        buffer.clear();
                    }
                    String incoming = new String(buffer.array()).trim();
                    LOG.info("<<<< {}", incoming);

                    if (incoming.equalsIgnoreCase(":q")) {
                        LOG.info("{} is now closing their connection", key.attachment());
                        clientSocket.close();
                    }

                    //notify through 'broadcast'
                    broadcast(baos.toByteArray());
                }
            }
        }
    }

    public abstract void broadcast(byte[] msg) throws IOException;
}
