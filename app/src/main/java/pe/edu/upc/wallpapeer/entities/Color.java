package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "color")
public class Color {

    @PrimaryKey
    public String id;

    @ColumnInfo(name = "color_code")
    public String colorCode;

    public String id_element;
}
