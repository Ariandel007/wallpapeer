package pe.edu.upc.wallpapeer.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pe.edu.upc.wallpapeer.entities.Project;

@Dao
public interface ProjectDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Project project);

    @Query("SELECT * FROM project")
    List<Project> getAll();

    @Query("SELECT * FROM project WHERE id = :id")
    Project getProject(String id);
}
