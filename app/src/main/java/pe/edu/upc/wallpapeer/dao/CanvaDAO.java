package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.reactivex.Completable;
import io.reactivex.Single;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Project;

@Dao
public interface CanvaDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Canva canva);

    @Query("SELECT * FROM canva WHERE id = :id")
    Single<Canva> getCanva(String id);

}
