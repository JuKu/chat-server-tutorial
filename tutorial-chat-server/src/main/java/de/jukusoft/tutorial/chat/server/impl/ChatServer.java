package de.jukusoft.tutorial.chat.server.impl;

import de.jukusoft.tutorial.chat.server.Client;
import de.jukusoft.tutorial.chat.server.Server;
import de.jukusoft.tutorial.chat.server.message.ChatMessage;
import de.jukusoft.tutorial.chat.server.message.MessageListener;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Justin on 26.10.2016.
 */
public class ChatServer implements Server {

    /**
    * instance of vertx
    */
    protected Vertx vertx = null;

    /**
    * vertx.io options
    */
    protected VertxOptions vertxOptions = new VertxOptions();

    /**
    * options of TCP network server
    */
    protected NetServerOptions netServerOptions = new NetServerOptions();

    /**
    * instance of TCP network server
    */
    protected NetServer netServer = null;

    /**
    * map with client instances
    */
    protected Map<Long,Client> clientMap = new ConcurrentHashMap<>();

    /**
    * instance of message listener
    */
    protected MessageListener messageReceiver = null;

    public static final String SERVER_VERSION = "1.0.0a";

    public ChatServer () {
        //set number of threads
        this.vertxOptions.setEventLoopPoolSize(2);
        this.vertxOptions.setWorkerPoolSize(2);
    }

    @Override
    public void setPort (int port) {
        //set port
        this.netServerOptions.setPort(port);
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        this.messageReceiver = listener;
    }

    @Override
    public void start() throws Exception {
        if (this.messageReceiver == null) {
            throw new IllegalStateException("set message receiver first.");
        }

        //create new vertx.io instance
        this.vertx = Vertx.vertx(this.vertxOptions);

        //create network server
        this.netServer = this.vertx.createNetServer(this.netServerOptions);

        //set connection handler
        this.netServer.connectHandler(socket -> {
            System.out.println("new connection accepted, ip: " + socket.remoteAddress().host() + ", port: " + socket.remoteAddress().port());

            //add client
            addClient(socket);
        });

        //start network server
        this.netServer.listen(res -> {
            if (res.succeeded()) {
                System.out.println("Chat Server is now listening on port " + res.result().actualPort());
            } else {
                System.err.println("Couldnt start network server.");
            }
        });

    }

    @Override
    public void shutdown() throws Exception {
        //close network server
        this.netServer.close(res -> {
            if (res.succeeded()) {
                System.out.println("Server was shutdown now.");

                //close vertx.io
                this.vertx.close();
            } else {
                System.out.println("Server couldnt be closed.");
            }
        });
    }

    /**
     * list all clients
     */
    public List<Client> listAllClients () {
        //create new list
        List<Client> list = new ArrayList<>();

        //iterate through all clients
        for (Map.Entry<Long,Client> entry : this.clientMap.entrySet()) {
            //add client to list
            list.add(entry.getValue());
        }

        return list;
    }

    /**
    * add client
     *
     * @param socket network socket
    */
    protected void addClient (NetSocket socket) {
        //create new client instance
        ChatClient client = new ChatClient(socket, this.messageReceiver);

        //set close handler
        socket.closeHandler(v -> {
            //remove client from map
            clientMap.remove(client.getClientID());

            //cleanUp client
            client.cleanUp();
        });

        //put client to map
        this.clientMap.put(client.getClientID(), client);
    }

    /**
    * send broadcast message to all clients
    */
    public void broadcastChatMessage (ChatMessage msg) {
        //convert to json object
        JSONObject json = msg.toJSON();

        //iterate through all clients
        for (Map.Entry<Long,Client> entry : this.clientMap.entrySet()) {
            //check first, if client is authentificated
            if (entry.getValue().isAuthentificated()) {
                //send message
                entry.getValue().send(json);
            }
        }
    }

}
