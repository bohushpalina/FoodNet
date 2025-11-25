package foodnet.client;

import foodnet.data.Dish;
import foodnet.data.Order;
import foodnet.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class FoodClient {

	public static void main(String[] args) throws Exception {
        Socket socket = new Socket("10.195.59.160", 5555);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Scanner sc = new Scanner(System.in);

        out.writeObject(new Message(MessageType.GET_MENU, null));
        Message menuMsg = (Message) in.readObject();
        System.out.println("Menu:");
        System.out.println(menuMsg.getData());

        System.out.println("Enter your order:");
        String orderString = sc.nextLine();

        ArrayList<Dish> dishesToSend = new ArrayList<>();
        String[] dishNames = orderString.split(",");
        for (String name : dishNames) {
            dishesToSend.add(new Dish(name.trim(), 0.0));
        }

        out.writeObject(new Message(MessageType.ORDER, orderString));

        Message sumMsg = (Message) in.readObject();
        System.out.println("Order total: " + sumMsg.getData());

        System.out.println("Enter delivery address:");
        String addr = sc.nextLine();
        Order myOrder = new Order(dishesToSend, addr);
        out.writeObject(new Message(MessageType.SUBMIT_ORDER, myOrder));
        System.out.println("Order placed.");

        sc.close();
        socket.close();
    }
}