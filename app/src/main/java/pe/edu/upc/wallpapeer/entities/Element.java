package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Element {

    @PrimaryKey
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
    public Date dateCreation;

    public String id_project;

}
