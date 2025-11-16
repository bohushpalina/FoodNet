package foodnet.client;

import foodnet.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FoodClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5555);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Scanner sc = new Scanner(System.in);

        // 1. Получить меню
        out.writeObject(new Message(MessageType.GET_MENU, ""));
        Message menuMsg = (Message) in.readObject();
        System.out.println("Меню:");
        System.out.println(menuMsg.getData());

        // 2. Ввести заказ
        System.out.println("Введите заказ (через запятую):");
        String order = sc.nextLine();
        out.writeObject(new Message(MessageType.ORDER, order));

        Message sumMsg = (Message) in.readObject();
        System.out.println("Стоимость заказа: " + sumMsg.getData());

        // 3. Адрес
        System.out.println("Введите адрес доставки:");
        String addr = sc.nextLine();
        out.writeObject(new Message(MessageType.ADDRESS, addr));

        System.out.println("Заказ оформлен.");
    }
}
