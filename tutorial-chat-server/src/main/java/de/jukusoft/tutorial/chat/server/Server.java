package de.jukusoft.tutorial.chat.server;

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
    * start server
    */
    public void start () throws Exception;

    /**
    * shutdown server
    */
    public void shutdown () throws Exception;

}
