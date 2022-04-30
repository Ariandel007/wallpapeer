package pe.edu.upc.wallpapeer.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;
import pe.edu.upc.wallpapeer.views.custom.CanvasView;

public class CanvasActivity extends AppCompatActivity {

    private ConnectionPeerToPeerViewModel model;
    FloatingActionButton btnQr;
    ImageView imgQr;
    private String userDeviceName;
    private ConstraintLayout constraintLayout;
    ConstraintLayout constraintLayoutLoadingSearchPeers;
    private boolean imgQrShowed = false;
    private String addressee;
    private boolean isOffline;
    private String startDate;
    private CanvasView canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_canvas);

        model = ViewModelProviders.of(this).get(ConnectionPeerToPeerViewModel.class);

        btnQr = findViewById(R.id.btn_qr);
        imgQr = findViewById(R.id.img_qr);
        canvasView = findViewById(R.id.canvas);
        constraintLayout = findViewById(R.id.popup_qr);
        constraintLayout.setVisibility(View.GONE);
        constraintLayoutLoadingSearchPeers  = findViewById(R.id.search_peer_connections_canvas);
        //
        constraintLayoutLoadingSearchPeers.setVisibility(View.VISIBLE);
        btnQr.setVisibility(View.GONE);
        canvasView.setVisibility(View.GONE);
        //
        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE, false);
        addressee = getIntent().getStringExtra(Constants.ADDRESAT_NAME);
        startDate = getIntent().getStringExtra(Constants.DATE);

        //EJEMPLO PARA OBTENER INTENTS
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String projetcId = extras.getString("project_id");
//            String deviceId = extras.getString("device_id");
//            //The key argument here must match that used in the other activity
//        }

        userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (userDeviceName == null)
            userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        if (isOffline) {
            model.setAddressee(addressee);
        } else {

            model.startSearch();
            model.socketIsReady().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
//                        Objects.requireNonNull(getSupportActionBar()).show();
//                        addressee = model.getAddressee();
//                        getSupportActionBar().setTitle(addressee);
                    }
                }
            });

            model.getInicioLaBusquedaDePares().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
//                        Toast.makeText(CanvasActivity.this, "Se incio la busqueda de pares.", Toast.LENGTH_SHORT).show();
                        constraintLayoutLoadingSearchPeers.setVisibility(View.GONE);
                        btnQr.setVisibility(View.VISIBLE);
                        canvasView.setVisibility(View.VISIBLE);
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
                    CharSequence[] items = new CharSequence[peers.size()];
                    int i = 0;
                    for (WifiP2pDevice wifiP2pDevice : peers) {
                        items[i] = wifiP2pDevice.deviceName;
                        i++;
                    }
                }
            });

            model.getChatClosed().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    // Acción de cierre de chat
                    if (aBoolean == null || aBoolean) {
                        Toast.makeText(CanvasActivity.this, "Uno de los dispositivos abandonó el group owner.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(imgQrShowed) {
                        constraintLayout.setVisibility(View.GONE);
                        imgQrShowed = !imgQrShowed;
                    } else {
                        constraintLayout.setVisibility(View.VISIBLE);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.encodeBitmap(userDeviceName, BarcodeFormat.QR_CODE, 300, 300);
                        imgQr.setImageBitmap(bitmap);
                        imgQrShowed = !imgQrShowed;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        ActivityCanvasBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_canvas);
//
//        MainCanvasViewModel viewModel = new MainCanvasViewModel();
//        activityMainBinding.setVariable(BR.mainCanvasViewModel, viewModel);
//        activityMainBinding.executePendingBindings();


    }
}