package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.viewmodels.LastProjectViewModel;

public class LastProjectActivity extends AppCompatActivity {

    private LastProjectViewModel model;
    Button btnQr;
    ImageView imgQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_project);

        btnQr = findViewById(R.id.btnQrCode);
        imgQr = findViewById(R.id.ivQr);
        model = ViewModelProviders.of(this).get(LastProjectViewModel.class);

        ///
        String deviceName = getDeviceName();
        //

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(deviceName, BarcodeFormat.QR_CODE, 300,300);
                    imgQr.setImageBitmap(bitmap);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}