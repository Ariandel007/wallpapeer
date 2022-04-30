package pe.edu.upc.wallpapeer.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class JoinPaletaActivity extends AppCompatActivity {

    private String addressee;
    private String startDate;
    private boolean isOffline;
    private ConnectionPeerToPeerViewModel model;
    private ConstraintLayout loadingScreen;
    private ConstraintLayout loadingPallete;

    private String targetDeviceName = "";

    Button btnDecodes;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(JoinPaletaActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    targetDeviceName = result.getContents();
                    Toast.makeText(JoinPaletaActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    connectionToDevice();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_paleta);

        btnDecodes = findViewById(R.id.btnScanQr);

        initConnection();

        if (isOffline) {
            model.setAddressee(addressee);
        } else {
            loadingScreen.setVisibility(View.VISIBLE);
            btnDecodes.setVisibility(View.VISIBLE);

            model.startSearch();
            model.socketIsReady().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
//                        Objects.requireNonNull(getSupportActionBar()).show();
//                        addressee = model.getAddressee();
//                        getSupportActionBar().setTitle(addressee);
                        Toast.makeText(JoinPaletaActivity.this, "Conexion realizada.", Toast.LENGTH_SHORT).show();
                        loadingScreen.setVisibility(View.GONE);
                        loadingPallete.setVisibility(View.VISIBLE);
                    }
                }
            });
            model.getPeerList().observe(this, new Observer<List<WifiP2pDevice>>() {
                @Override
                public void onChanged(@Nullable final List<WifiP2pDevice> peers) {
                    // Si hemos cambiado la lista de pares, también debemos actualizar las ventanas eliminadas.
                    assert peers != null;
                    Log.d("", peers.toString());
                    if (peers.size() == 0)
                        return;
                    if(targetDeviceName.equals("")) {
                        return;
                    }

                    List<WifiP2pDevice> peersFindedWithTargetDeviceName = new ArrayList<>();
                    for (WifiP2pDevice peer :peers) {
                        if(peer.deviceName.contains(targetDeviceName)) {
                            peersFindedWithTargetDeviceName.add(peer);
                        }
                    }
                    if(peersFindedWithTargetDeviceName.size() == 0) {
                        return;
                    }
                    WifiP2pDevice peerFindedInQR = peersFindedWithTargetDeviceName.get(0);

                    model.connectToPeer(peerFindedInQR);

                }
            });

            model.getChatClosed().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    // Acción de cierre de chat
                    if (aBoolean == null || aBoolean) {
                        Toast.makeText(JoinPaletaActivity.this, "Uno de los dispositivos abandonó el grupo.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }

    }


    private void initConnection() {
        loadingScreen = findViewById(R.id.loadingScreen);
        loadingScreen.setVisibility(View.GONE);
        loadingPallete = findViewById(R.id.loadingPallete);
        loadingPallete.setVisibility(View.GONE);
        //CANCELAR BUSQUEDA Y CERRAR SOCKET
//        findViewById(R.id.stopSearch).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isOffline) {
//                    model.closeChat();
//                }
//                finish();
//            }
//        });
        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE, false);
        model = ViewModelProviders.of(this).get(ConnectionPeerToPeerViewModel.class);
        addressee = getIntent().getStringExtra(Constants.ADDRESAT_NAME);
        startDate = getIntent().getStringExtra(Constants.DATE);


        btnDecodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Escanea el QR");
                options.setCameraId(0);  // Use a specific camera of the device
                options.setBeepEnabled(false);
                options.setBarcodeImageEnabled(true);
                options.setOrientationLocked(false);

                barcodeLauncher.launch(options);
            }
        });

//        this.model.getOnSucessConnection().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                // Acción de comexion iniciada
//                if (aBoolean) {
//                    Toast.makeText(JoinPaletaActivity.this, "Conexion realizada.", Toast.LENGTH_SHORT).show();
//                    loadingScreen.setVisibility(View.GONE);
//                    loadingPallete.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    public void connectionToDevice() {
        List<WifiP2pDevice> wifiP2pDevices = this.model.getPeerList().getValue();
        if(wifiP2pDevices == null) {
            Log.e("NULO", "wifiP2pDevices es nulo");
            Toast.makeText(JoinPaletaActivity.this, "La lista de pares esta vacia", Toast.LENGTH_LONG).show();
            return;
        }
        if(targetDeviceName.equals("")) {
            Toast.makeText(JoinPaletaActivity.this, "No se leyo a ningun dispòsitivo", Toast.LENGTH_LONG).show();
            return;
        }

        List<WifiP2pDevice> peersFindedWithTargetDeviceName = new ArrayList<>();
        for (WifiP2pDevice peer :wifiP2pDevices) {
            if(peer.deviceName.contains(targetDeviceName)) {
                peersFindedWithTargetDeviceName.add(peer);
            }
        }
        if(peersFindedWithTargetDeviceName.size() == 0) {
            Toast.makeText(JoinPaletaActivity.this, "No se leyo a ningun dispòsitivo dentro de la lista de pares", Toast.LENGTH_LONG).show();
            return;
        }
        WifiP2pDevice peerFindedInQR = peersFindedWithTargetDeviceName.get(0);

        model.connectToPeer(peerFindedInQR);
        targetDeviceName = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.unregisterBroadcast();
    }
}