package ait.chat.server.task;

import ait.mediation.BlkQueueImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


import java.net.Socket;

public class ChatServerReceiver implements Runnable {

    private BlkQueueImpl<String> queue;
    private BufferedReader reader;

    public ChatServerReceiver(BlkQueueImpl<String> queue, Socket socket) throws IOException {
        this.queue = queue;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                this.queue.push(message);
            }
        } catch (IOException e) {
            System.out.println("Еrror  input: " + e.getMessage());
        }
    }
}