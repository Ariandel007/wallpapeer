package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Image;

@Dao
public interface ImageDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Image image);
}
