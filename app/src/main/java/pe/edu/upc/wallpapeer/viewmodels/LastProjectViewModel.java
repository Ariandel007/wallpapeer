package pe.edu.upc.wallpapeer.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import pe.edu.upc.wallpapeer.model.User;

public class LastProjectViewModel extends AndroidViewModel {
    private Application app;


    public LastProjectViewModel(@NonNull Application application) {
        super(application);
        this.app = application;
    }
}
