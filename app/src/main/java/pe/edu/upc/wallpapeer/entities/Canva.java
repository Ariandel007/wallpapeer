package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "canva",
        foreignKeys = @ForeignKey(
                entity = Device.class,
                parentColumns = "id",
                childColumns = "id_device",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )

)
public class Canva {
    @PrimaryKey
    @NonNull
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

    @ColumnInfo(name = "id_device")
    public String id_device;

    public Canva(@NonNull String id, boolean isMain, float heightCanvas, float widthCanvas, float posX, float posY, String id_device) {
        this.id = id;
        this.isMain = isMain;
        this.heightCanvas = heightCanvas;
        this.widthCanvas = widthCanvas;
        this.posX = posX;
        this.posY = posY;
        this.id_device = id_device;
    }

    public Canva() {}

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public float getHeightCanvas() {
        return heightCanvas;
    }

    public void setHeightCanvas(float heightCanvas) {
        this.heightCanvas = heightCanvas;
    }

    public float getWidthCanvas() {
        return widthCanvas;
    }

    public void setWidthCanvas(float widthCanvas) {
        this.widthCanvas = widthCanvas;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public String getId_device() {
        return id_device;
    }

    public void setId_device(String id_device) {
        this.id_device = id_device;
    }
}
