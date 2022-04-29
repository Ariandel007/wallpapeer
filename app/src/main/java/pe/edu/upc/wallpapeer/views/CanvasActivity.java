package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.viewmodels.MainCanvasViewModel;

public class CanvasActivity extends AppCompatActivity {

    private MainCanvasViewModel model;
    FloatingActionButton btnQr;
    ImageView imgQr;
    private String userDeviceName;
    private ConstraintLayout constraintLayout;
    private boolean imgQrShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_canvas);

        model = ViewModelProviders.of(this).get(MainCanvasViewModel.class);

        btnQr = findViewById(R.id.btn_qr);
        imgQr = findViewById(R.id.img_qr);
        constraintLayout = findViewById(R.id.popup_qr);
        constraintLayout.setVisibility(View.GONE);

        userDeviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        if (userDeviceName == null)
            userDeviceName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

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