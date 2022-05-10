package pe.edu.upc.wallpapeer.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pe.edu.upc.wallpapeer.App;
import pe.edu.upc.wallpapeer.dao.CanvaDAO;
import pe.edu.upc.wallpapeer.dao.ColorDAO;
import pe.edu.upc.wallpapeer.dao.DeviceDAO;
import pe.edu.upc.wallpapeer.dao.ElementDAO;
import pe.edu.upc.wallpapeer.dao.FilterDAO;
import pe.edu.upc.wallpapeer.dao.ImageDAO;
import pe.edu.upc.wallpapeer.dao.PaletteDAO;
import pe.edu.upc.wallpapeer.dao.ProjectDAO;
import pe.edu.upc.wallpapeer.dao.TextDAO;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Color;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Filter;
import pe.edu.upc.wallpapeer.entities.Image;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.entities.Text;
import pe.edu.upc.wallpapeer.entities.relations.PaletteDevice;

@Database(entities = {Palette.class,
                        Canva.class,
                        Device.class,
                        Color.class,
                        Project.class,
                        Element.class,
                        Text.class,
                        Image.class,
                        Filter.class,
                        }, version = 11)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PaletteDAO paletteDAO();
    public abstract CanvaDAO canvaDAO();
    public abstract DeviceDAO deviceDAO();
    public abstract ColorDAO colorDAO();
    public abstract ProjectDAO projectDAO();
    public abstract ElementDAO elementDAO();
    public abstract TextDAO textDAO();
    public abstract ImageDAO imageDAO();
    public abstract FilterDAO filterDAO();


    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance() {

        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(App.getContext(),
                    AppDatabase.class, "localDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }


}
