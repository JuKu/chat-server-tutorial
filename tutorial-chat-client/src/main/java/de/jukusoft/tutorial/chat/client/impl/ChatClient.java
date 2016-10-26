package de.jukusoft.tutorial.chat.client.impl;

import de.jukusoft.tutorial.chat.client.Client;
import de.jukusoft.tutorial.chat.client.message.ChatMessage;
import de.jukusoft.tutorial.chat.client.message.MessageReceiver;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
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
    * message receiver
    */
    protected MessageReceiver messageReceiver = null;

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
        if (this.messageReceiver == null) {
            throw new IllegalStateException("You have to set an message receiver first.");
        }

        //create new vertx.io instance
        this.vertx = Vertx.vertx(this.vertxOptions);

        //create new network client
        this.netClient = this.vertx.createNetClient(this.netClientOptions);

        //connect to server
        this.netClient.connect(port, ip, res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                ChatClient.this.socket = res.result();

                //initialize socket
                initSocket(socket);

                //set flag
                connected.set(true);
            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });
    }

    /**
    * initialize socket
     *
     * @param socket network socket
    */
    protected void initSocket (NetSocket socket) {
        //set connection close handler
        socket.closeHandler(res -> {
            System.err.println("Server connection was closed by server.");

            System.exit(0);
        });

        //add message handler
        socket.handler(buffer -> {
            //convert to string and json object
            String str = buffer.toString(StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(str);

            //convert to chat message
            ChatMessage msg = ChatMessage.create(json);

            //call message receiver
            messageReceiver.messageReceived(msg);
        });
    }

    @Override
    public void shutdown() throws Exception {
        this.netClient.close();
        this.vertx.close();
    }

    @Override
    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void auth(String username) {
        //create new chat message and convert to json string
        ChatMessage msg = ChatMessage.create();
        JSONObject json = msg.toJSON();
        json.put("action", "auth");
        json.put("username", username);

        //send message
        this.socket.write(json.toString());
    }

    @Override
    public void sendMessageToServer(String text) {
        if (this.socket == null) {
            throw new IllegalStateException("Client isnt connected yet.");
        }

        //create new chat message
        ChatMessage msg = ChatMessage.create(text);

        //send text
        this.socket.write(msg.toJSON().toString());
    }

    @Override
    public void sendMessageToServer(JSONObject json) {

    }

    @Override
    public void executeBlocking(Runnable runnable) {
        this.vertx.executeBlocking(future -> {
            //execute blocking code
            runnable.run();

            //task was executed
            future.complete();
        }, res -> {
            //
        });
    }

    @Override
    public boolean isConnected() {
        return this.connected.get();
    }

}
