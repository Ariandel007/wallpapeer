package pe.edu.upc.wallpapeer.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pe.edu.upc.wallpapeer.model.figures.Circle;

public class CanvasView  extends View {

    private Paint mPaint;
    private int backgroundFill;
    private List<Circle> circleList; // = Collections.emptyList();
    float posX = 50;
    float posY = 50;
    private List<Paint> paintList;
    private Circle mCircle;

    ///para el evento ontouch
    private Path mPath;

    public CanvasView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        paintList = new ArrayList<>();
        circleList = new ArrayList<>();
        //mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //mPaint.setColor(backgroundFill);
        //canvas.drawPaint(mPaint);

        int i = 0;
        for(Circle circle : getCircleList()){
            canvas.drawCircle(circle.getX(),circle.getY(),30, paintList.get(i));
        }
        //mPaint.setColor(Color.BLUE);
        /*for(Circle model : circleList) {
            canvas.drawCircle(model.getX(), model.getY(), 100, mPaint);
            if (accion =="down"){
                mPath.moveTo(iniTouchX,iniTouchY);
                model.setX(iniTouchX);
                model.setY(iniTouchY);
                //setCircleList(Arrays.asList(model));
                canvas.drawCircle(model.getX(), model.getY(), 100, mPaint);
                canvas.drawPath(mPath,mPaint);
            }
        }*/
        /// para el evento ontouch
        /*if (accion =="down"){
            mPath.moveTo(iniTouchX,iniTouchY);
            canvas.drawCircle(iniTouchX, iniTouchY, 100, mPaint);
        }
        if (accion == "move") {
            mPath.moveTo(iniTouchX, iniTouchY);
            canvas.drawCircle(iniTouchX, iniTouchY, 100, mPaint);
        }
        if (accion == "up")
            mPath.moveTo(iniTouchX, iniTouchY);
            canvas.drawCircle(iniTouchX, iniTouchY, 100, mPaint);*/

        //canvas.drawPath(mPath,mPaint);
        //canvas.restore();
    }

    public void setBackgroundFill(@ColorInt int backgroundFill){
        this.backgroundFill = backgroundFill;
    }
    public List<Circle> getCircleList(){
        return circleList;
    }
    public void setCircleList(List<Circle> circles){
        this.circleList = circles;
    }

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
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.BLUE);
                paintList.add(mPaint);
                mCircle = new Circle(posX,posY);
                mPath = new Path();
                mPath.moveTo(posX,posY);
                circleList.add(mCircle);
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
        invalidate();
        return true;
    }
}

