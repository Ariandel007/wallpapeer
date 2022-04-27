package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import pe.edu.upc.wallpapeer.entities.Project;

@Dao
public interface ProjectDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProject(Project project);
}
