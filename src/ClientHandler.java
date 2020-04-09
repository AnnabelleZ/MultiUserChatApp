import java.awt.event.WindowListener;
import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {
    final Socket socket;
    final DataInputStream in;
    final DataOutputStream out;

    public ClientHandler(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            out.writeUTF("Enter your name");

            String name = in.readUTF();

            out.writeUTF("Hello " + name);

            String message = "";
            // continuously receive user messages until client enters exit
            while (!message.equals("exit")) {
                message = in.readUTF();
                out.writeUTF("[" + name + "]: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
