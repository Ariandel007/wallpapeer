package pe.edu.upc.wallpapeer.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
import pe.edu.upc.wallpapeer.utils.MyLastPinch;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;
import pe.edu.upc.wallpapeer.views.custom.CanvasView;

public class JoinLienzoActivity extends AppCompatActivity {

    private ConnectionPeerToPeerViewModel model;
    FloatingActionButton btnQr, btnLockPinch;
    ImageView imgQr;
    private String userDeviceName;
    private ConstraintLayout constraintLayout;
    //ConstraintLayout constraintLayoutLoadingSearchPeers;
    private boolean imgQrShowed = false;
    private String addressee;
    private boolean isOffline;
    private String startDate;
    private CanvasView canvasView;
    private String projetcId = "";
    private String deviceId  = "";
    private String canvaId   = "";
    private  List<Element> elementList;



    private ConstraintLayout loadingScreen;
    private ConstraintLayout pinchScreen;
    Button btnDecodes;

    private String targetDeviceName = "";

    //para el pinch
    private Boolean waitToJoinLienzo = true;
//    boolean isPinchActivate = true;
    SwipeListener swipeListener;
    CoordinatorLayout mainScreenJoinLienzo;
    //


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

        userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (userDeviceName == null)
            userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        initConnection();

        userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (userDeviceName == null)
            userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");


        //
        btnQr = findViewById(R.id.btn_qr_join_lienzo);
        btnLockPinch = findViewById(R.id.btn_lock_pinch_lienzo);
        imgQr = findViewById(R.id.img_qr_join_lienzo);
        canvasView = findViewById(R.id.canvas_join_lienzo);
        constraintLayout = findViewById(R.id.popup_qr_join_lienzo);
        constraintLayout.setVisibility(View.GONE);
        //constraintLayoutLoadingSearchPeers  = findViewById(R.id.search_peer_connections_canvas_join_canvas);

