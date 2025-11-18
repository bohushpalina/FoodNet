package foodnet.client;

import foodnet.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FoodClient {

    public static void main(String[] args) throws Exception {
        // Hardcoded IP and port for connection
        Socket socket = new Socket("10.195.59.160", 5555); 

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Scanner sc = new Scanner(System.in);

        // 1. Get menu
        out.writeObject(new Message(MessageType.GET_MENU, ""));
        Message menuMsg = (Message) in.readObject();
        System.out.println("Menu:");
        System.out.println(menuMsg.getData());

        // 2. Enter order
        System.out.println("Enter your order (comma-separated):");
        String order = sc.nextLine();
        out.writeObject(new Message(MessageType.ORDER, order));

        Message sumMsg = (Message) in.readObject();
        System.out.println("Order total: " + sumMsg.getData());

        // 3. Address
        System.out.println("Enter delivery address:");
        String addr = sc.nextLine();
        out.writeObject(new Message(MessageType.ADDRESS, addr));

        System.out.println("Order placed.");
        
        // Closing the resources (optional, as main thread ends, but good practice)
        sc.close();
        socket.close();
    }
}