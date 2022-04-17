package pe.edu.upc.wallpapeer.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import pe.edu.upc.wallpapeer.Constants;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityJoinLienzoBinding;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.viewmodels.JoinLienzoViewModel;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class JoinLienzoActivity extends AppCompatActivity {

//    private View chatBox;
//    private MessageListAdapter adapter;
    private String addressee;
    private String startDate;
    private boolean isOffline;
    private JoinLienzoViewModel model;
    private ConstraintLayout loadingScreen;
//    private ConstraintLayout messengerLayout;
    Button btnDecodes;

    private String targetDeviceName = "";

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
            model.chatIsReady().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
                        loadingScreen.setVisibility(View.GONE);
//                        messengerLayout.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(getSupportActionBar()).show();
                        addressee = model.getAddressee();
                        getSupportActionBar().setTitle(addressee);
//                        if (dialogActive[0]) {
//                            dialogs[0].dismiss();
//                        }
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
//                    CharSequence[] items = new CharSequence[peers.size()];
//                    int i = 0;
//                    for (WifiP2pDevice wifiP2pDevice : peers) {
//                        items[i] = wifiP2pDevice.deviceName;
//                        i++;
//                    }
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
//                    if (!dialogActive[0]) {
//                        dialogs[0] = adb.show();
//                        dialogActive[0] = true;
//                    } else {
//                        dialogs[0].dismiss();
//                        dialogs[0] = adb.show();
//                    }
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

    }

    private void initConnection() {
//        messengerLayout = findViewById(R.id.messengerLayout);
//        chatBox = findViewById(R.id.layout_chatbox);
        loadingScreen = findViewById(R.id.loadingScreen);
        loadingScreen.setVisibility(View.GONE);
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
        model = ViewModelProviders.of(this).get(JoinLienzoViewModel.class);
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
    }

    public void connectionToDevice() {
        List<WifiP2pDevice> wifiP2pDevices = this.model.getPeerList().getValue();
        wifiP2pDevices.size();
        if(targetDeviceName.equals("")) {
            return;
        }

        List<WifiP2pDevice> peersFindedWithTargetDeviceName = new ArrayList<>();
        for (WifiP2pDevice peer :wifiP2pDevices) {
            if(peer.deviceName.contains(targetDeviceName)) {
                peersFindedWithTargetDeviceName.add(peer);
            }
        }
        if(peersFindedWithTargetDeviceName.size() == 0) {
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