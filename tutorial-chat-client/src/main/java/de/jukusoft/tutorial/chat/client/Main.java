package de.jukusoft.tutorial.chat.client;

import de.jukusoft.tutorial.chat.client.impl.ChatClient;

/**
 * Created by Justin on 26.10.2016.
 */
public class Main {

    public static void main (String[] args) {
        //create new chat client
        Client client = new ChatClient();

        //connect to server
        try {
            client.connect("localhost", 2200);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
