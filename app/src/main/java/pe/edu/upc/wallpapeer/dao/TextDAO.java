package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Text;

@Dao
public interface TextDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Text text);
}
