package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import pe.edu.upc.wallpapeer.entities.Palette;

import pe.edu.upc.wallpapeer.entities.relations.PaletteDevice;

@Dao
public interface PaletteDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPalette(Palette palette);




}
