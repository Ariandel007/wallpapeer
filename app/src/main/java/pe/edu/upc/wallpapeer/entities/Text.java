package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "text")
public class Text {
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "size")
    public int size;

    @ColumnInfo(name = "text")
    public String textContent;

    public String id_element;
}
