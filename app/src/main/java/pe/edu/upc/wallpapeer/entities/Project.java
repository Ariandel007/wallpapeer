package pe.edu.upc.wallpapeer.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "project")
public class Project {

    @PrimaryKey
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "date_creation")
    public Date dateCreation;

    @ColumnInfo(name = "height")
    public float height;

    @ColumnInfo(name = "width")
    public float width;


}
