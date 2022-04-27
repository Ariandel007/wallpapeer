package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Element;


@Dao
public interface ElementDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProject(Element element);
}
