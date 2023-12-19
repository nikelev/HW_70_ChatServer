


        package ait.chat;

        import ait.mediation.BlkQueueImpl;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.PrintWriter;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;
        import java.util.concurrent.TimeUnit;

public class ChatServerAppl {

    public static void main(String[] args) {
        int port = 9000;
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        BlkQueueImpl<String> messageQueue = new BlkQueueImpl<>(100);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                System.out.println("server wait...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection established");
                System.out.println("Client host: " + socket.getInetAddress() + ":" + socket.getPort());

                executorService.execute(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String message;
                        while ((message = reader.readLine()) != null) {
                            messageQueue.push(message);
                        }
                    } catch (IOException e) {
                        System.out.println("Error input: " + e.getMessage());
                    }
                });

                executorService.execute(() -> {
                    try {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        while (true) {
                            String message = messageQueue.pop();
                            writer.println(message);
                        }
                    } catch (IOException e) {
                        System.out.println("Error output: " + e.getMessage());
                    }
                });
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