package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import io.reactivex.Completable;
import pe.edu.upc.wallpapeer.entities.Canva;

@Dao
public interface CanvaDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Canva canva);

}
