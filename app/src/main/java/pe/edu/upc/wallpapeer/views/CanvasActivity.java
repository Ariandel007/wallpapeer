package pe.edu.upc.wallpapeer.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
import pe.edu.upc.wallpapeer.utils.MyLastPinch;
import pe.edu.upc.wallpapeer.utils.PaletteOption;
import pe.edu.upc.wallpapeer.utils.PaletteState;
import pe.edu.upc.wallpapeer.utils.QrMessage;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;
import pe.edu.upc.wallpapeer.views.custom.CanvasView;

public class CanvasActivity extends AppCompatActivity {

    private ConnectionPeerToPeerViewModel model;
    FloatingActionButton btnQr, btnLockPinch;
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
    private String deviceId = "";
    private String canvaId = "";
    private List<Element> elementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_canvas);

        model = ViewModelProviders.of(this).get(ConnectionPeerToPeerViewModel.class);
        model.isMainCanvas = true;

        btnQr = findViewById(R.id.btn_qr);
        btnLockPinch = findViewById(R.id.btn_lock_pinch);
        imgQr = findViewById(R.id.img_qr);
        canvasView = findViewById(R.id.canvas);
        constraintLayout = findViewById(R.id.popup_qr);
        constraintLayout.setVisibility(View.GONE);
        constraintLayoutLoadingSearchPeers = findViewById(R.id.search_peer_connections_canvas);
        //
        constraintLayoutLoadingSearchPeers.setVisibility(View.VISIBLE);
        btnQr.setVisibility(View.GONE);
        btnLockPinch.setVisibility(View.GONE);
        canvasView.setVisibility(View.GONE);
        canvasView.setModel(model);
        //
        isOffline = getIntent().getBooleanExtra(Constants.IS_OFFLINE, false);
        addressee = getIntent().getStringExtra(Constants.ADDRESAT_NAME);
        startDate = getIntent().getStringExtra(Constants.DATE);

        //EJEMPLO PARA OBTENER INTENTS
        Bundle extras = getIntent().getExtras();
        Context contextCanvas = this;
        if (extras != null) {
            projetcId = extras.getString("project_id");
            LastProjectState.getInstance().setProjectId(projetcId);
            deviceId = extras.getString("device_id");
            canvaId = extras.getString("canva_id");

            if (extras.getString("project_load").equals("loaded_project")) {
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
                    if (imgQrShowed) {
                        constraintLayout.setVisibility(View.GONE);
                        imgQrShowed = !imgQrShowed;
                    } else {
                        constraintLayout.setVisibility(View.VISIBLE);
                        //Enviar esto al json:
                        QrMessage qrMessage = new QrMessage(userDeviceName, userDeviceName);
                        String qrMessageJson = JsonConverter.getGson().toJson(qrMessage);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.encodeBitmap(qrMessageJson, BarcodeFormat.QR_CODE, 300, 300);
                        imgQr.setImageBitmap(bitmap);
                        imgQrShowed = !imgQrShowed;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                }
            }
        });
    }

    public void initializaPeerSearch() {
        model.getInicioLaBusquedaDePares().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    constraintLayoutLoadingSearchPeers.setVisibility(View.GONE);
                    btnQr.setVisibility(View.VISIBLE);
                    btnLockPinch.setVisibility(View.VISIBLE);
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
                        //Instancia de last Pinch
                        MyLastPinch.getInstance().setProjectId(projetcId);
                        MyLastPinch.getInstance().setProject(project);
                        MyLastPinch.getInstance().setCanvaId(canvaId);
                        MyLastPinch.getInstance().setCanva(canva);
                        //Se inicia la busqueda de pares
                        initializaPeerSearch();

                        startPaletteObservable(contextCanvas);
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
                            //Instancia de last Pinch
                            MyLastPinch.getInstance().setProjectId(projetcId);
                            MyLastPinch.getInstance().setProject(project);
                            MyLastPinch.getInstance().setCanvaId(canvaId);
                            MyLastPinch.getInstance().setCanva(canva);

                            //Se inicia la busqueda de pares
                            initializaPeerSearch();

                            startPaletteObservable(contextCanvas);
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

    public void startPaletteObservable(Context context1){
        AppDatabase.getInstance().paletteDAO().listenPaletteChangesByDeviceName(LastProjectState.getInstance().getDeviceName()).observe(this, new Observer<Palette>() {
            @Override
            public void onChanged(Palette palette) {
                if(PaletteState.getInstance() != null){
//                    Toast.makeText(CanvasActivity.this, "Se recontra logró", Toast.LENGTH_SHORT).show();
                    switch (PaletteState.getInstance().getSelectedOption()){
                        case 0:
                            Toast.makeText(context1, "Se recibió la opción de trazo", Toast.LENGTH_SHORT).show();
                            Log.i("Pencil Button", "Se recibió la opción de trazo");
                            break;
                        case 1:
                            Toast.makeText(context1, "Se recibió la opción de deshacer", Toast.LENGTH_SHORT).show();
                            Log.i("Undo Button", "Se recibió la opción de deshacer");
                            AppDatabase.getInstance().elementDAO().deleteOne().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                                Log.i("Undo Button", "Se eliminó el último elemento");
                                Toast.makeText(context1, "Se eliminó el último elemento insertado", Toast.LENGTH_SHORT).show();
                            });
                            break;
                        case 2:
                            Toast.makeText(context1, "Se recibió la opción de modificar capa", Toast.LENGTH_SHORT).show();
                            Log.i("Layers Button", "Se recibió la opción de modificar capa");
                            break;
                        case 3:
                            Toast.makeText(context1, "Se recibió la opción de texto", Toast.LENGTH_SHORT).show();
                            Log.i("Text Button", "Se recibió la opción de texto");
                            break;
                        case PaletteOption.ROTATE:
                            Toast.makeText(context1, "Se recibió la opción de rotar", Toast.LENGTH_SHORT).show();
                            Log.i("Rotate Button", "Se recibió la opción de rotar");
                            break;
                        case PaletteOption.SHAPES_OPTION:
                            Toast.makeText(context1, "Se recibió la opción de añadir forma", Toast.LENGTH_SHORT).show();
                            Log.i("Shape Button", "Se recibió la opción de añadir forma");
                            break;
                        case PaletteOption.COLOR:
                            Toast.makeText(context1, "Se recibió la opción de cambiar color", Toast.LENGTH_SHORT).show();
                            Log.i("Shape Button", "Se recibió la opción de cambiar color");
                            break;
                    }
                }
            }
        });
    }


}