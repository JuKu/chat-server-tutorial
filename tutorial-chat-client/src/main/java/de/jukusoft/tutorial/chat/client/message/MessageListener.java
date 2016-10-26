package de.jukusoft.tutorial.chat.client.message;

/**
 * Created by Justin on 26.10.2016.
 */
public interface MessageListener {

    /**
    * message received
     *
     * @param message chat message
    */
    public void messageReceived(ChatMessage message);

}
