package pe.edu.upc.wallpapeer.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import pe.edu.upc.wallpapeer.entities.Device;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;

public class ProjectElement {
    @Embedded
    public Project project;
    @Relation(
            parentColumn = "id",
            entityColumn = "id_project"
    )
    public Element element;
}
