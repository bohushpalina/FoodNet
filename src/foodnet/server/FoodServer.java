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
	        new Dish("Pizza", 12.5), // Changed "Пицца" to "Pizza"
	        new Dish("Burger", 9.0), // Changed "Бургер" to "Burger"
	        new Dish("Salad", 5.0),  // Changed "Салат" to "Salad"
	        new Dish("Sushi", 14.0)  // Changed "Суши" to "Sushi"
	);

    public static void main(String[] args) throws Exception {
    	ServerSocket server = new ServerSocket(5555, 50, InetAddress.getByName("0.0.0.0"));
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
                            // Using more robust split for comma-separated list
                            String[] dishes = msg.getData().split("\\s*,\\s*");
                            double sum = 0;

                            for (String dishName : dishes) {
                                for (Dish d : menu) {
                                    if (d.getName().equalsIgnoreCase(dishName.trim())) {
                                        sum += d.getPrice();
                                        // Added break to prevent re-counting and optimize search
                                        break; 
                                    }
                                }
                            }

                            out.writeObject(new Message(MessageType.ORDER_SUM, String.valueOf(sum)));
                        }

                        case ADDRESS -> {
                            System.out.println("Delivery Address: " + msg.getData());
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Client disconnected.");
            }
        }
    }
}