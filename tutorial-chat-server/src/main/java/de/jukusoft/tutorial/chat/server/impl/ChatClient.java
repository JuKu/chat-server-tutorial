package de.jukusoft.tutorial.chat.server.impl;

import de.jukusoft.tutorial.chat.server.Client;
import de.jukusoft.tutorial.chat.server.message.ChatMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Justin on 26.10.2016.
 */
public class ChatClient implements Client {

    /**
    * last client ID
    */
    protected static AtomicLong lastID = new AtomicLong(0);

    /**
    * network socket to client
    */
    protected NetSocket socket = null;

    /**
    * clientID
    */
    protected long clientID = 0;

    /**
    * name of user
    */
    protected String username = "";

    /**
    * flag, if user is authentificated
    */
    protected AtomicBoolean isAuthentificated = new AtomicBoolean(false);

    public ChatClient (NetSocket socket) {
        this.socket = socket;

        //generate new clientID
        this.clientID = this.lastID.incrementAndGet();

        //add message handler
        this.socket.handler(buffer -> {
            try {
                //call message received listener
                messageReceived(buffer);
            } catch (Exception e) {
                //print exception
                System.err.println("exception while reading message from client " + this.clientID + ":");
                e.printStackTrace();
            }
        });

        //send welcome message
        this.sendWelcomeMessage();
    }

    @Override
    public void send(JSONObject json) {
        //send json to client
        this.send(json.toString());
    }

    @Override
    public void send(String str) {
        //send string to client
        this.socket.write(str);
    }

    @Override
    public long getClientID() {
        return this.clientID;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAuthentificated() {
        return this.isAuthentificated.get();
    }

    @Override
    public void cleanUp() {
        //
    }

    /**
    * message received
    */
    protected void messageReceived (Buffer buffer) {
        //convert to string and json object
        String str = buffer.toString(StandardCharsets.UTF_8);

        //remove whitespaces at begin and end
        str = str.trim();

        //check, if message is an json message
        if (!str.startsWith("{") || !str.endsWith("}")) {
            //no json message
            System.err.println("imvalid json message: " + str);

            return;
        }

        JSONObject json = new JSONObject(str);

        if (json.has("action") && json.getString("action").equals("auth") && json.has("username")) {
            //get username
            String username = json.getString("username");

            //set username
            this.username = username;

            //you can add right authorization here

            //set authentification state
            this.isAuthentificated.set(true);

            System.out.println("[Login] user " + this.clientID + " (" + this.username + ") logged in.");

            //send result
            ChatMessage resMsg = ChatMessage.create(0, "system", "You are not logged in!");
            JSONObject resJSON = resMsg.toJSON();
            resJSON.put("action", "auth_res");
            resJSON.put("res", "success");
            this.send(resJSON);

            return;
        }

        if (!this.isAuthentificated()) {
            System.err.println("client " + this.clientID + " isnt authentificated, drop message now.");

            return;
        }

        //create message object
        ChatMessage message = ChatMessage.create(this.clientID, this.username, json);

        //log
        String log = "message received from clientID " + this.clientID + "";

        if (!this.username.isEmpty()) {
            log += " (" + this.username + ")";
        }

        log += ": " + message.getText();

        System.out.println(log);
    }

    /**
    * send welcome message to client
    */
    protected void sendWelcomeMessage () {
        //create new chat message
        ChatMessage msg = ChatMessage.create(0, "system", "Welcome");

        //convert to JSON object
        JSONObject json = msg.toJSON();

        //add chat server version
        json.put("chatserver_version", ChatServer.SERVER_VERSION);

        //send message
        this.send(json);
    }

}
