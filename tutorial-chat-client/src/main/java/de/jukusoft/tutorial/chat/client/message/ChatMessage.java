package de.jukusoft.tutorial.chat.client.message;

import org.json.JSONObject;

/**
 * Created by Justin on 26.10.2016.
 */
public class ChatMessage {

    /**
    * sender clientID
    */
    protected long clientID = 0;

    /**
    * username of sender
    */
    protected String username = "";

    /**
    * text of message
    */
    protected String text = "";

    /**
    * message action
    */
    protected String action = "add_message";

    /**
     * result string for actions
     */
    protected String res = "";

    protected ChatMessage () {
        //
    }

    /**
    * get clientID
     *
     * @return clientID
    */
    public long getClientID () {
        return this.clientID;
    }

    /**
    * get username
     *
     * @return username
    */
    public String getUsername () {
        return this.username;
    }

    /**
    * get text
     *
     * @return text
    */
    public String getText () {
        return this.text;
    }

    /**
    * get action
     *
     * @return action
    */
    public String getAction () {
        return this.action;
    }

    /**
     * get result
     *
     * @return result
     */
    public String getResult () {
        return this.res;
    }

    public JSONObject toJSON () {
        //create new json object
        JSONObject json = new JSONObject();

        //add attributes
        json.put("action", "add_message");
        json.put("clientID", this.clientID);
        json.put("username", this.username);
        json.put("text", this.text);
        json.put("res", this.res);

        return json;
    }

    public static ChatMessage create (JSONObject json) {
        //create new chat message
        final ChatMessage msg = new ChatMessage();

        //parse message
        if (json.has("text")) {
            msg.text = json.getString("text");
        }

        if (json.has("clientID")) {
            msg.clientID = json.getLong("clientID");
        }

        if (json.has("username")) {
            msg.username = json.getString("username");
        }

        if (json.has("action")) {
            msg.action = json.getString("action");
        }

        if (json.has("res")) {
            msg.res = json.getString("res");
        }

        return msg;
    }

    public static ChatMessage create (final long clientID, final String username, JSONObject json) {
        //create new chat message
        final ChatMessage msg = new ChatMessage();

        //set clientID and username
        msg.clientID = clientID;
        msg.username = username;

        //parse message
        msg.action = json.getString("action");

        if (json.has("text")) {
            msg.text = json.getString("text");
        }

        if (json.has("action")) {
            msg.action = json.getString("action");
        }

        if (json.has("res")) {
            msg.res = json.getString("res");
        }

        return msg;
    }

    public static ChatMessage create (final long clientID, final String username, String text) {
        //create new chat message
        final ChatMessage msg = new ChatMessage();

        //set clientID and username
        msg.clientID = clientID;
        msg.username = username;
        msg.text = text;

        return msg;
    }

    public static ChatMessage create (String text) {
        //create new chat message
        ChatMessage msg = new ChatMessage();

        //set text
        msg.text = text;

        return msg;
    }

    public static ChatMessage create () {
        //create new chat message
        final ChatMessage msg = new ChatMessage();

        return msg;
    }

}
