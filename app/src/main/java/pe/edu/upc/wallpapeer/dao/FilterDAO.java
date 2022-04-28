package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Filter;

@Dao
public interface FilterDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Filter filter);
}
