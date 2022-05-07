package pe.edu.upc.wallpapeer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;


@Dao
public interface ElementDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Element element);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMany(List<Element> elements);


    @Query("SELECT * FROM element ")
    LiveData<List<Element>> getAllElementsLiveData();

    @Query("SELECT * FROM element WHERE id_project = :idProject")
    LiveData<List<Element>> getAllElementsLiveDataByProject(String idProject);


    @Query("SELECT * FROM element")
    Single<List<Element>> getAll();

    @Query("SELECT * FROM element WHERE id_project =:idProject")
    Single<List<Element>> getAllByProject(String idProject);

}
