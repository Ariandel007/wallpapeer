package pe.edu.upc.wallpapeer;

import android.app.Application;
import android.content.Context;

import pe.edu.upc.wallpapeer.utils.AppDatabase;

public class App extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppDatabase.getInstance(context);
    }
}
