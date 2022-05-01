package pe.edu.upc.wallpapeer.connections;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

import pe.edu.upc.wallpapeer.LocalDevice;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class Server extends IMessenger {
    private Socket socket;
    private ServerSocket serverSocket;
    private String peerName;
    private MutableLiveData<Boolean> isConnected;
    private ConnectionPeerToPeerViewModel model;

    public Server(ConnectionPeerToPeerViewModel model, MutableLiveData<Boolean> isConnected) {
        this.model = model;
        this.isConnected = isConnected;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Tras conectar, enviamos el nombre de nuestro dispositivo como primer mensaje
        send(LocalDevice.getInstance().getDevice().deviceName, false);

        // Ya he leído el nombre del compañero
        boolean isAddresseeSet = false;

        while (socket != null) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String messageText = (String) inputStream.readObject();
                if (messageText != null) {
                    if (isAddresseeSet) {
                        // Si llega un nuevo mensaje lo guardamos directamente en la base de datos
                        // Es el objeto de esta base de datos el que es activado por la actividad correspondiente al objeto leído
                        // Ya no tenemos que enviar a Active
                        Date c = Calendar.getInstance().getTime();
//                        MessageEntity message = new MessageEntity(messageText, c, peerName, false);
//                        MessageRepository.getInstance().insert(message);
                    } else {
                        // El primer mensaje que leemos es el nombre del par y luego chateamos.
                        isAddresseeSet = true;
                        peerName = messageText;
                        model.setAddressee(messageText);
                        isConnected.postValue(true);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // Si el socket está cerrado desde el otro lado, también cerramos la ventana de chat.
                model.closeChat();
            }
        }

    }

    @Override
    public void send(final String text, final boolean isMessage) {

        new Thread() {
            @Override
            public void run() {
                if (socket == null) return;
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(text);
                    outputStream.flush();
                    if (isMessage) {
                        // Si no es el primer mensaje, es decir no enviamos el nombre
                        // Luego tenemos que almacenarlo en la base de datos también
                        Date c = Calendar.getInstance().getTime();
//                        MessageEntity message = new MessageEntity(text, c, peerName, true);
//                        MessageRepository.getInstance().insert(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void DestroySocket() {
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

}
