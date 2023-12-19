package ait.chat.server.task;

import ait.mediation.BlkQueueImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerSender implements Runnable {

    private final BlkQueueImpl<String> sharedQueue;
    private final List<PrintWriter> clientOutputStreams;

    public ChatServerSender(BlkQueueImpl<String> sharedQueue) {
        this.sharedQueue = sharedQueue;
        this.clientOutputStreams = new ArrayList<>();
    }

    public void addClient(Socket clientSocket) throws IOException {
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        synchronized (clientOutputStreams) {
            clientOutputStreams.add(writer);
        }
    }

    public void removeClient(Socket clientSocket) throws IOException {
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        synchronized (clientOutputStreams) {
            clientOutputStreams.remove(writer);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setDaemon(true);
        while (true) {
            String message = sharedQueue.pop();
            synchronized (clientOutputStreams) {
                for (PrintWriter writer : clientOutputStreams) {
                    writer.println(message);
                }
            }
        }
    }
}