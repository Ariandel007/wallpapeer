package pe.edu.upc.wallpapeer.connections;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pe.edu.upc.wallpapeer.LocalDevice;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class Server2 extends IMessenger {
    private ServerSocket serverSocket;
    private String peerName;
    private MutableLiveData<Boolean> isConnected;
    private ConnectionPeerToPeerViewModel model;

    ArrayList<ServerThread> threadList;

    public Server2(ConnectionPeerToPeerViewModel model, MutableLiveData<Boolean> isConnected) {
        this.model = model;
        this.isConnected = isConnected;
    }

    @Override
    public void run() {
        threadList = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(8888);

            while (true) {
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket, threadList, model);
                // iniciando el hilo
                threadList.add(serverThread);
                serverThread.start();
                //obtener todos los hilos
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void send(final String text, final boolean isMessage) {
        for (ServerThread serverThread : threadList) {
            serverThread.send(text, isMessage);
//            Socket socket = serverThread.getSocket();
//            new Thread() {
//                @Override
//                public void run() {
//                    if (socket == null) return;
//                    try {
//                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//                        outputStream.writeObject(text);
//                        outputStream.flush();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
        }

    }

    @Override
    public void DestroySocket() {
        for (ServerThread serverThread : threadList) {
            Socket socket = serverThread.getSocket();
            if (socket != null) {
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
