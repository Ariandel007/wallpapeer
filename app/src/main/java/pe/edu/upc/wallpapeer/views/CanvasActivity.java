package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import pe.edu.upc.wallpapeer.BR;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityCanvasBinding;
import pe.edu.upc.wallpapeer.viewmodels.MainCanvasViewModel;

public class CanvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCanvasBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_canvas);

        MainCanvasViewModel viewModel = new MainCanvasViewModel();
        activityMainBinding.setVariable(BR.mainCanvasViewModel, viewModel);
        activityMainBinding.executePendingBindings();


    }
}