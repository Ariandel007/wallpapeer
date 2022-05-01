package pe.edu.upc.wallpapeer.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.model.figures.Circle;
import pe.edu.upc.wallpapeer.utils.AppDatabase;

public class CanvasView  extends View {

    private Paint mPaint;
    private int backgroundFill;
//    private List<Circle> circleList;
    float posX = 50;
    float posY = 50;
    private List<Paint> paintList;
    private Circle mCircle;
    private  List<Element> elementListCanvas;
    private  Context canvasContext;
    private Project currentProjectEntity;
    private Canva currentCanvaEntity;

    ///para el evento ontouch
    private Path mPath;

    public CanvasView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        paintList = new ArrayList<>();
//        circleList = new ArrayList<>();
        //mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for(Element element : getElementListCanvas()){
            if(element.getTypeElement().equals("circle_figure")) {
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.BLUE);
                canvas.drawCircle(element.getPosxElement(),element.getPosyElement(),element.getWidthElement(), mPaint);
            }

        }

    }

    public void setBackgroundFill(@ColorInt int backgroundFill){
        this.backgroundFill = backgroundFill;
    }
//    public List<Circle> getCircleList(){
//        return circleList;
//    }
//    public void setCircleList(List<Circle> circles){
//        this.circleList = circles;
//    }

    ///para el evento ontouch

    //String accion = "accion";

    @Override
    public boolean onTouchEvent(MotionEvent event){
        posX = event.getX();
        posY = event.getY();
        /*if (event.getAction() == MotionEvent.ACTION_DOWN){accion = "down"; }
        if (event.getAction() == MotionEvent.ACTION_MOVE){accion = "move"; }
        if(event.getAction() == MotionEvent.ACTION_UP){accion = "up"; }*/
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                mPaint = new Paint();
//                mPaint.setStyle(Paint.Style.FILL);
//                mPaint.setColor(Color.BLUE);
//                paintList.add(mPaint);
//                mCircle = new Circle(posX,posY);
//                mPath = new Path();
//                mPath.moveTo(posX,posY);
//                circleList.add(mCircle);
                //SAVE AN ELEMENT
                Element newElement = new Element();
                newElement.setId(UUID.randomUUID().toString());
                newElement.setTypeElement("circle_figure");
                newElement.setRotation(0);
                newElement.setzIndex(0);
                newElement.setHeightElement(30);
                newElement.setWidthElement(30);
                newElement.setPosxElement(posX);
                newElement.setPosyElement(posY);
                newElement.setId_project(currentProjectEntity.id);
                AppDatabase.getInstance(canvasContext).elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                    Log.e("Se creo","Se creo con exito");
                }, throwable -> {
                    Log.e("Error","Error al crear");
                });
                //SET PATH
                mPath = new Path();
                mPath.moveTo(posX,posY);
                break;
            case MotionEvent.ACTION_MOVE:
                /*respecto a un area*/
            case MotionEvent.ACTION_UP:
                /*int pHistorical = event.getHistorySize();
                for (int i=0; i < pHistorical; i++) {
                    mPath.moveTo(event.getHistoricalX(i),event.getHistoricalY(i));
                    circleList.add(mCircle);
                }
                break;*/
        }
        //on Draw se llamada tras cualquierda llamada a invalidate
//        invalidate();
        return true;
    }

    //on Draw se llamada tras cualquierda llamada a invalidate
    public void triggerOnDraw() {
        invalidate();
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public int getBackgroundFill() {
        return backgroundFill;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public List<Paint> getPaintList() {
        return paintList;
    }

    public void setPaintList(List<Paint> paintList) {
        this.paintList = paintList;
    }

    public Circle getmCircle() {
        return mCircle;
    }

    public void setmCircle(Circle mCircle) {
        this.mCircle = mCircle;
    }

    public List<Element> getElementListCanvas() {
        return elementListCanvas;
    }

    public void setElementListCanvas(List<Element> elementListCanvas) {
        this.elementListCanvas = elementListCanvas;
    }

    public Path getmPath() {
        return mPath;
    }

    public void setmPath(Path mPath) {
        this.mPath = mPath;
    }

    public Context getCanvasContext() {
        return canvasContext;
    }

    public void setCanvasContext(Context canvasContext) {
        this.canvasContext = canvasContext;
    }

    public Project getCurrentProjectEntity() {
        return currentProjectEntity;
    }

    public void setCurrentProjectEntity(Project currentProjectEntity) {
        this.currentProjectEntity = currentProjectEntity;
    }

    public Canva getCurrentCanvaEntity() {
        return currentCanvaEntity;
    }

    public void setCurrentCanvaEntity(Canva currentCanvaEntity) {
        this.currentCanvaEntity = currentCanvaEntity;
    }
}

