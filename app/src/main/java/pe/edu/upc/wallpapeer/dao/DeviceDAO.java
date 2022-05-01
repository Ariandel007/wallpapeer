package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.relations.PaletteDevice;

@Dao
public interface DeviceDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Device device);

//    @Transaction
//    @Query("SELECT * FROM Device")
//    public List<PaletteDevice> getPalettesDevices();
    @Transaction
    @Query("SELECT * FROM Device")
    Single<List<Device>> getDevices();

    @Query("SELECT * FROM Device WHERE device_name = :deviceName LIMIT 1")
    Single<Device> getDeviceByDeviceName(String deviceName);

}
