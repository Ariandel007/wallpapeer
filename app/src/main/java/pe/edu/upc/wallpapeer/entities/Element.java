package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pe.edu.upc.wallpapeer.converters.DateConverter;

@Entity(tableName = "element",
        foreignKeys =
        @ForeignKey(
                entity = Project.class,
                parentColumns = "id",
                childColumns = "id_project",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ))
public class Element {

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "type_element")
    public String typeElement;

    @ColumnInfo(name = "width_element")
    public float widthElement;

    @ColumnInfo(name = "height_element")
    public float heightElement;

    @ColumnInfo(name = "posx_element")
    public float posxElement;

    @ColumnInfo(name = "posy_element")
    public float posyElement;

    @ColumnInfo(name = "z_index")
    public int zIndex;

    @ColumnInfo(name = "opacity")
    public float opacity;

    @ColumnInfo(name = "rotation")
    public float rotation;

    @ColumnInfo(name = "date_creation")
    @TypeConverters(DateConverter.class)
    public Date dateCreation;

    public String id_project;

}
