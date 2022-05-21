package pe.edu.upc.wallpapeer.views;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.dialogs.ImagesDialog;
import pe.edu.upc.wallpapeer.dialogs.LayersDialog;
import pe.edu.upc.wallpapeer.dialogs.ShapesDialog;
import pe.edu.upc.wallpapeer.dialogs.TextDialog;
import pe.edu.upc.wallpapeer.dtos.AddingPalette;
import pe.edu.upc.wallpapeer.dtos.ChangingOption;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CanvaStateForPalette;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
import pe.edu.upc.wallpapeer.utils.PaletteState;
import pe.edu.upc.wallpapeer.utils.QrMessage;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;
import yuku.ambilwarna.AmbilWarnaDialog;

public class JoinPaletaActivity extends AppCompatActivity implements LayersDialog.LayersDialogListener, TextDialog.TextDialogListener, ShapesDialog.ShapesDialogListener {

    private String addressee;
    private String startDate;
    private String userDeviceName;
    private String textToInsert;

    private boolean paletteIsReady = false;
    private boolean optionChanged = false;
    private boolean isOffline;
    private boolean isInNetwork = false;
    private ConnectionPeerToPeerViewModel model;
    private ConstraintLayout loadingScreen;
    private ConstraintLayout loadingPallete;
    private ConstraintLayout paletteSelector;

    private int pSelectedOption = 0;
    private int pSubOption = -1;
    private String lastTarget = "";
    List<String> layerList = new ArrayList<String>();
    private String targetDeviceName = "";
    private Integer defaultColor = Color.BLUE;

    private String trulyClientTargetDevice = "";


