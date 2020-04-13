package src.MultiUserChatApp;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Vector;

public class Server {

    // Vector to store active clients
    static Vector<Thread> activeClients = new Vector<>();

    // Hashmap to store channels
    static HashMap<String, Vector<Thread>> channels = new HashMap<>();

    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port,  1, new InetSocketAddress(0).getAddress());
        serverSocket.setSoTimeout(600000);

        System.out.println("Server started");
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + " ...");

        channels.put("main", new Vector<>());

        Socket server = null;

        try {
            // Running infinite loop for getting client request
            while (true) {
                server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                DataInputStream in = new DataInputStream(server.getInputStream());

                Thread t = new ClientHandler(server, in, out);

                // add this client to active clients list
                activeClients.add(t);

                t.start();
            }
        } catch (Exception e) {
            server.close();
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        int port = Integer.parseInt(args[0]);
        try {
            Server server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
