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

     // 1. Get menu (Остается без изменений)
        out.writeObject(new Message(MessageType.GET_MENU, ""));
        Message menuMsg = (Message) in.readObject();
        System.out.println("Menu:");
        System.out.println(menuMsg.getData());

        // 2. Ввод заказа и запрос суммы (Используем старую логику для получения суммы)
        System.out.println("Enter your order (comma-separated):");
        String orderString = sc.nextLine();
        
        // Отправляем строку заказа, чтобы сервер вычислил сумму
        out.writeObject(new Message(MessageType.ORDER, orderString));

        Message sumMsg = (Message) in.readObject();
        System.out.println("Order total: " + sumMsg.getData());

        // 3. Ввод адреса
        System.out.println("Enter delivery address:");
        String addr = sc.nextLine();
        
        String fullReport = orderString + "|" + addr;
        out.writeObject(new Message(MessageType.SUBMIT_ORDER, fullReport));
        
        System.out.println("Order placed.");
        
        sc.close();
        socket.close();
    }
}