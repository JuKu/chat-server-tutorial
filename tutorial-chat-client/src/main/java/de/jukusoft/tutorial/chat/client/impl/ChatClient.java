package de.jukusoft.tutorial.chat.client.impl;

import de.jukusoft.tutorial.chat.client.Client;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Justin on 26.10.2016.
 */
public class ChatClient implements Client {

    /**
    * vertx.io options
    */
    protected VertxOptions vertxOptions = new VertxOptions();

    /**
    * vertx.io instance
    */
    protected Vertx vertx = null;

    /**
    * network client options
    */
    protected NetClientOptions netClientOptions = new NetClientOptions();

    /**
    * network client
    */
    protected NetClient netClient = null;

    /**
    * network socket
    */
    protected NetSocket socket = null;

    /**
    * flag, if client is connected
    */
    protected AtomicBoolean connected = new AtomicBoolean(false);

    public ChatClient () {
        //set connection timeout of 5 seconds
        this.netClientOptions.setConnectTimeout(5000);

        //if connection fails, try 5x
        this.netClientOptions.setReconnectAttempts(5)
                .setReconnectInterval(500);
    }

    @Override
    public void connect(String ip, int port) throws Exception {
        //create new vertx.io instance
        this.vertx = Vertx.vertx(this.vertxOptions);

        //create new network client
        this.netClient = this.vertx.createNetClient(this.netClientOptions);

        //connect to server
        this.netClient.connect(port, ip, res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                ChatClient.this.socket = res.result();

                //set flag
                connected.set(true);
            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });
    }

    @Override
    public void shutdown() throws Exception {
        this.netClient.close();
        this.vertx.close();
    }

    @Override
    public void sendMessageToServer(String text) {
        if (this.socket == null) {
            throw new IllegalStateException("Client isnt connected yet.");
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected.get();
    }

}
