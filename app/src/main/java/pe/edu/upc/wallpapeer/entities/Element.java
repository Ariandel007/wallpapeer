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

    @ColumnInfo(name = "id_project")
    public String id_project;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "color")
    public String color;

    public Element(@NonNull String id, String typeElement, float widthElement, float heightElement, float posxElement, float posyElement, int zIndex, float opacity, float rotation, Date dateCreation, String id_project, String text, String color) {
        this.id = id;
        this.typeElement = typeElement;
        this.widthElement = widthElement;
        this.heightElement = heightElement;
        this.posxElement = posxElement;
        this.posyElement = posyElement;
        this.zIndex = zIndex;
        this.opacity = opacity;
        this.rotation = rotation;
        this.dateCreation = dateCreation;
        this.id_project = id_project;
        this.text = text;
        this.color = color;
    }

    public Element() {}

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }

    public float getWidthElement() {
        return widthElement;
    }

    public void setWidthElement(float widthElement) {
        this.widthElement = widthElement;
    }

    public float getHeightElement() {
        return heightElement;
    }

    public void setHeightElement(float heightElement) {
        this.heightElement = heightElement;
    }

    public float getPosxElement() {
        return posxElement;
    }

    public void setPosxElement(float posxElement) {
        this.posxElement = posxElement;
    }

    public float getPosyElement() {
        return posyElement;
    }

    public void setPosyElement(float posyElement) {
        this.posyElement = posyElement;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getId_project() {
        return id_project;
    }

    public void setId_project(String id_project) {
        this.id_project = id_project;
    }

    public String getText() {   return text; }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
