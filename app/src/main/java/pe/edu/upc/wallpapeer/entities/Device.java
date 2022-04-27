package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device")
public class Device {
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "width_screen")
    public float widthScreen;

    @ColumnInfo(name = "height_screen")
    public float heightScreen;

    @ColumnInfo(name = "device_name")
    public String deviceName;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    public String id_paleta;


}
