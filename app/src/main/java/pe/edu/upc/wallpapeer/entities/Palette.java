package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "palette",
        foreignKeys = @ForeignKey(
        entity = Device.class,
        parentColumns = "id",
        childColumns = "id_device",
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
))
public class Palette implements Serializable {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "selected_option")
    public int selectedOption;

    @ColumnInfo(name = "sub_option")
    public int subOption;

    @ColumnInfo(name = "id_device")
    public String id_device;

    @ColumnInfo(name = "palette_device_name")
    public String paletteDeviceName;

    public String getId_device() {
        return id_device;
    }

    public void setId_device(String id_device) {
        this.id_device = id_device;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getSubOption() {
        return subOption;
    }

    public void setSubOption(int subOption) {
        this.subOption = subOption;
    }

    public String getPaletteDeviceName() {
        return paletteDeviceName;
    }

    public void setPaletteDeviceName(String paletteDeviceName) {
        this.paletteDeviceName = paletteDeviceName;
    }
}
