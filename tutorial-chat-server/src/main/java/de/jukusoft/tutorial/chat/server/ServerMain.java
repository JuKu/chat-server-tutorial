package de.jukusoft.tutorial.chat.server;

import de.jukusoft.tutorial.chat.server.impl.ChatServer;
import de.jukusoft.tutorial.chat.server.message.ChatMessage;
import de.jukusoft.tutorial.chat.server.message.MessageListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Justin on 26.10.2016.
 */
public class ServerMain {

    public static void main (String[] args) {
        //create new chat server
        ChatServer server = new ChatServer();

        //set port
        server.setPort(2200);

        //set message receiver
        server.setMessageListener((Client client, ChatMessage message) -> {
            //broadcast message to all other clients
            server.broadcastChatMessage(message, client.getClientID());
        });

        //start server
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Error while starting vertx.io server: ");

            //print stacktrace
            e.printStackTrace();

            System.err.println("quit server now");
            System.exit(0);
        }

        /**
        * console
        */

        try {
            //wait 500ms, while server is starting
            Thread.currentThread().sleep(500l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //create new buffered reader to read from keyboard
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line = "";

        while (true) {
            System.out.print("> ");

            //read line
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();

                continue;
            }

            switch (line) {
                case "/help":
                    System.out.println("-------- Help --------\r\n/quit - Close the chat server\r\n/listuser - lists all users");

                    break;

                //close server
                case "/quit":
                    System.out.println("quit server now.");

                    //shutdown server
                    try {
                        server.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.exit(0);

                    break;

                //list all users in chat room
                case "/listuser":
                    //get list with all clients
                    List<Client> clients = server.listAllClients();

                    String str = "-------- " + clients.size() + " users in chat --------\r\n";

                    //iterate through all clients
                    for (Client client : clients) {
                        //check, if client is authentificated
                        if (client.isAuthentificated()) {
                            str += " - " + client.getUsername() + " (clientID: " + client.getClientID() + ")\r\n";
                        } else {
                            str += " - <not logged in> (clientID: " + client.getClientID() + ")\r\n";
                        }
                    }

                    //print text
                    System.out.println(str);

                    break;
            }
        }
    }

}
