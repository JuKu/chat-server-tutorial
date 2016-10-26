package de.jukusoft.tutorial.chat.server;

import org.json.JSONObject;

/**
 * Created by Justin on 26.10.2016.
 */
public interface Client {

    /**
    * send json object
     *
     * @param json json object
    */
    public void send (JSONObject json);

    /**
    * send string
     *
     * @param str string
    */
    public void send (String str);

    /**
    * get clientID
     *
     * @return clientID
    */
    public long getClientID ();

    /**
    * get username
     *
     * @return username
    */
    public String getUsername ();

    /**
    * check, if client is authentificated
    */
    public boolean isAuthentificated ();

    /**
    * cleanUp client, will be called if connection was closed
    */
    public void cleanUp ();

}