    Button btnDecodes, btnColor;
    ImageButton btnPencil, btnUndo, btnLayers, btnAddText, btnRotate, btnAddShape, btnAddImage;
    EditText editText;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(JoinPaletaActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    QrMessage qrMessage = JsonConverter.getGson().fromJson(result.getContents(), QrMessage.class);
                    targetDeviceName = qrMessage.getOwnername();
                    lastTarget = qrMessage.getOwnername();
                    trulyClientTargetDevice = qrMessage.getMyName();
                    Toast.makeText(JoinPaletaActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    connectionToDevice();
                }
            });

    ActivityResultLauncher<String> mGetcontent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_paleta);

        layerList.add("Traer adelante");
        layerList.add("Enviar atrás");
        layerList.add("Traer al frente");
        layerList.add("Enviar al fondo");

        btnDecodes = findViewById(R.id.btnScanQr);

        btnAddShape = findViewById(R.id.btnAddShape);
        btnAddText = findViewById(R.id.btnAddText);
        btnLayers = findViewById(R.id.btnLayers);
        btnPencil = findViewById(R.id.btnPencil);
        btnRotate = findViewById(R.id.btnRotate);
        btnUndo = findViewById(R.id.btnUndo);
        btnColor = findViewById(R.id.btnColor);
        btnAddImage = findViewById(R.id.btnAddImage);

        editText = new EditText(this);

        userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (userDeviceName == null)
            userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        initConnection();

        if (isOffline) {
            model.setAddressee(addressee);
        } else {
            loadingScreen.setVisibility(View.VISIBLE);

            new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    btnDecodes.setVisibility(View.VISIBLE);
                }}, 5000);

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
                        isInNetwork = true;
                        if(lastTarget.equals("")){
                            return;
                        }
                        sendPaletteRequest();

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
            AppDatabase.getInstance().projectDAO().listenAllProjectChanges().observe(this, new Observer<List<Project>>() {
                @Override
                public void onChanged(List<Project> projects) {
                    if(!paletteIsReady && projects.size() > 0 && isInNetwork){
                        paletteIsReady = true;
                        loadingPallete.setVisibility(View.GONE);
                        paletteSelector.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    @Override
    public void applyLayerOption(int option) {
        pSubOption = option;
        PaletteState.getInstance().setSubOption(pSubOption);
        PaletteState.getInstance().setSelectedOption(pSelectedOption);
        sendSelectedOption(pSelectedOption, pSubOption);
    }

    @Override
    public void applyTextOption(String textToSend) {
        sendSelectedOptionText(pSelectedOption, pSubOption, textToSend);
    }

    @Override
    public void applyShapeOption(int option) {
        pSubOption = option;
        PaletteState.getInstance().setSubOption(pSubOption);
        PaletteState.getInstance().setSelectedOption(pSelectedOption);
        sendSelectedOption(pSelectedOption, pSubOption);
    }

    private void initConnection() {
        loadingScreen = findViewById(R.id.loadingScreen);
        loadingScreen.setVisibility(View.GONE);
        loadingPallete = findViewById(R.id.loadingPallete);
        loadingPallete.setVisibility(View.GONE);
        paletteSelector = findViewById(R.id.paletteSelector);
        paletteSelector.setVisibility(View.GONE);
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

        btnPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 0;
                pSubOption = -1;
                sendSelectedOption(pSelectedOption, pSubOption);
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 1;
                pSubOption = -1;
                sendSelectedOption(pSelectedOption, pSubOption);
            }
        });

        btnLayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 2;
                openLayersDialog();
            }
        });

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 3;
                pSubOption = -1;
                openTextDialog();
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 4;
                pSubOption = -1;
                sendSelectedOption(pSelectedOption, pSubOption);
            }
        });

        btnAddShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 5;
                openShapesDialog();
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 6;
                openColorPicker();
            }
        });

        final Intent intent = new Intent(this, ImageListActivity.class);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pSelectedOption = 7;
                startActivityForResult(intent, 1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            int subOption = data.getIntExtra(ImageListActivity.RESULT_POSITION, 0);
            pSubOption = subOption;
            Toast.makeText(this, "Escogiste la imagen número: " + subOption, Toast.LENGTH_SHORT).show();
            sendSelectedOption(pSelectedOption, pSubOption);
        }
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                btnColor.setBackgroundColor(defaultColor);
                sendSelectedOption(pSelectedOption, pSubOption);
            }
        });
        colorPicker.show();
    }

    private void openTextDialog() {
        TextDialog textDialog = new TextDialog();
        textDialog.show(getSupportFragmentManager(), "text");
    }

    Context context = this;

    public void openLayersDialog() {
        LayersDialog layersDialog = new LayersDialog();
        layersDialog.show(getSupportFragmentManager(), "layer");
    }

    public void openShapesDialog(){
        ShapesDialog shapesDialog = new ShapesDialog();
        shapesDialog.show(getSupportFragmentManager(), "shape");
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

    private void sendPaletteRequest(){
        AddingPalette addingPalette = new AddingPalette();
        addingPalette.setA1_eventCode(CodeEvent.ADDING_PALLETE_TO_DEVICE);
        addingPalette.setDeviceName(userDeviceName);
        addingPalette.setTargetDeviceName(lastTarget);
        addingPalette.setMacAddress("");
        addingPalette.setSelectedOption(0);
        addingPalette.setSubOption(-1);
        addingPalette.setOriginalSender(LastProjectState.getInstance().getDeviceName());
        addingPalette.setTrueTargetDevice(trulyClientTargetDevice);

        String json = JsonConverter.getGson().toJson(addingPalette);
        model.sendMessage(json);
    }

    private void sendSelectedOption(int selectedOption, int subOption){
        if(CanvaStateForPalette.getInstance() == null){
            return;
        }
        if(CanvaStateForPalette.getInstance().getAcceptingPalette().getLinkedDevice() == null){
            return;
        }
        ChangingOption changingOption = new ChangingOption();
        changingOption.setA1_eventCode(CodeEvent.SELECT_OPTION_PALLETE);
        changingOption.setDeviceName(userDeviceName);
        changingOption.setTargetDeviceName(trulyClientTargetDevice);
        changingOption.setMacAddress("");
        changingOption.setSelectedOption(selectedOption);
        changingOption.setSubOption(subOption);
        changingOption.setColor(defaultColor);
        changingOption.setTextToInsert("");
        changingOption.setOriginalSender(LastProjectState.getInstance().getDeviceName());

        String json = JsonConverter.getGson().toJson(changingOption);
        model.sendMessage(json);
    }

    private void sendSelectedOptionText(int selectedOption, int subOption, String textToInsert){
        if(CanvaStateForPalette.getInstance() == null){
            return;
        }
        if(CanvaStateForPalette.getInstance().getAcceptingPalette().getLinkedDevice() == null){
            return;
        }
        ChangingOption changingOption = new ChangingOption();
        changingOption.setA1_eventCode(CodeEvent.SELECT_OPTION_PALLETE);
        changingOption.setDeviceName(userDeviceName);
        changingOption.setTargetDeviceName(trulyClientTargetDevice);
        changingOption.setMacAddress("");
        changingOption.setSelectedOption(selectedOption);
        changingOption.setSubOption(subOption);
        changingOption.setColor(defaultColor);
        changingOption.setTextToInsert(textToInsert);
        changingOption.setOriginalSender(LastProjectState.getInstance().getDeviceName());

        String json = JsonConverter.getGson().toJson(changingOption);
        model.sendMessage(json);
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