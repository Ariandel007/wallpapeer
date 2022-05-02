package pe.edu.upc.wallpapeer.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.Resources;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

public class JoinLienzoActivity extends AppCompatActivity {

//    private View chatBox;
//    private MessageListAdapter adapter;
    private String addressee;
    private String startDate;
    private boolean isOffline;
    private ConnectionPeerToPeerViewModel model;
    private ConstraintLayout loadingScreen;
    private ConstraintLayout pinchScreen;
//    private ConstraintLayout messengerLayout;
    Button btnDecodes;

    boolean isPinchActivate = true;

    private String targetDeviceName = "";

    //para el pinch
    SwipeListener swipeListener;
    CoordinatorLayout mainScreenJoinLienzo;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(JoinLienzoActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    targetDeviceName = result.getContents();
                    Toast.makeText(JoinLienzoActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    connectionToDevice();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lienzo);

        btnDecodes = findViewById(R.id.btnScanQr);

        initConnection();

//        User user = (User)getIntent().getSerializableExtra("USER");
//
//        ActivityJoinLienzoBinding activityJoinLienzoBinding = DataBindingUtil.setContentView(this,R.layout.activity_join_lienzo);
//        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(user, this)).get(LoginViewModel.class);
//
//        activityJoinLienzoBinding.setLoginViewModel(loginViewModel);

//        final AlertDialog.Builder adb = new AlertDialog.Builder(JoinLienzoActivity.this);
//        final Boolean[] dialogActive = {Boolean.FALSE};
//        final AlertDialog[] dialogs = {null};
        if (isOffline) {
//            chatBox.setVisibility(View.GONE);
            model.setAddressee(addressee);
//            model.getMessageList().observe(this, new Observer<List<MessageEntity>>() {
//                @Override
//                public void onChanged(@Nullable List<MessageEntity> messageEntities) {
//                    adapter.updateData(messageEntities);
//                }
//            });
        } else {
            loadingScreen.setVisibility(View.VISIBLE);
            btnDecodes.setVisibility(View.VISIBLE);
//            messengerLayout.setVisibility(View.GONE);
//            Objects.requireNonNull(getSupportActionBar()).hide();

            model.startSearch();
            model.socketIsReady().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
                        Toast.makeText(JoinLienzoActivity.this, "Conexion realizada con dispositivo!!", Toast.LENGTH_SHORT).show();
                        loadingScreen.setVisibility(View.GONE);
                        pinchScreen.setVisibility(View.VISIBLE);

//                        Objects.requireNonNull(getSupportActionBar()).show();
//                        addressee = model.getAddressee();
//                        getSupportActionBar().setTitle(addressee);

//                        model.sendMessage("TEST MESSAGE");

                        //                        model.getMessageList().observe(ChatActivity.this, new Observer<List<MessageEntity>>() {
//                            @Override
//                            public void onChanged(@Nullable List<MessageEntity> messageEntities) {
//                                adapter.updateData(messageEntities);
//                            }
//                        });
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
//                    adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface d, int n) {
//                            model.connectToPeer(peers.get(n));
//                            d.cancel();
//                        }
//
//                    });
//                    adb.setNegativeButton("Cancelar", null);
//                    adb.setTitle("¿Cual de estos es el dispositivo que deseas conectar?");
                }
            });

            model.getChatClosed().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    // Acción de cierre de chat
                    if (aBoolean == null || aBoolean) {
                        Toast.makeText(JoinLienzoActivity.this, "Uno de los dispositivos abandonó el grupo.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }

        //Para el pinch
        mainScreenJoinLienzo = findViewById(R.id.mainScreenJoinLienzo);
        swipeListener = new SwipeListener(mainScreenJoinLienzo);

    }

    private void initConnection() {
//        messengerLayout = findViewById(R.id.messengerLayout);
//        chatBox = findViewById(R.id.layout_chatbox);
        loadingScreen = findViewById(R.id.loadingScreen);
        loadingScreen.setVisibility(View.GONE);
        pinchScreen = findViewById(R.id.PinchScreen);
        pinchScreen.setVisibility(View.GONE);
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
//        newMessage = findViewById(R.id.edittext_chatbox);
//        ImageButton sendMessage = findViewById(R.id.button_chatbox_send);
//        sendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(newMessage.getText().toString().length() == 0)
//                    return;
//                model.sendMessage(newMessage.getText().toString());
//                newMessage.setText("");
//            }
//        });

//        RecyclerView messages = findViewById(R.id.reyclerview_message_list);
//        messages.setLayoutManager(new LinearLayoutManager(this, 1, true));
//        adapter = new MessageListAdapter(new ArrayList<MessageEntity>());
//        messages.setAdapter(adapter);

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
//                    Toast.makeText(JoinLienzoActivity.this, "Conexion realizada con dispositivo!!", Toast.LENGTH_SHORT).show();
//                    loadingScreen.setVisibility(View.GONE);
//                    pinchScreen.setVisibility(View.VISIBLE);
////                    model.sendMessage("TEST MSG");
//                }
//            }
//        });
    }

    public void connectionToDevice() {
        List<WifiP2pDevice> wifiP2pDevices = this.model.getPeerList().getValue();
        if(wifiP2pDevices == null) {
            Log.e("NULO", "wifiP2pDevices es nulo");
            Toast.makeText(JoinLienzoActivity.this, "La lista de pares esta vacia", Toast.LENGTH_LONG).show();
            return;
        }

        if(targetDeviceName.equals("")) {
            Toast.makeText(JoinLienzoActivity.this, "No se leyo a ningun dispòsitivo", Toast.LENGTH_LONG).show();
            return;
        }

        List<WifiP2pDevice> peersFindedWithTargetDeviceName = new ArrayList<>();
        for (WifiP2pDevice peer :wifiP2pDevices) {
            if(peer.deviceName.contains(targetDeviceName)) {
                peersFindedWithTargetDeviceName.add(peer);
            }
        }
        if(peersFindedWithTargetDeviceName.size() == 0) {
            Toast.makeText(JoinLienzoActivity.this, "No se leyo a ningun dispòsitivo dentro de la lista de pares", Toast.LENGTH_LONG).show();
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
//PINCH COMIENZA ACA
//    private int getHeigthDevice() {
//        return Resources.getSystem().getDisplayMetrics().heightPixels;
//    }

    private int getWidthDevice() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private boolean touchAScreenLimit(float limit, float coord) {
        if(coord <0) {
            return true;
        }
        int absValue = (int) Math.abs(coord - limit);
        return absValue <= 100;
    }

    private void sendCoordsToPinch(float xDiff, float yDiff, MotionEvent e2,int threshoold, float velocityX, float velocityY, int velocity_threshold) {
        if(Math.abs(xDiff) > Math.abs(yDiff)){
            if(Math.abs(xDiff) > threshoold && Math.abs(velocityX) >velocity_threshold){
                if(xDiff>0){
                    //Se movio hacia la derecha Derecha
//                    if(touchAScreenLimit(getWidthDevice(),e2.getX())) {
//                        Log.e("Se movio a la derecha", "X: " + getWidthDevice() + ", Y: "+ e2.getY());
//                    }
                    Log.i("Se movio a la derecha", "X: " + getWidthDevice() + ", Y: "+ e2.getY());

                } else {
//                    if(touchAScreenLimit(0,e2.getX())) {
//                        Log.e("Se movio a la izquierda", "X: " + 0 + ", Y: "+ e2.getY());
//                    }
                    Log.i("Se movio a la izquierda", "X: " + 0 + ", Y: "+ e2.getY());

                }
            }
        }
    }


    //PINCH LISTENER
    private class SwipeListener implements View.OnTouchListener{

        GestureDetector gestureDetector;

        SwipeListener(View view){
            final int threshoold = 100;
            final int velocity_threshold = 100;

            GestureDetector.SimpleOnGestureListener listener =
                    new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            float xDiff = e2.getX() - e1.getX();
                            float yDiff = e2.getY() - e1.getY();

                            try{
                                if(isPinchActivate) {
                                    sendCoordsToPinch(xDiff, yDiff, e2, threshoold, velocityX, velocityY, velocity_threshold);
                                }
                                return true;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return false;
                        }
                    };
            gestureDetector = new GestureDetector(getApplicationContext(), listener);

            view.setOnTouchListener(this);


        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return gestureDetector.onTouchEvent(event);
        }
    }
}