package pe.edu.upc.wallpapeer.views;

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
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
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
    private String projetcId = "";
    private String deviceId  = "";
    private String canvaId   = "";
    private  List<Element> elementList;

    //para el pinch
    boolean isPinchActivate = true;
    SwipeListener swipeListener;
    CoordinatorLayout mainScreenJoinLienzo;
    //

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
        Bundle extras = getIntent().getExtras();
        Context contextCanvas = this;
        if (extras != null) {
            projetcId = extras.getString("project_id");
            deviceId = extras.getString("device_id");
            canvaId = extras.getString("canva_id");

            if(extras.getString("project_load").equals("loaded_project")) {
                //Hacer cosas adicionales para cuando cargue un proyecto
                loadExistingProject(contextCanvas);
            } else {
                //Hacer cosas cuando se cree un proyecto
                loadNewProject(contextCanvas);
            }
        }

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

//            model.getInicioLaBusquedaDePares().observe(this, new Observer<Boolean>() {
//                @Override
//                public void onChanged(@Nullable Boolean aBoolean) {
//                    if (aBoolean != null && aBoolean) {
//                        constraintLayoutLoadingSearchPeers.setVisibility(View.GONE);
//                        btnQr.setVisibility(View.VISIBLE);
//                        canvasView.setVisibility(View.VISIBLE);
//                    }
//                }
//            });

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


        //Para el pinch
        mainScreenJoinLienzo = findViewById(R.id.mainScreenJoinLienzo);
        swipeListener = new SwipeListener(mainScreenJoinLienzo);
    }

    public void initializaPeerSearch() {
        model.getInicioLaBusquedaDePares().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    constraintLayoutLoadingSearchPeers.setVisibility(View.GONE);
                    btnQr.setVisibility(View.VISIBLE);
                    canvasView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    public void loadNewProject(Context contextCanvas) {
        AppDatabase.getInstance().projectDAO().getProject(projetcId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe((Project project) -> {
            Log.e("Proyecto Traido", project.toString());
            AppDatabase.getInstance().canvaDAO().getCanva(canvaId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe((canva)-> {
                        Log.e("Canvas Traido", canva.toString());
                        elementList = new ArrayList<>();
                        canvasView.setElementListCanvas(elementList);
                        canvasView.setCanvasContext(contextCanvas);
                        canvasView.setCurrentProjectEntity(project);
                        canvasView.setCurrentCanvaEntity(canva);
                        Toast.makeText(CanvasActivity.this,"Se inicio canvas", Toast.LENGTH_LONG).show();
                        //Se inicia la busqueda de pares
                        initializaPeerSearch();
                        // Se inicializa observable del proyecto
                        startElementObservable(contextCanvas);
                    },
                    throwable -> {
                        Log.e("ERROR - GET PRY", throwable.getMessage());
                    });
        }, throwable -> {
            Log.e("ERROR - GET PRY", throwable.getMessage());
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
                            Toast.makeText(CanvasActivity.this,"Se inicio canvas", Toast.LENGTH_LONG).show();
                            //Se inicia la busqueda de pares
                            initializaPeerSearch();
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