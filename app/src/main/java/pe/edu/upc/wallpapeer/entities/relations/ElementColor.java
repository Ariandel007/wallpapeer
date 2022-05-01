package pe.edu.upc.wallpapeer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import pe.edu.upc.wallpapeer.entities.Color;
import pe.edu.upc.wallpapeer.entities.Element;

public class ElementColor {
    @Embedded
    public Element element;
    @Relation(
            parentColumn = "id",
            entityColumn = "id_element"
    )
    public Color color;
}
