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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.net.InetAddress;
import java.util.List;

import pe.edu.upc.wallpapeer.WiFiDirectBroadcastReceiver;
import pe.edu.upc.wallpapeer.connections.Client;
import pe.edu.upc.wallpapeer.connections.IMessenger;
import pe.edu.upc.wallpapeer.connections.Server;
import pe.edu.upc.wallpapeer.connections.WIFIDirectConnections;

public class JoinLienzoViewModel extends AndroidViewModel {
    //NECESARIO PARA WIFIDIRECT
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private Application app;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private WIFIDirectConnections connections;
    private IMessenger messenger;
    private String addressee;
//    private MessageRepository repository;


    private MutableLiveData<Boolean> chatIsReady;

//    private LiveData<List<MessageEntity>> messageList;

    private MutableLiveData<List<WifiP2pDevice>> peerList;

    private MutableLiveData<Boolean> chatClosed;

    private boolean isConnected = false;
//

    public JoinLienzoViewModel(@NonNull final Application application) {
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
                    Server server = new Server(JoinLienzoViewModel.this, chatIsReady);
                    server.start();
                    messenger = server;
                } else {
                    Client client = new Client(address.getHostAddress(), JoinLienzoViewModel.this, chatIsReady);
                    client.start();
                    messenger = client;
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
        chatIsReady = new MutableLiveData<>();
//        messageList = new MutableLiveData<>();
        peerList = new MutableLiveData<>();
        chatClosed = new MutableLiveData<>();
        registerReceiver();
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
//        messageList = repository.getAllMessages(this.addressee);
    }

    public String getAddressee() {
        return addressee;
    }

    public MutableLiveData<Boolean> chatIsReady() {
        return chatIsReady;
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
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("", "connection success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("", "connection fail");
            }
        });
    }

    public void sendMessage(String text) {
        messenger.send(text, true);
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
        if(isConnected)
            chatClosed.postValue(true);
    }

//    public void deleteChat() {
//        repository.deleteAllFrom(addressee);
//    }

}
