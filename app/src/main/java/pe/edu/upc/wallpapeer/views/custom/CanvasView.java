package pe.edu.upc.wallpapeer.views.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.model.figures.Circle;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.MyLastPinch;

public class CanvasView  extends View {

    private Paint mPaint;
    private int backgroundFill;
    float posX = 50;
    float posY = 50;
    private List<Paint> paintList;
    private  List<Element> elementListCanvas;
    private  Context canvasContext;
    private Project currentProjectEntity;
    private Canva currentCanvaEntity;
    private GestureDetectorCompat mDetector;

    ///para el evento ontouch
    private Path mPath;

    public CanvasView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        paintList = new ArrayList<>();
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
//        circleList = new ArrayList<>();
        //mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for(Element element : getElementListCanvas()){
            if(element.getTypeElement().equals("circle_figure")) {
                element.setPosyElement(element.getPosyElement() - currentCanvaEntity.getPosY());
                element.setPosxElement(element.getPosxElement() - currentCanvaEntity.getPosX());
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

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
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


    private void sendCoordsToPinch(float xDiff, float yDiff, MotionEvent e2,int threshoold, float velocityX, float velocityY, int velocity_threshold) {
        if(Math.abs(xDiff) > Math.abs(yDiff)){
            if(Math.abs(xDiff) > threshoold && Math.abs(velocityX) >velocity_threshold){
                if(xDiff>0){
                    MyLastPinch.getInstance().setPinchX((float) getWidthDevice());
                    MyLastPinch.getInstance().setPinchY(e2.getY());
                    MyLastPinch.getInstance().setDate(new Date());
                    MyLastPinch.getInstance().setDirection("RIGHT");
                } else {
                    MyLastPinch.getInstance().setPinchX(0.0f);
                    MyLastPinch.getInstance().setPinchY(e2.getY());
                    MyLastPinch.getInstance().setDate(new Date());
                    MyLastPinch.getInstance().setDirection("LEFT");
                }
            }
        }
    }

    private int getHeigthDevice() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private int getWidthDevice() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            posX = event.getX() + getCurrentCanvaEntity().getPosX();
            posY = event.getY() + getCurrentCanvaEntity().getPosY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
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
                newElement.setDateCreation(new Date());
                newElement.setId_project(currentProjectEntity.id);
                AppDatabase.getInstance().elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                    Log.i("Se creo","Se creo con exito");
                }, throwable -> {
                    Log.e("Error","Error al crear");
                });
                //SET PATH
                mPath = new Path();
                mPath.moveTo(posX,posY);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
                /*respecto a un area*/
            case MotionEvent.ACTION_UP:
                break;
        }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            float xDiff = e2.getX() - e1.getX();
            float yDiff = e2.getY() - e1.getY();

            try{
                if(true) {
                    sendCoordsToPinch(xDiff, yDiff, e2, 100, velocityX, velocityY, 100);
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;

//            return true;
        }
    }

}

