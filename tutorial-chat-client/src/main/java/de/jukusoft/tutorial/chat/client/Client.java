
package de.jukusoft.tutorial.chat.client;

import de.jukusoft.tutorial.chat.client.message.MessageReceiver;
import org.json.JSONObject;

/**
 * Created by Justin on 26.10.2016.
 */
public interface Client {

    /**
    * start server
    */
    public void connect (String ip, int port) throws Exception;

    /**
    * shutdown server
    */
    public void shutdown() throws Exception;

    /**
    * set message receiver
     *
     * @param messageReceiver instance of message receiver
    */
    public void setMessageReceiver (MessageReceiver messageReceiver);

    /**
    * authentificate user
     *
     * @param username name of user
    */
    public void auth (String username);

    /**
    * send message to server
     *
     * @param text text
    */
    public void sendMessageToServer (String text);

    /**
     * send message to server
     *
     * @param json json object
     */
    public void sendMessageToServer (JSONObject json);

    /**
    * execute blocking code
     *
     * @param runnable runnable to execute
    */
    public void executeBlocking (Runnable runnable);

    /**
    * check, if client is conncected
     *
     * @return true, if client is connected
    */
    public boolean isConnected ();

}
