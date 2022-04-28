package pe.edu.upc.wallpapeer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import pe.edu.upc.wallpapeer.entities.Filter;
import pe.edu.upc.wallpapeer.entities.Image;

public class ImageFilter {
    @Embedded
    public Image image;
    @Relation(
            parentColumn = "id",
            entityColumn = "id_image"
    )
    public Filter filter;
}
