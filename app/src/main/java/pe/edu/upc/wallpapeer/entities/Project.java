package pe.edu.upc.wallpapeer.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import pe.edu.upc.wallpapeer.converters.DateConverter;

@Entity(tableName = "project")
public class Project {

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "name")
    public String name;


    @ColumnInfo(name = "date_creation")
    @TypeConverters(DateConverter.class)
    public Date dateCreation;

    @ColumnInfo(name = "height")
    public float height;

    @ColumnInfo(name = "width")
    public float width;


}
