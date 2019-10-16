package com.practicaldime.zesty.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Client {

    private final static Logger LOG = LoggerFactory.getLogger(DumbClient.class);
    private static final int READ_BUFFER_SIZE = 0x100000;
    private static final int WRITE_BUFFER_SIZE = 0x100000;
    private SocketChannel clientSocket;
    private Selector selector;
    private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUFFER_SIZE); // 1Mb
    private ByteBuffer writeBuf = ByteBuffer.allocateDirect(WRITE_BUFFER_SIZE); // 1Mb
    private AtomicLong bytesOut = new AtomicLong(0L);
    private AtomicLong bytesIn = new AtomicLong(0L);

    public static void main(String... args) throws IOException {
        Client client = new Client() {
            @Override
            public void onConnected() {
                System.out.println("client connection established");
            }

            @Override
            public void onRead(ByteBuffer readBuf) {
                System.out.println(readBuf.toString());
            }
        };

        client.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase(":q")) {
                    client.send(ByteBuffer.wrap(line.getBytes()));
                    break;
                } else {
                    client.send(ByteBuffer.wrap(line.getBytes()));
                    LOG.info(">>>> {}", line);
                }
            }
            client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", 4444);
        clientSocket = SocketChannel.open();
        clientSocket.configureBlocking(false);

        LOG.info("connected to server on port 4444");

        //multiplexer for SelectableChannel objects
        selector = Selector.open();

        //establish connection
        clientSocket.connect(address);
        clientSocket.register(selector, SelectionKey.OP_CONNECT);

        while (clientSocket.isOpen()) { // events multiplexing loop
            if (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();

                //iterate over returned set of keys and act depending on type
                for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext(); ) {
                    SelectionKey key = iter.next();
                    //remove selected key from the iterator
                    iter.remove();

                    if (key.isReadable()) processRead(key);
                    if (key.isWritable()) processWrite(key);
                    if (key.isConnectable()) processConnect(key);
                    if (key.isAcceptable()) ;
                }
            }
        }
    }

    public void send(ByteBuffer buffer) throws InterruptedException, IOException {
        writeBuf.put(buffer);
        writeBuf.flip();
        int bytesOp = 0, bytesTotal = 0;
        while (writeBuf.hasRemaining() && (bytesOp = clientSocket.write(writeBuf)) > 0) bytesTotal += bytesOp;
        writeBuf.compact();

        if (writeBuf.hasRemaining()) {
            SelectionKey key = clientSocket.keyFor(selector);
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            selector.wakeup();
        }
    }

    public void close() throws IOException {
        clientSocket.close();
    }

    private void processConnect(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        if (ch.finishConnect()) {
            key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            onConnected();
        }
    }

    private void processWrite(SelectionKey key) throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(WRITE_BUFFER_SIZE);
        WritableByteChannel ch = (WritableByteChannel) key.channel();

        int bytesOp = 0, bytesTotal = 0;
        while (writeBuf.hasRemaining() && (bytesOp = ch.write(writeBuf)) > 0) bytesTotal += bytesOp;

        bytesOut.addAndGet(bytesTotal);

        if (writeBuf.remaining() == 0) {
            key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
        }

        if (bytesTotal > 0) writeBuf.notify();
        else if (bytesOp == -1) {
            LOG.info("peer closed write channel");
            ch.close();
        }

        writeBuf.compact();
    }

    private void processRead(SelectionKey key) throws IOException {
        ReadableByteChannel ch = (ReadableByteChannel) key.channel();

        int bytesOp = 0, bytesTotal = 0;
        while (readBuf.hasRemaining() && (bytesOp = ch.read(readBuf)) > 0) bytesTotal += bytesOp;

        if (bytesTotal > 0) {
            readBuf.flip();
            onRead(readBuf);
            readBuf.compact();
        } else if (bytesOp == -1) {
            LOG.info("peer closed read channel");
            ch.close();
        }

        bytesIn.addAndGet(bytesTotal);
    }

    public abstract void onConnected();

    public abstract void onRead(ByteBuffer readBuf);
}
