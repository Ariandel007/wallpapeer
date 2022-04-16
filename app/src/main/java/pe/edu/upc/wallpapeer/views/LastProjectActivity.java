package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProviders;
//
//import android.graphics.Bitmap;
import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;

import pe.edu.upc.wallpapeer.R;
//import pe.edu.upc.wallpapeer.viewmodels.LastProjectViewModel;

public class LastProjectActivity extends AppCompatActivity {

//    private LastProjectViewModel model;
//    Button btnQr = findViewById(R.id.btnQrCode);
//    ImageView imgQr = findViewById(R.id.ivQr);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_project);
//        model = ViewModelProviders.of(this).get(LastProjectViewModel.class);

//        btnQr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                    Bitmap bitmap = barcodeEncoder.encodeBitmap("testdataxd", BarcodeFormat.QR_CODE, 300,300);
//                    imgQr.setImageBitmap(bitmap);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
    }

}