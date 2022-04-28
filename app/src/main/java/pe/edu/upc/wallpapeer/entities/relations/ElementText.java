package pe.edu.upc.wallpapeer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;


import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Text;

public class ElementText {
    @Embedded
    public Element element;
    @Relation(
            parentColumn = "id",
            entityColumn = "id_element"
    )
    public Text text;
}
