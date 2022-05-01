package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "device",
        foreignKeys ={
                @ForeignKey(
                        entity = Palette.class,
                        parentColumns = "id",
                        childColumns = "id_paleta",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Project.class,
                        parentColumns = "id",
                        childColumns = "id_project",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                )
})
public class Device {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "width_screen")
    public float widthScreen;

    @ColumnInfo(name = "height_screen")
    public float heightScreen;

    @ColumnInfo(name = "device_name")
    public String deviceName;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    //FKS
    @ColumnInfo(name = "id_paleta")
    public String id_paleta;

    @ColumnInfo(name = "id_project")
    public String id_project;


    public Device(@NonNull String id, float widthScreen, float heightScreen, String deviceName, String macAddress, String id_paleta, String id_project) {
        this.id = id;
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.id_paleta = id_paleta;
        this.id_project = id_project;
    }

    public Device() {}

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public float getWidthScreen() {
        return widthScreen;
    }

    public void setWidthScreen(float widthScreen) {
        this.widthScreen = widthScreen;
    }

    public float getHeightScreen() {
        return heightScreen;
    }

    public void setHeightScreen(float heightScreen) {
        this.heightScreen = heightScreen;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getId_paleta() {
        return id_paleta;
    }

    public void setId_paleta(String id_paleta) {
        this.id_paleta = id_paleta;
    }

    public String getId_project() {
        return id_project;
    }

    public void setId_project(String id_project) {
        this.id_project = id_project;
    }
}
