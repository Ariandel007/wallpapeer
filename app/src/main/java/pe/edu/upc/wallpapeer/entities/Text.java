package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "text",
        foreignKeys =
        @ForeignKey(
                entity = Element.class,
                parentColumns = "id",
                childColumns = "id_element",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ))
public class Text {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "size")
    public int size;

    @ColumnInfo(name = "text")
    public String textContent;

    public String id_element;
}
