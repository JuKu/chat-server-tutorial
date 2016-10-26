
package de.jukusoft.tutorial.chat.client;

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
    * send message to server
    */
    public void sendMessageToServer (String text);

    /**
    * check, if client is conncected
     *
     * @return true, if client is connected
    */
    public boolean isConnected ();

}
