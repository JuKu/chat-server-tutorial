package de.jukusoft.tutorial.chat.server;

import de.jukusoft.tutorial.chat.server.message.MessageListener;

/**
 * Created by Justin on 26.10.2016.
 */
public interface Server {

    /**
    * set server port
     *
     * @param port server port
    */
    public void setPort (int port);

    /**
    * set message listener
     *
     * @param listener message listener
    */
    public void setMessageListener (MessageListener listener);

    /**
    * start server
    */
    public void start () throws Exception;

    /**
    * shutdown server
    */
    public void shutdown () throws Exception;

}
