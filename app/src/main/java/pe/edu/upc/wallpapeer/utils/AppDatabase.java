package pe.edu.upc.wallpapeer.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pe.edu.upc.wallpapeer.App;
import pe.edu.upc.wallpapeer.dao.CanvaDAO;
import pe.edu.upc.wallpapeer.dao.DeviceDAO;
import pe.edu.upc.wallpapeer.dao.PaletteDAO;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Palette;

@Database(entities = {Palette.class, Canva.class, Device.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PaletteDAO paletteDAO();
    public abstract CanvaDAO canvaDAO();
    public abstract DeviceDAO deviceDAO();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {

        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(App.getContext(),
                    AppDatabase.class, "localDB").build();
        }

        return INSTANCE;
    }


}
