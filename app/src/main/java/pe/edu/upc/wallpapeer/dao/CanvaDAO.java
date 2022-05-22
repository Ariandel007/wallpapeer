package pe.edu.upc.wallpapeer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Project;

@Dao
public interface CanvaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Canva canva);

    @Update
    Completable update(Canva canva);

    @Query("SELECT * FROM canva WHERE id = :id")
    Single<Canva> getCanva(String id);

    @Query("SELECT * FROM canva WHERE id_device = :idDevice")
    Single<Canva> getCanvaByIdDevice(String idDevice);

    @Query("SELECT * FROM canva " +
            "INNER JOIN device ON device.id = id_device WHERE device.id_project = :idProject " +
            " ORDER BY mod_date ASC")
    LiveData<List<Canva>> getCanvaByIdProjectLiveData(String idProject);

    @Query("SELECT * FROM canva")
    LiveData<List<Canva>> listenAllCanvasChanges();


}