        //constraintLayoutLoadingSearchPeers.setVisibility(View.VISIBLE);
        btnQr.setVisibility(View.GONE);
        btnLockPinch.setVisibility(View.GONE);
        canvasView.setVisibility(View.GONE);
        canvasView.setModel(model);
        //

        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE, false);
        addressee = getIntent().getStringExtra(Constants.ADDRESAT_NAME);
        startDate = getIntent().getStringExtra(Constants.DATE);

        ///

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
                        Toast.makeText(JoinLienzoActivity.this, "Conexion realizada con dispositivo!!", Toast.LENGTH_SHORT).show();
                        loadingScreen.setVisibility(View.GONE);
                        pinchScreen.setVisibility(View.VISIBLE);
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
                        Toast.makeText(JoinLienzoActivity.this, "Uno de los dispositivos abandonó el grupo.", Toast.LENGTH_SHORT).show();
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

        //Para el pinch
        mainScreenJoinLienzo = findViewById(R.id.mainScreenJoinLienzo);
        swipeListener = new SwipeListener(mainScreenJoinLienzo);

        Context context = this;
        //listen change in Canva
        AppDatabase.getInstance().canvaDAO().listenAllCanvasChanges().observe(this, new Observer<List<Canva>>() {
            @Override
            public void onChanged(List<Canva> canvas) {
                //Haz cosas
                if(waitToJoinLienzo) {
                    projetcId = LastProjectState.getInstance().getProjectId();
                    canvaId = LastProjectState.getInstance().getCanvaId();
                    //Traer data
                    LastProjectState.getInstance().setProjectId(projetcId);
                    loadExistingProject(context);
                }
            }
        });

        btnLockPinch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView.isPinchLocked = !canvasView.isPinchLocked;
                if(canvasView.isPinchLocked) {
                    btnLockPinch.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open, null));
                } else {
                    btnLockPinch.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock, null));
                }            }
        });


    }

    public void initializaPeerSearch() {
        model.getInicioLaBusquedaDePares().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    pinchScreen.setVisibility(View.GONE);
                    btnQr.setVisibility(View.VISIBLE);
                    btnLockPinch.setVisibility(View.VISIBLE);
                    canvasView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    public void loadExistingProject(Context contextCanvas) {
        AppDatabase.getInstance().projectDAO().getProject(projetcId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe((Project project) -> {
            Log.e("Proyecto Traido", project.toString());
            AppDatabase.getInstance().canvaDAO().getCanva(canvaId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe((canva)-> {
                        AppDatabase.getInstance().elementDAO().getAllByProject(projetcId)
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                elements -> {
                                    elementList = elements;
                                    canvasView.setElementListCanvas(elements);
                                    //
//                            canvasView.setElementListCanvas(elementList);
                                    canvasView.setCanvasContext(contextCanvas);
                                    canvasView.setCurrentProjectEntity(project);
                                    canvasView.setCurrentCanvaEntity(canva);
                                    Toast.makeText(JoinLienzoActivity.this,"Se inicio canvas", Toast.LENGTH_LONG).show();
                                    //Instancia de last Pinch
                                    MyLastPinch.getInstance().setProjectId(projetcId);
                                    MyLastPinch.getInstance().setProject(project);
                                    MyLastPinch.getInstance().setCanvaId(canvaId);
                                    MyLastPinch.getInstance().setCanva(canva);

                                    //Se inicia la busqueda de pares
                                    initializaPeerSearch();

                                    waitToJoinLienzo = false;

                                    // Se inicializa observable del proyecto
                                    startElementObservable(contextCanvas);
                                    //Se pinta
                                    canvasView.triggerOnDraw();


                                }, throwable -> {
                                    Log.e("Error", "Error consulta");
                                }
                        );
                    },
                    throwable -> {
                        Log.e("ERROR - GET PRY", throwable.getMessage());
                    });
        }, throwable -> {
            Log.e("ERROR - GET PRY", throwable.getMessage());
        });
    }

    public void startElementObservable(Context contextCanvas) {
        AppDatabase.getInstance().elementDAO().getAllElementsLiveDataByProject(projetcId).observe(this, new Observer<List<Element>>() {
                    @Override
                    public void onChanged(List<Element> elements) {
                        if(elements.size() > 0) {
                            Log.e("On CHanged Elements", String.valueOf(elements.size()));
                            canvasView.setElementListCanvas(elements);
                            canvasView.triggerOnDraw();
                        }
                    }
                }

        );
    }

    private void initConnection() {

        loadingScreen = findViewById(R.id.loadingScreen);
        loadingScreen.setVisibility(View.GONE);
        pinchScreen = findViewById(R.id.PinchScreen);
        pinchScreen.setVisibility(View.GONE);


        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE, false);
        model = ViewModelProviders.of(this).get(ConnectionPeerToPeerViewModel.class);
        addressee = getIntent().getStringExtra(Constants.ADDRESAT_NAME);
//        startDate = getIntent().getStringExtra(Constants.DATE);

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
    private int getHeigthDevice() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

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
                    Log.i("Se movio a la derecha", "X: " + getWidthDevice() + ", Y: "+ e2.getY());
                    //Creamos Objecto
                    EngagePinchEvent engagePinchEvent = new EngagePinchEvent();
                    engagePinchEvent.setEventCode(CodeEvent.PINCH_EVENT);
                    engagePinchEvent.setDirection("RIGHT");
                    engagePinchEvent.setDeviceName(userDeviceName);
                    engagePinchEvent.setMacAddress("");
                    engagePinchEvent.setPosPinchX((float) getWidthDevice());
                    engagePinchEvent.setPosPinchY(e2.getY());
                    engagePinchEvent.setWidthScreenPinch((float) getWidthDevice());
                    engagePinchEvent.setHeightScreenPinch((float) getHeigthDevice());
                    engagePinchEvent.setDatePinch(new Date());

                    String json = JsonConverter.getGson().toJson(engagePinchEvent);
                    model.sendMessage(json);


                } else {
                    Log.i("Se movio a la izquierda", "X: " + 0 + ", Y: "+ e2.getY());

                    EngagePinchEvent engagePinchEvent = new EngagePinchEvent();
                    engagePinchEvent.setEventCode(CodeEvent.PINCH_EVENT);
                    engagePinchEvent.setDirection("LEFT");
                    engagePinchEvent.setDeviceName(userDeviceName);
                    engagePinchEvent.setMacAddress("");
                    engagePinchEvent.setPosPinchX(0.0f);
                    engagePinchEvent.setPosPinchY(e2.getY());
                    engagePinchEvent.setWidthScreenPinch((float) getWidthDevice());
                    engagePinchEvent.setHeightScreenPinch((float) getHeigthDevice());
                    engagePinchEvent.setDatePinch(new Date());

                    String json = JsonConverter.getGson().toJson(engagePinchEvent);
                    model.sendMessage(json);


                }
            }
        } else {
            if(Math.abs(yDiff) > threshoold && Math.abs(velocityY) > velocity_threshold) {
                if(yDiff>0){
                    //
                    Log.i("DOWN", "DOWN");

                    EngagePinchEvent engagePinchEvent = new EngagePinchEvent();
                    engagePinchEvent.setEventCode(CodeEvent.PINCH_EVENT);
                    engagePinchEvent.setDirection("DOWN");
                    engagePinchEvent.setDeviceName(userDeviceName);
                    engagePinchEvent.setMacAddress("");
                    engagePinchEvent.setPosPinchX(e2.getX());
                    engagePinchEvent.setPosPinchY((float) getHeigthDevice());
                    engagePinchEvent.setWidthScreenPinch((float) getWidthDevice());
                    engagePinchEvent.setHeightScreenPinch((float) getHeigthDevice());
                    engagePinchEvent.setDatePinch(new Date());

                    String json = JsonConverter.getGson().toJson(engagePinchEvent);
                    model.sendMessage(json);

                } else {
                    //UP
                    Log.i("UP", "UP");

                    EngagePinchEvent engagePinchEvent = new EngagePinchEvent();
                    engagePinchEvent.setEventCode(CodeEvent.PINCH_EVENT);
                    engagePinchEvent.setDirection("UP");
                    engagePinchEvent.setDeviceName(userDeviceName);
                    engagePinchEvent.setMacAddress("");
                    engagePinchEvent.setPosPinchX(e2.getX());
                    engagePinchEvent.setPosPinchY(0.0f);
                    engagePinchEvent.setWidthScreenPinch((float) getWidthDevice());
                    engagePinchEvent.setHeightScreenPinch((float) getHeigthDevice());
                    engagePinchEvent.setDatePinch(new Date());

                    String json = JsonConverter.getGson().toJson(engagePinchEvent);
                    model.sendMessage(json);

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
                                if(waitToJoinLienzo) {
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