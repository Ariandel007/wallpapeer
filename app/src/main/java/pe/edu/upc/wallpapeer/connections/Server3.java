package pe.edu.upc.wallpapeer.connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server3 extends SimpleMessenger{
    public List<ClientTask> clientTasks = new ArrayList<>();

    public void start() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(8888);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        ClientTask clientTask = new ClientTask(clientSocket, clientTasks);
                        clientTasks.add(clientTask);
                        clientProcessingPool.submit(clientTask);
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    @Override
    public void send(String text, boolean isMessage) {
        for (ClientTask clientTask : clientTasks ) {
            clientTask.send(text, isMessage);
        }

    }

    @Override
    public void DestroySocket() {
        for (ClientTask clientTask : clientTasks ) {
            clientTask.DestroySocket();
        }
    }
}
