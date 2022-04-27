package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "canva")
public class Canva {
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "is_main")
    public boolean isMain;

    @ColumnInfo(name = "height_canvas")
    public float heightCanvas;

    @ColumnInfo(name = "width_canvas")
    public float widthCanvas;

    @ColumnInfo(name = "posx")
    public float posX;

    @ColumnInfo(name = "posy")
    public float posY;

}
