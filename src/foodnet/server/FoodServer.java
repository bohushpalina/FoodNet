package foodnet.server;

import foodnet.data.Dish;
import foodnet.protocol.*;
import java.util.Arrays;
import java.util.List;


import java.io.*;
import java.net.*;
import java.util.*;

public class FoodServer {

	private static final List<Dish> menu = Arrays.asList(
	        new Dish("Пицца", 12.5),
	        new Dish("Бургер", 9.0),
	        new Dish("Салат", 5.0),
	        new Dish("Суши", 14.0)
	);

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(5555);
        System.out.println("Food server started...");

        while (true) {
            Socket socket = server.accept();
            new Thread(new ClientHandler(socket)).start();
        }
    }

    static class ClientHandler implements Runnable {

        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
            ) {

                while (true) {
                    Message msg = (Message) in.readObject();

                    switch (msg.getType()) {
                        case GET_MENU -> {
                            StringBuilder sb = new StringBuilder();
                            menu.forEach(d -> sb.append(d).append("\n"));
                            out.writeObject(new Message(MessageType.MENU, sb.toString()));
                        }

                        case ORDER -> {
                            String[] dishes = msg.getData().split(",");
                            double sum = 0;

                            for (String dishName : dishes) {
                                for (Dish d : menu) {
                                    if (d.getName().equalsIgnoreCase(dishName.trim())) {
                                        sum += d.getPrice();
                                    }
                                }
                            }

                            out.writeObject(new Message(MessageType.ORDER_SUM, String.valueOf(sum)));
                        }

                        case ADDRESS -> {
                            System.out.println("Адрес доставки: " + msg.getData());
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Клиент отключился.");
            }
        }
    }
}