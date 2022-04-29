package pe.edu.upc.wallpapeer.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableInt;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import pe.edu.upc.wallpapeer.model.figures.Circle;

public class MainCanvasViewModel  extends BaseObservable {

    public final ObservableInt backgroundFill = new ObservableInt();
    @Bindable
    private List<Circle> circleList = new ArrayList<>();

    public List<Circle> getCircleList(){
        return circleList;
    }

    public void setCircleList(List<Circle> circleList){
        this.circleList = circleList;
        notifyPropertyChanged(BR.circleList);
    }
}
