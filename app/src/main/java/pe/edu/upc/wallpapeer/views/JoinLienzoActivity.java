package pe.edu.upc.wallpapeer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.databinding.ActivityJoinLienzoBinding;
import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;
import pe.edu.upc.wallpapeer.viewmodels.factory.LoginViewModelFactory;

public class JoinLienzoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lienzo);

        User user = (User)getIntent().getSerializableExtra("USER");

        ActivityJoinLienzoBinding activityJoinLienzoBinding = DataBindingUtil.setContentView(this,R.layout.activity_join_lienzo);
        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(user, this)).get(LoginViewModel.class);

        activityJoinLienzoBinding.setLoginViewModel(loginViewModel);

    }
}