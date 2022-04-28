package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Canva;

@Dao
public interface CanvaDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Canva canva);

}
