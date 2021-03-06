package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Color;

@Dao
public interface ColorDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Color color);
}
