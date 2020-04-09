import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String args[]) throws IOException{
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

        System.out.println("Server says " + in.readUTF() + ".\nNow you can send messages to the chatroom");

        if (inFromConsole.hasNextLine()) {
            String message = inFromConsole.nextLine();
            while (!message.equals("exit")) {
                out.writeUTF(message);
                System.out.println(in.readUTF());
                message = inFromConsole.nextLine();
            }
        }
        client.close();
    }
}
