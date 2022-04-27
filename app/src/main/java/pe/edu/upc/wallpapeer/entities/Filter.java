package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "filter")
public class Filter {

    @PrimaryKey
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    public String id_image;
}
