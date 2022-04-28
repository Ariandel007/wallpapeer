package pe.edu.upc.wallpapeer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import pe.edu.upc.wallpapeer.entities.Project;

@Dao
public interface ProjectDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Completable insert(Project project);

    @Query("SELECT * FROM project")
    Single<List<Project>> getAll();

    @Query("SELECT * FROM project WHERE id = :id")
    Single<Project> getProject(String id);
}
