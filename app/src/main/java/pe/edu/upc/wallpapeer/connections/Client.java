package pe.edu.upc.wallpapeer.connections;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

import pe.edu.upc.wallpapeer.LocalDevice;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class Client extends IMessenger {
    private Socket socket;
    private String peerName;
    private String host;
    private ConnectionPeerToPeerViewModel model;
    private MutableLiveData<Boolean> isConnected;

    public Client(String host, ConnectionPeerToPeerViewModel model, MutableLiveData<Boolean> isConnected) {
        this.host = host;
        this.isConnected = isConnected;
        this.model = model;
    }

    @Override
    public void run() {
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, 8888), 5000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Tras conectar, enviamos el nombre de nuestro dispositivo como primer mensaje
        send(LocalDevice.getInstance().getDevice().deviceName, false);

        // Ya he leído el nombre del par
        boolean isAddresseeSet = false;

        while (socket != null) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                String messageText = (String) inputStream.readObject();
                if (messageText != null) {
                    if (isAddresseeSet) {
                        //EJEMPLO, Tomar con pinzas uwu
//                        String obtenerEvent = messageText.substring(7,14);
//                        switch (obtenerEvent) {
//                            case CodeEvent.PINCH_EVENT:
//                                //haz cosas
//                                break;
//                        }

                        // Si llega un nuevo mensaje, lo guardaremos directamente en la base de datos.
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
    }


}
