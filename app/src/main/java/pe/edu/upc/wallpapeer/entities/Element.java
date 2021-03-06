package pe.edu.upc.wallpapeer.entities;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

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

    @ColumnInfo(name = "posx_element2")
    public float posxElement2;

    @ColumnInfo(name = "posy_element2")
    public float posyElement2;

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

    @ColumnInfo(name = "image_name")
    public String source;

    @ColumnInfo(name = "filter")
    public int filter;

    public Element(@NonNull String id, String typeElement, float widthElement, float heightElement, float posxElement, float posyElement, int zIndex, float opacity, float rotation, Date dateCreation, String id_project, String text, String color, String source, int filter) {
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
        this.source = source;
        this.filter = filter;
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

    public float getPosxElement2() {
        return posxElement2;
    }

    public void setPosxElement2(float posxElement2) {
        this.posxElement2 = posxElement2;
    }

    public float getPosyElement2() {
        return posyElement2;
    }

    public void setPosyElement2(float posyElement2) {
        this.posyElement2 = posyElement2;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }
}
