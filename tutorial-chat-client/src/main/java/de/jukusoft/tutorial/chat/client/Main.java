package de.jukusoft.tutorial.chat.client;

import de.jukusoft.tutorial.chat.client.impl.ChatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Justin on 26.10.2016.
 */
public class Main {

    public static void main (String[] args) {
        //create new chat client
        Client client = new ChatClient();

        //create new buffered reader to read from keyboard
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        client.setMessageReceiver(message -> {
            //check, if message is authorization message
            if (message.getAction().equals("request_auth")) {
                //because reader.readLine() blocks event loop, we have to execute is specificly, see http://vertx.io/docs/vertx-core/java/#blocking_code
                client.executeBlocking(() -> {
                    //ask for username
                    System.out.println("Please insert your username: ");

                    try {
                        //read username
                        String username = reader.readLine();

                        //authorize
                        System.out.println("send username: " + username);
                        client.auth(username);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                return;
            } else if (message.getAction().equals("auth_res")) {
                //check, if authorization was successful.
                if (!message.getResult().isEmpty() && message.getResult().equals("success")) {
                    System.out.println("[System] authorization successful!");
                } else {
                    System.out.println("[System] authorization failed!");

                    //because reader.readLine() blocks event loop, we have to execute is specificly, see http://vertx.io/docs/vertx-core/java/#blocking_code
                    client.executeBlocking(() -> {
                        //ask for username
                        System.out.println("Please insert an new username: ");

                        try {
                            //read username
                            String username = reader.readLine();

                            //authorize
                            System.out.println("send username: " + username);
                            client.auth(username);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                System.out.println("[" + message.getUsername() + "] " + message.getText());
            }
        });

        //connect to server
        try {
            client.connect("localhost", 2200);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        /**
        * console
        */

        String line = "";

        System.out.println("You can submit messages with ENTER. Type /quit to close client.");

        while (true) {
            System.out.print("> ");

            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();

                continue;
            }

            if (line.equals("/quit")) {
                System.out.println("close chat client now.");

                try {
                    //close client
                    client.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.exit(0);
            } else {
                //its an chat message
                client.sendMessageToServer(line);
            }
        }
    }

}
