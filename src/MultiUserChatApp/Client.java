package src.MultiUserChatApp;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String args[]) throws IOException, InterruptedException {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        System.out.println("Connecting to " + hostname + " on port " + port + " ...");

        Socket client = new Socket(hostname, port);

        System.out.println("Just connected to " + client.getRemoteSocketAddress());

        InputStream inFromServer = client.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);
        System.out.println("Server says " + in.readUTF());

        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        // Get username from console
        Scanner inFromConsole = new Scanner(System.in);
        String name = inFromConsole.nextLine();

        out.writeUTF(name);

        System.out.println("Server says " + in.readUTF());

        Thread sendMessage = new Thread(() -> {
            String message = "";
            while (!message.equals(("exit"))) {
                message = inFromConsole.nextLine();
                try {
                    out.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String message = in.readUTF();
                    System.out.println(message);
                } catch (EOFException e) {
                    System.out.println("Client " + name + " exited");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}
