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

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public Project(@NonNull String id, String name, Date dateCreation, float height, float width) {
        this.id = id;
        this.name = name;
        this.dateCreation = dateCreation;
        this.height = height;
        this.width = width;
    }

    public Project() {
    }
}
