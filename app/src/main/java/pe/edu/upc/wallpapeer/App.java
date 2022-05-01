package pe.edu.upc.wallpapeer;

import android.app.Application;
import android.content.Context;

import pe.edu.upc.wallpapeer.utils.AppDatabase;

public class App extends Application {

    private static Context context;
    private static AppDatabase appDatabase;

    public static Context getContext() {
        return context;
    }
    public static AppDatabase getAppDatabase() { return appDatabase; }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        appDatabase = AppDatabase.getInstance(context);
    }
}
