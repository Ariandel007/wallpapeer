package pe.edu.upc.wallpapeer.viewmodels.factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pe.edu.upc.wallpapeer.model.User;
import pe.edu.upc.wallpapeer.viewmodels.LoginViewModel;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private User user;
    private Context context;

    public LoginViewModelFactory(User user, Context context){
        this.user = user;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T)new LoginViewModel(user, context);
    }

}
