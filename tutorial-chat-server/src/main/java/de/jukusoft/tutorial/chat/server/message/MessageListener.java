package de.jukusoft.tutorial.chat.server.message;

import de.jukusoft.tutorial.chat.server.Client;

/**
 * Created by Justin on 26.10.2016.
 */
public interface MessageListener {

    /**
    * message received
     *
     * @param message chat message
    */
    public void messageReceived (Client client, ChatMessage message);

}
