package pe.edu.upc.wallpapeer.connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pe.edu.upc.wallpapeer.utils.RememberSocketsAddress;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class Server3 extends SimpleMessenger{
    public List<ClientTask> clientTasks = new ArrayList<>();
    public ServerSocket serverSocket;
    private ConnectionPeerToPeerViewModel model;

    public Server3(ConnectionPeerToPeerViewModel model) {
        this.model = model;
    }

    public void start() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        clientTasks = RememberSocketsAddress.getInstance().getClientTasks();

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(8888);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        ClientTask clientTask = new ClientTask(clientSocket, clientTasks, model);
                        String fullAddress = clientSocket.getRemoteSocketAddress().toString();
                        String ipSocket = fullAddress.substring(0,fullAddress.indexOf(":")-1);
                        if(!RememberSocketsAddress.getInstance().getSocketsAddress().contains(ipSocket)) {
                            clientTasks.add(clientTask);
                            RememberSocketsAddress.getInstance().addAddressToSet(ipSocket);
                        }
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
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
