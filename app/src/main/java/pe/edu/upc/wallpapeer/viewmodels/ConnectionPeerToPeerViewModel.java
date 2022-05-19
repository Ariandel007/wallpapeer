package pe.edu.upc.wallpapeer.viewmodels;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.net.InetAddress;
import java.util.List;

import pe.edu.upc.wallpapeer.WiFiDirectBroadcastReceiver;
import pe.edu.upc.wallpapeer.connections.Client;
import pe.edu.upc.wallpapeer.connections.IMessenger;
import pe.edu.upc.wallpapeer.connections.Server3;
import pe.edu.upc.wallpapeer.connections.SimpleMessenger;
import pe.edu.upc.wallpapeer.connections.WIFIDirectConnections;
import pe.edu.upc.wallpapeer.model.figures.Circle;

public class ConnectionPeerToPeerViewModel extends AndroidViewModel implements Observable{
    //NECESARIO PARA WIFIDIRECT
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Application app;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private WIFIDirectConnections connections;
    private IMessenger messenger;
    private SimpleMessenger simpleMessenger;
    private String addressee;

//    private MessageRepository repository;

    private MutableLiveData<Boolean> inicioLaBusquedaDePares;

    private MutableLiveData<Boolean> socketIsReady;

//    private LiveData<List<MessageEntity>> messageList;

    private MutableLiveData<List<WifiP2pDevice>> peerList;

    private MutableLiveData<Boolean> chatClosed;

//  private MutableLiveData<Boolean> onSucessConnection = new MutableLiveData<Boolean>(false);

    private boolean isConnected = false;

    public boolean isMainCanvas = false;
//
    public final ObservableInt backgroundFill = new ObservableInt();
    @Bindable
    private MutableLiveData<List<Circle>> circleList;


    public ConnectionPeerToPeerViewModel(@NonNull final Application application) {
        super(application);
        app = application;
        wifiP2pManager = (WifiP2pManager) app.getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(app.getApplicationContext(), app.getMainLooper(), null);
        WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if (!info.groupFormed) return;
                if (isConnected) return;
                isConnected = true;

                Log.d("new connection", info.toString());
                final InetAddress address = info.groupOwnerAddress;
                if (info.isGroupOwner) {
//                    Server server = new Server(ConnectionPeerToPeerViewModel.this, socketIsReady);
//                    server.start();
//                    messenger = server;
//                    if(messenger != null) {
//                        socketIsReady.setValue(true);
//                    }
//                    Server2 server = new Server2(ConnectionPeerToPeerViewModel.this, socketIsReady);
//                    server.start();
//                    messenger = server;
//                    if(messenger != null) {
//                        socketIsReady.setValue(true);
//                    }
                    if(simpleMessenger == null) {
                        Server3 server = new Server3();
                        server.start();
                        simpleMessenger = server;
                        if(simpleMessenger != null) {
                            socketIsReady.setValue(true);
                        }
                    }

                } else {
                    Client client = new Client(address.getHostAddress(), ConnectionPeerToPeerViewModel.this, socketIsReady);
                    client.start();
                    messenger = client;
                    if(messenger != null) {
                        socketIsReady.setValue(true);
                    }
                }
                Toast.makeText(application, "Se ha establecido la conexión con el dispositivo", Toast.LENGTH_SHORT).show();
            }
        };


        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.e("new peer", peers.toString());

                if (connections != null) {
                    if (!connections.updateDeviceList(peers)) return;
                    if (connections.getDeviceCount() > 0 && !isConnected) {
                        peerList.postValue(connections.getDeviceList());
                    }
                }
            }
        };
        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, peerListListener, connectionInfoListener);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        connections = new WIFIDirectConnections();
//        repository = MessageRepository.getInstance();
        socketIsReady = new MutableLiveData<>(false);
        inicioLaBusquedaDePares = new MutableLiveData<>(false);
//        messageList = new MutableLiveData<>();
        peerList = new MutableLiveData<>();
        chatClosed = new MutableLiveData<>();
        circleList = new MutableLiveData<>();

        registerReceiver();
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
//        messageList = repository.getAllMessages(this.addressee);
    }

    public String getAddressee() {
        return addressee;
    }

    public MutableLiveData<Boolean> socketIsReady() {
        return socketIsReady;
    }

//    public LiveData<List<MessageEntity>> getMessageList() {
//        return messageList;
//    }

    public MutableLiveData<List<WifiP2pDevice>> getPeerList() {
        return peerList;
    }

    public MutableLiveData<Boolean> getChatClosed() {
        return chatClosed;
    }


    public void registerReceiver() {
        app.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
    }




    public void unregisterBroadcast() {
        app.getApplicationContext().unregisterReceiver(broadcastReceiver);
    }

    public void startSearch() {
        if (ActivityCompat.checkSelfPermission(app.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("", "success peer discovery");
                inicioLaBusquedaDePares.setValue(true);
            }

            @Override
            public void onFailure(int reason) {
                Log.d("", "fail peer discovery");
            }
        });
    }

    public void connectToPeer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        if (ActivityCompat.checkSelfPermission(app.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(isMainCanvas) {
            config.groupOwnerIntent = 15;
        } else {
            config.groupOwnerIntent = 0;
        }
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                onSucessConnection.setValue(true);
                Log.d("", "connection success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("", "connection fail");
            }
        });
    }

    public void sendMessage(String text) {
        if(messenger != null){
            messenger.send(text, true);
        }
        if(simpleMessenger != null) {
            simpleMessenger.send(text, true);
        }

    }

    //El propietario del grupo elimina el grupo. Cierra el socket
    public void closeChat() {
        if (wifiP2pManager != null && channel != null) {
            if (ActivityCompat.checkSelfPermission(app.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && wifiP2pManager != null && channel != null
                            && group.isGroupOwner()) {
                        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d("groupRemoveSuccess", "removeGroup onSuccess -");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("groupRemoveFail", "removeGroup onFailure -" + reason);
                            }
                        });
                    }
                }
            });
        }


        if (messenger != null) {
            messenger.DestroySocket();
        }

        if (simpleMessenger != null) {
            simpleMessenger.DestroySocket();
        }

        if(isConnected)
            chatClosed.postValue(true);
    }

//    public MutableLiveData<Boolean> getOnSucessConnection() {
//        return onSucessConnection;
//    }

//    public void setOnSucessConnection(MutableLiveData<Boolean> onSucessConnection) {
//        this.onSucessConnection = onSucessConnection;
//    }

//    public void deleteChat() {
//        repository.deleteAllFrom(addressee);
//    }

    public ObservableInt getBackgroundFill() {
        return backgroundFill;
    }

    public MutableLiveData<List<Circle>> getCircleList() {
        return circleList;
    }

    public void setCircleList(MutableLiveData<List<Circle>> circleList) {
        this.circleList = circleList;
    }

    @Override
    public void addOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback) {

    }

    @Override
    public void removeOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback) {

    }

    public MutableLiveData<Boolean> getInicioLaBusquedaDePares() {
        return inicioLaBusquedaDePares;
    }
}
