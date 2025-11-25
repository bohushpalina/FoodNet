package foodnet.server;

import foodnet.data.Dish;
import foodnet.data.Order;
import foodnet.protocol.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class FoodServer {

    private static final List<Dish> menu = Arrays.asList(
        new Dish("Pizza", 12.5),
        new Dish("Burger", 9.0),
        new Dish("Salad", 5.0),
        new Dish("Sushi", 14.0)
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
                            String dataStr = (String) msg.getData(); 
                            String[] dishes = dataStr.split("\\s*,\\s*");
                            double sum = calculateSum(Arrays.asList(dishes));
                            out.writeObject(new Message(MessageType.ORDER_SUM, String.valueOf(sum)));
                        }

                        case SUBMIT_ORDER -> {
                            if (msg.getData() instanceof Order) {
                                Order receivedOrder = (Order) msg.getData();
                                
                                double sum = 0;
                                
                                for (Dish clientDish : receivedOrder.getDishes()) {
                                    for (Dish menuDish : menu) {
                                        if (menuDish.getName().equalsIgnoreCase(clientDish.getName())) {
                                            sum += menuDish.getPrice();
                                            break;
                                        }
                                    }
                                }

                                System.out.println("-----------Full Delivery Report------------");
                                System.out.println("Dishes count: " + receivedOrder.getDishes().size());
                                for(Dish d : receivedOrder.getDishes()) {
                                     System.out.println(" - " + d.getName());
                                }
                                System.out.println("Order Total: " + String.format("%.2f", sum) + " RUB");
                                System.out.println("Delivery Address: " + receivedOrder.getAddress());
                                System.out.println("-------------------------------------------");
                            } else {
                                System.out.println("Error: Received data is not an Order object");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Client disconnected.");
            }
        }

        private double calculateSum(List<String> dishNames) {
            double sum = 0;
            for (String name : dishNames) {
                for (Dish d : menu) {
                    if (d.getName().equalsIgnoreCase(name.trim())) {
                        sum += d.getPrice();
                        break;
                    }
                }
            }
            return sum;
        }
    }
}