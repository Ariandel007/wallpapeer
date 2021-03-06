package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.Objects;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityMainBinding;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.utils.GalleryMap;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
import pe.edu.upc.wallpapeer.utils.RememberSocketsAddress;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private static final int MY_REQUEST_PERMISSION_CODE = 1;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        loginViewModel = ViewModelProviders.of(this,new LoginViewModelFactory(new User(), this)).get(LoginViewModel.class);

        activityMainBinding.setLoginViewModel(loginViewModel);

        //SettingUP
        setUpGPS();
//        setUpDrawer();
//        setUpHistoryPage();
//        setUpViewModel();
        String deviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (deviceName == null)
            deviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
        LastProjectState.getInstance().setDeviceName(deviceName);

        GalleryMap.getInstance();
        RememberSocketsAddress.getInstance();
    }

//    private void setUpViewModel() {
//        loginViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
//
//        model.getHistory().observe(this, new Observer<List<ChatHistoryEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<ChatHistoryEntity> chats) {
//                assert chats != null;
//                assert getSupportActionBar() != null;
//
//                if (chats.size() == 0) {
//                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.historyPageTitle));
//                    emptyPageMessage.setVisibility(View.VISIBLE);
//                    chatHistoryView.setVisibility(View.GONE);
//                    clearHistoryButton.setVisibility(View.GONE);
//                } else {
//                    emptyPageMessage.setVisibility(View.GONE);
//                    chatHistoryView.setVisibility(View.VISIBLE);
//                    clearHistoryButton.setVisibility(View.VISIBLE);
//                    getSupportActionBar().setTitle(getString(R.string.historyPageTitle) + "(" + chats.size() + ")");
//                    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.historyPageTitle) + "(" + chats.size() + ")");
//                }
//                historyAdapter.updateData(chats);
//
//            }
//        });
//    }

    public void setUpGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_REQUEST_PERMISSION_CODE);
        }

        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        manager.setWifiEnabled(true);
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Por favor, encienda el GPS");

        adb.setPositiveButton("yo inclu??", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    dialog.dismiss();
                } else {
                    adb.show();
                }
            }
        });
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            adb.show();
        }
    }

//    private void setUpHistoryPage() {
//        clearHistoryButton = findViewById(R.id.clearHistory);
//        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                model.clearHistory();
//            }
//        });
//        chatHistoryView = findViewById(R.id.chatHistory);
//        chatHistoryView.setLayoutManager(new LinearLayoutManager(this));
//        historyAdapter = new ChatListAdapter();
//        chatHistoryView.setAdapter(historyAdapter);
//        emptyPageMessage = findViewById(R.id.chat_list_empty_message);
//    }


//    private void setUpDrawer() {
//        drawer = findViewById(R.id.drawer_layout);
//        drawer.setVisibility(View.VISIBLE);
//        toggle = new ActionBarDrawerToggle(this, drawer, R.string.Open, R.string.Close);
//        toggle.setDrawerIndicatorEnabled(true);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(getString(R.string.historyPageTitle));
//
//
//        NavigationView navView = findViewById(R.id.nav_view);
//        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                if (id == R.id.menu_chat_button) {
//                    //Objects.requireNonNull(getSupportActionBar()).hide();
//                    //drawer.setVisibility(View.GONE);
//                    model.startSearch();
//                }
//                drawer.closeDrawers();
//                return false;
//            }
//        });
//    }
}