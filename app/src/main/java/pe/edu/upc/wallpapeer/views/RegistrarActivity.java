package pe.edu.upc.wallpapeer.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityRegistrarBinding;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class RegistrarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        ActivityRegistrarBinding activityRegistrarBinding = DataBindingUtil.setContentView(this,R.layout.activity_registrar);

        LoginViewModel userViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(new User(), this)).get(LoginViewModel.class);

        activityRegistrarBinding.setLoginViewModel(userViewModel);
    }
}