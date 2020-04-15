package src.MultiUserChatApp;

import java.net.*;
import java.io.*;
import java.util.Vector;

public class ClientHandler extends Thread {
    final Socket socket;
    final DataInputStream in;
    final DataOutputStream out;
    boolean isActive;
    String channel = "";

    public ClientHandler(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.isActive = true;
    }

    @Override
    public void run() {
        String name = "";

        try {
            // Prompt user for a username
            out.writeUTF("Enter your name");

            name = in.readUTF();

            while (!socket.isClosed()) {
                out.writeUTF("Hello " + name + ". Welcome to Dumpling Chatroom.\n"
                        + "Type \"enter $channel_name\" to enter a channel.\n"
                        + "Type \"create $channel_name\" to create a new channel.\n"
                        + "Type \"quit\" to exit a channel\n"
                        + "Type \"exit\" to exit the program");
                out.writeUTF("List of channels: " + Server.channels.keySet().toString());

                String command = in.readUTF();
                System.out.println(command);

                // Continuously prompt the user to re-enter command if command invalid
                while (true) {
                    if (!command.startsWith("enter") & !command.startsWith("create") &
                            !command.equals("exit") & !command.equals("quit")) {
                        out.writeUTF("command doesn't exist, please try again");
                        command = in.readUTF();
                        continue;
                    } else if (command.equals("quit")) {
                        out.writeUTF("you are not in any channel, please enter a channel first");
                        command = in.readUTF();
                        continue;
                    } else if (command.startsWith("enter")) {
                        String[] tmp = command.split(" ");
                        if (tmp.length != 2 || !Server.channels.containsKey(tmp[1])) {
                            out.writeUTF("channel not found, please try again");
                            command = in.readUTF();
                            continue;
                        }
                    } else if (command.startsWith("create")) {
                        String[] tmp = command.split(" ");
                        if (tmp.length != 2) {
                            out.writeUTF("please specify a valid channel name");
                            command = in.readUTF();
                            continue;
                        }
                    }
                    break;
                }

                // perform computation based on the commands
                if (command.startsWith("create")) {
                    String channelName = command.split(" ")[1];
                    Server.channels.put(channelName, new Vector<>());
                    Server.channels.get(channelName).add(this);
                    out.writeUTF(name + " has joined the channel " + channelName);
                    channel = channelName;
                }
                if (command.startsWith("enter")) {
                    String channelName = command.split(" ")[1];
                    Vector<Thread> channelUsers = Server.channels.get(channelName);
                    channelUsers.add(this);
                    for (Thread t : channelUsers) {
                        ClientHandler client = (ClientHandler) t;
                        client.out.writeUTF(name + " has joined the channel " + channelName);
                    }
                    channel = channelName;
                }
                if (!command.equals("exit")) {
                    String message = "";

                    // continuously receive user messages until client enters exit
                    while (true) {
                        message = in.readUTF();
                        System.out.println(message);

                        if (message.equals("quit")) {
                            removeEmptyChannel();
                            break;
                        }
                        if (message.equals("exit")) {
                            this.isActive = false;
                            this.socket.close();
                            break;
                        }

                        for (Thread t : Server.channels.get(channel)) {
                            ClientHandler client = (ClientHandler) t;
                            if (this.isActive)
                                client.out.writeUTF("[" + name + "]: " + message);
                        }
                    }
                } else {
                    this.isActive = false;
                    this.socket.close();
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client " + name +  " has disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // closing resources
            this.in.close();
            this.out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        Server.activeClients.remove(this);
        removeEmptyChannel();
    }

    private void removeEmptyChannel() {
        if (!channel.isEmpty() & Server.channels.containsKey(channel)) {
            Server.channels.get(channel).remove(this);
            if (Server.channels.get(channel).isEmpty() & !channel.equals("main")) {
                Server.channels.remove(channel);
            }
        }
    }
}
