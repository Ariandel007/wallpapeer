package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "image",
        foreignKeys =
        @ForeignKey(
                entity = Element.class,
                parentColumns = "id",
                childColumns = "id_element",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ))
public class Image {

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    public byte[] image;

    public String id_element;
}
