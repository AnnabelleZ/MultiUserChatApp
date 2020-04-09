import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port,  1, new InetSocketAddress(0).getAddress());
        serverSocket.setSoTimeout(10000);

        System.out.println("Server started");
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + " ...");

        // Running infinite loop for getting client request
        while (true) {
            Socket server = serverSocket.accept();
            System.out.println("Just connected to " + server.getRemoteSocketAddress());

            // Prompt user for a username
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            //out.writeUTF("Enter your name");

            DataInputStream in = new DataInputStream(server.getInputStream());
            //String name = in.readUTF();

            Thread t = new ClientHandler(server, in, out);
            t.start();
            //out.writeUTF("Hello " + name);

            /*String message = "";
            while (!message.equals("exit")) {
                message = in.readUTF();
                out.writeUTF("[" + name + "]: " + message);
            }*/

            server.close();
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
