package ait.chat;

import ait.chat.server.task.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChatServerApp {

    public static void main(String[] args) {
        int port = 9000;
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                System.out.println("server wait...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection established");
                System.out.println("Client host: " + socket.getInetAddress() + ":" + socket.getPort());

                ClientHandler clientHandler = new ClientHandler(socket);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ie) {
                executorService.shutdownNow();
            }
        }
    }
}