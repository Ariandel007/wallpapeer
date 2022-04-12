package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityChangePasswordBinding;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        User user = (User)getIntent().getSerializableExtra("USER");

        ActivityChangePasswordBinding activityChangePasswordBinding = DataBindingUtil.setContentView(this,R.layout.activity_change_password);
        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(user,this)).get(LoginViewModel.class);

        activityChangePasswordBinding.setLoginViewModel(loginViewModel);
    }
}