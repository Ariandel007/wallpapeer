package pe.edu.upc.wallpapeer.utils;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import pe.edu.upc.wallpapeer.connections.IMessenger;
import pe.edu.upc.wallpapeer.connections.SimpleMessenger;

public class WifiDirectSIngletonForSocketDisconection {
    private static WifiDirectSIngletonForSocketDisconection INSTANCE;


    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private IMessenger messenger;
    private SimpleMessenger simpleMessenger;
    private Application app;


    private WifiDirectSIngletonForSocketDisconection() {
    }

    public static WifiDirectSIngletonForSocketDisconection getInstance() {
        if(INSTANCE == null) {
            synchronized (WifiDirectSIngletonForSocketDisconection.class)
            {
                if(INSTANCE==null)
                {
                    INSTANCE = new WifiDirectSIngletonForSocketDisconection();
                }

            }
        }

        return INSTANCE;
    }

    public void closeConnections() {
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
    }

    public WifiP2pManager getWifiP2pManager() {
        return wifiP2pManager;
    }

    public void setWifiP2pManager(WifiP2pManager wifiP2pManager) {
        this.wifiP2pManager = wifiP2pManager;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }

    public void setChannel(WifiP2pManager.Channel channel) {
        this.channel = channel;
    }

    public IMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(IMessenger messenger) {
        this.messenger = messenger;
    }

    public SimpleMessenger getSimpleMessenger() {
        return simpleMessenger;
    }

    public void setSimpleMessenger(SimpleMessenger simpleMessenger) {
        this.simpleMessenger = simpleMessenger;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }
}
