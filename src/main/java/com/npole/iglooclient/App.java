package com.npole.iglooclient;

import java.net.*;
import java.io.*;
import java.util.*;
import java.io.IOException;

public class App {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private String server, username;
    private int port;

    // constructor
    App(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    // begin dialog
    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        // create both data streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/Output streams: " + eIO);
            return false;
        }

        // create thread to listen from server
        new ListenFromServer().start();

        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            display("Exception during login: " + eIO);
            disconnect();
            return false;
        }
        return true;
    }

    private void display(String msg) {
        System.out.println(msg);
    }

    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    private void disconnect() {
        try {
            if (sInput != null) sInput.close();
        } catch (Exception e) {}
        try {
            if (sOutput != null) sOutput.close();
        } catch (Exception e) {}
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {}
    }

    /*
    * To start the Client in console mode use one of the following command
    * > java Client
    * > java Client username
    * > java Client username portNumber
    * > java Client username portNumber serverAddress
    * at the console prompt
    * If the portNumber is not specified 1500 is used
    * If the serverAddress is not specified "localHost" is used
    * If the username is not specified "Anonymous" is used
    * > java Client 
    * is equivalent to
    * > java Client anon 8000 localhost 
    * are eqquivalent
    * 
    * In console mode, if an error occurs the program simply stops
    */

    public static void main(String[] args) {
        int portNumber = 8000;
        String serverAddress = "localhost";
        String username = "anon";

        switch (args.length) {
            // > javac Client username portnumber serverAddr
            case 3:
                serverAddress = args[2];
            case 2:
                try {
                    portNumber = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is > java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            // > javac Client username
            case 1:
                username = args[0];
            case 0:
                break;
            // invalid number of args
            default:
                System.out.println("Usage is > java Client [username] [portNumber] [serverAddress]");
            return;
        }

        // create Client obj
        App app = new App(serverAddress, portNumber, username);
        // test connection to server
        if (!app.start()) return;

        // wait for msgs from user
        Scanner scan = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String msg = scan.nextLine();
            if (msg.equalsIgnoreCase("LOGOUT")) {
                app.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            } else if (msg.equalsIgnoreCase("WHOISIN")) {
                app.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
            } else {
                app.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
            }
        }
        app.disconnect();
    }

    class ListenFromServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.println(msg);
                    System.out.print("> ");
                } catch (IOException e) {
                    display("Server has closed the connection: " + e);
                    break;
                } catch (ClassNotFoundException e2) {}
            }
        }
    }
}