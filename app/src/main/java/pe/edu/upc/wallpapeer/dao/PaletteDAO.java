package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import pe.edu.upc.wallpapeer.entities.Palette;

import pe.edu.upc.wallpapeer.entities.relations.PaletteDevice;

@Dao
public interface PaletteDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Palette palette);

    @Update
    Completable update(Palette palette);

    @Query("Select * from palette " +
            "INNER JOIN device ON device.id = id_device WHERE device.id_project = :idProject " +
            "and id_device = :idDevice")
    Single<Palette> getPaletteByProjectIdDeviceId(String idProject, String idDevice);



}
