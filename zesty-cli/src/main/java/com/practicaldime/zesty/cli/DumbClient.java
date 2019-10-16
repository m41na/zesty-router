package com.practicaldime.zesty.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class DumbClient {

    private final static Logger LOG = LoggerFactory.getLogger(DumbClient.class);
    private final static AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String...args) throws IOException {
        //connect client to server instance
        InetSocketAddress isAddress = new InetSocketAddress("localhost", 4444);
        SocketChannel clientSocket = SocketChannel.open(isAddress);

        LOG.info("connected to server on port 4444");

        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
            String line = "";
            while((line = br.readLine()) != null){
                if(line.equalsIgnoreCase(":q")){
                    clientSocket.write(ByteBuffer.wrap(line.getBytes()));
                    break;
                }
                else{
                    clientSocket.write(ByteBuffer.wrap(line.getBytes()));
                    LOG.info(">>>> {}", line);
                }
            }
            clientSocket.close();
        }
    }
}
