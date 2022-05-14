package pe.edu.upc.wallpapeer.views.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ScaleGestureDetectorCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.dtos.NewElementInserted;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.model.figures.Circle;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.MyLastPinch;
import pe.edu.upc.wallpapeer.utils.PaletteOption;
import pe.edu.upc.wallpapeer.utils.PaletteState;
import pe.edu.upc.wallpapeer.viewmodels.ConnectionPeerToPeerViewModel;

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
    private ScaleGestureDetector mScaleDetector;
    private ConnectionPeerToPeerViewModel model;

    public boolean isPinchLocked = true;

    ///para el evento ontouch
    private Path mPath;

    public CanvasView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        paintList = new ArrayList<>();
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//        circleList = new ArrayList<>();
        //mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        for(Element element : getElementListCanvas()){
            float posXelement;
            float posYelement;
            if(element.getTypeElement().equals("circle_figure")) {
                posYelement = element.getPosyElement() - currentCanvaEntity.getPosY();
                posXelement = element.getPosxElement() - currentCanvaEntity.getPosX();
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                if(element.getColor() == null) {
                    mPaint.setColor(Color.BLUE);
                } else {
                    mPaint.setColor(Integer.parseInt(element.getColor()));
                }
                if(element.getRotation() != 0) {
                    canvas.save();
                    canvas.rotate(element.getRotation(), posXelement, posYelement);
                }
                canvas.drawCircle(posXelement,posYelement,element.getWidthElement(), mPaint);
                if(element.getRotation() != 0) {
                    canvas.restore();
                }
            }

            if(element.getTypeElement().equals("square_figure")) {
                posYelement = element.getPosyElement() - currentCanvaEntity.getPosY();
                posXelement = element.getPosxElement() - currentCanvaEntity.getPosX();
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                if(element.getColor() == null) {
                    mPaint.setColor(Color.BLUE);
                } else {
                    mPaint.setColor(Integer.parseInt(element.getColor()));
                }
                if(element.getRotation() != 0) {
                    canvas.save();
                    canvas.rotate(element.getRotation(), posXelement, posYelement);
                }
                drawSquare(posXelement, posYelement, element.getWidthElement(), element.getHeightElement(),canvas, mPaint);
                if(element.getRotation() != 0) {
                    canvas.restore();
                }
            }

            if(element.getTypeElement().equals("triangle_figure")) {
                posYelement = element.getPosyElement() - currentCanvaEntity.getPosY();
                posXelement = element.getPosxElement() - currentCanvaEntity.getPosX();
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                if(element.getColor() == null) {
                    mPaint.setColor(Color.BLUE);
                } else {
                    mPaint.setColor(Integer.parseInt(element.getColor()));
                }
                if(element.getRotation() != 0) {
                    canvas.save();
                    canvas.rotate(element.getRotation(), posXelement, posYelement);
                }
                drawTriangle(posXelement, posYelement, element.getWidthElement(), canvas, mPaint);
                if(element.getRotation() != 0) {
                    canvas.restore();
                }
            }
        }

    }

    public void drawSquare(float x, float y, float width, float heigth, Canvas canvas, Paint mPaint) {
        double squareSideHalf = 1 / Math.sqrt(2);
        Rect rectangle = new Rect((int) (x - (squareSideHalf * width)), (int) (y - (squareSideHalf * heigth)), (int) (x + (squareSideHalf * width)), (int) (y + ((squareSideHalf * heigth))));
        canvas.drawRect(rectangle, mPaint);
    }

    public void drawTriangle(float x, float y, float width, Canvas canvas, Paint mPaint) {
        int halfWidth = (int) (width / 2);
        Path path = new Path();
        path.moveTo(x, y - halfWidth); // Top
        path.lineTo(x - halfWidth, y + halfWidth); // Bottom left
        path.lineTo(x + halfWidth, y + halfWidth); // Bottom right
        path.lineTo(x, y - halfWidth); // Back to Top
        path.close();
        canvas.drawPath(path, mPaint);
    }

    public void setBackgroundFill(@ColorInt int backgroundFill){
        this.backgroundFill = backgroundFill;
    }

    public boolean checkIfPointIsInside(int x1, int y1, int x2, int y2, int x, int y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    public List<Element> filterElementsInsidePoint(float posX, float posY, List<Element> allElements) {
        List<Element> fileteredElements =new ArrayList<>();
        for (Element element : allElements) {
            int yPosEle = (int) (element.getPosyElement()) - (int) element.getHeightElement()/2;
            int xPosEle = (int) (element.getPosxElement()) - (int) element.getWidthElement()/2;
            int yPosEleFinal = (int) (element.getPosyElement()) + (int) element.getHeightElement()/2;
            int xPosEleFinal = (int) (element.getPosxElement()) + (int) element.getWidthElement()/2;
            if(checkIfPointIsInside(xPosEle, yPosEle, xPosEleFinal, yPosEleFinal,(int) posX, (int)posY)) {
                fileteredElements.add(element);
            }
        }
        return fileteredElements;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
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

    public ConnectionPeerToPeerViewModel getModel() {
        return model;
    }

    public void setModel(ConnectionPeerToPeerViewModel model) {
        this.model = model;
    }

    private void sendCoordsToPinch(float xDiff, float yDiff, MotionEvent e2, int threshoold, float velocityX, float velocityY, int velocity_threshold) {
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
        } else {
            if(Math.abs(yDiff) > threshoold && Math.abs(velocityY) > velocity_threshold) {
                if(yDiff>0){
                    //DOWN
                    MyLastPinch.getInstance().setPinchX(e2.getX());
                    MyLastPinch.getInstance().setPinchY((float) getHeigthDevice());
                    MyLastPinch.getInstance().setDate(new Date());
                    MyLastPinch.getInstance().setDirection("DOWN");

                } else {
                    //UP
                    MyLastPinch.getInstance().setPinchX(e2.getX());
                    MyLastPinch.getInstance().setPinchY(0.0f);
                    MyLastPinch.getInstance().setDate(new Date());
                    MyLastPinch.getInstance().setDirection("UP");

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

    List<Element> listFiltered;
    Element newElement;

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @SuppressLint("CheckResult")
        @Override
        public boolean onDown(MotionEvent event) {
            if(!isPinchLocked) {
                return true;
            }
            listFiltered = new ArrayList<>();
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            posX = event.getX() + getCurrentCanvaEntity().getPosX();
            posY = event.getY() + getCurrentCanvaEntity().getPosY();
            listFiltered = filterElementsInsidePoint(posX, posY, elementListCanvas);

            Log.i("Size listFiltered", String.valueOf(listFiltered.size()));

            newElement = null;
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    if(listFiltered.size() == 0) {
                        if(PaletteOption.SHAPES_OPTION == PaletteState.getInstance().getSelectedOption()
                                && PaletteOption.SHAPES_OPTION_CIRCLE== PaletteState.getInstance().getSubOption()) {
                            newElement = new Element();
                            newElement.setId(UUID.randomUUID().toString());
                            newElement.setTypeElement("circle_figure");
                            newElement.setRotation(0);
                            newElement.setzIndex(0);
                            newElement.setHeightElement(200);
                            newElement.setWidthElement(200);
                            newElement.setPosxElement(posX);
                            newElement.setPosyElement(posY);
                            newElement.setColor(PaletteState.getInstance().getColor().toString());
                            newElement.setDateCreation(new Date());
                            newElement.setId_project(currentProjectEntity.id);
                        }

                        if(PaletteOption.SHAPES_OPTION == PaletteState.getInstance().getSelectedOption()
                                && PaletteOption.SHAPES_OPTION_SQUARE== PaletteState.getInstance().getSubOption()) {
                            newElement = new Element();
                            newElement.setId(UUID.randomUUID().toString());
                            newElement.setTypeElement("square_figure");
                            newElement.setRotation(0);
                            newElement.setzIndex(0);
                            newElement.setHeightElement(200);
                            newElement.setWidthElement(200);
                            newElement.setPosxElement(posX);
                            newElement.setPosyElement(posY);
                            newElement.setColor(PaletteState.getInstance().getColor().toString());
                            newElement.setDateCreation(new Date());
                            newElement.setId_project(currentProjectEntity.id);
                        }

                        if(PaletteOption.SHAPES_OPTION == PaletteState.getInstance().getSelectedOption()
                                && PaletteOption.SHAPES_OPTION_TRIANGLE== PaletteState.getInstance().getSubOption()) {
                            newElement = new Element();
                            newElement.setId(UUID.randomUUID().toString());
                            newElement.setTypeElement("triangle_figure");
                            newElement.setRotation(0);
                            newElement.setzIndex(0);
                            newElement.setHeightElement(200);
                            newElement.setWidthElement(200);
                            newElement.setPosxElement(posX);
                            newElement.setPosyElement(posY);
                            newElement.setColor(PaletteState.getInstance().getColor().toString());
                            newElement.setDateCreation(new Date());
                            newElement.setId_project(currentProjectEntity.id);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                    /*respecto a un area*/
                case MotionEvent.ACTION_UP:
                    break;
            }
            //ACA SE GUARDA, ENVIA y LLAMA AL OBSERVABLE PARA PINTAR EL ELEMENTO
            if(newElement != null) {
                ///Informamos a los dispositivos el cambio
                getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement)));
                AppDatabase.getInstance().elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                    Log.i("Se creo","Se creo con exito");
                }, throwable -> {
                    Log.e("Error","Error al crear");
                });
            }
            //Se termino de crear
            return true;
        }

        @SuppressLint("CheckResult")
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
//            return super.onDoubleTapEvent(e);
            int move = e.getAction();

            if(PaletteOption.ROTATE == PaletteState.getInstance().getSelectedOption() && move == 1) {
                if(listFiltered.size() > 0) {
                    Element element = listFiltered.get(listFiltered.size() - 1);
                    float newRotation = element.getRotation() + 90;
                    if(newRotation == 360) {
                        newRotation = 0;
                    }
                    element.setRotation(newRotation);
                    getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, element)));
                    AppDatabase.getInstance().elementDAO().insert(element).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                        Log.i("Se creo","Se creo con exito");
                    }, throwable -> {
                        Log.e("Error","Error al crear");
                    });
                }
            }
            if(PaletteOption.LAYERS == PaletteState.getInstance().getSelectedOption() && move == 1){
                if(listFiltered.size() > 0){
                    Element element = listFiltered.get(listFiltered.size() - 1);
                    if(getElementListCanvas().size() > 0){
                        switch (PaletteState.getInstance().getSubOption()) {
                            case PaletteOption.LAYERS_BRING_TO_FRONT:
                                element.setzIndex(getElementListCanvas().get(getElementListCanvas().size() - 1).getzIndex() + 1);
                                break;
                            case PaletteOption.LAYERS_SEND_BACK:
                                element.setzIndex(element.getzIndex() - 1);
                                break;
                            case PaletteOption.LAYERS_BRING_FORWARD:
                                element.setzIndex(element.getzIndex() + 1);
                                break;
                            case PaletteOption.LAYERS_SEND_TO_THE_BACK:
                                element.setzIndex(getElementListCanvas().get(0).getzIndex() - 1);
                                break;
                        }
                        getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, element)));
                        AppDatabase.getInstance().elementDAO().insert(element).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Log.i("Se creo","Se creo con exito");
                        }, throwable -> {
                            Log.e("Error","Error al crear");
                        });
                    }
                }
            }
            Log.e("ex", " " + e.getX());
            Log.e("ey"," " + e.getY());
            return true;
        }

        @SuppressLint("CheckResult")
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            float xDiff = e2.getX() - e1.getX();
            float yDiff = e2.getY() - e1.getY();

            try{
                if(!isPinchLocked) {
                    sendCoordsToPinch(xDiff, yDiff, e2, 100, velocityX, velocityY, 100);
                } else {

                    float xMoved = e2.getX() + getCurrentCanvaEntity().getPosX();
                    float yMoved = e2.getY() + getCurrentCanvaEntity().getPosY();
                    if (listFiltered.size() > 0){
                        newElement = listFiltered.get(listFiltered.size()-1);
                        newElement.setPosxElement(xMoved);
                        newElement.setPosyElement(yMoved);

                        if(newElement != null) {
                            ///Informamos a los dispositivos el cambio
                            getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement)));
                            AppDatabase.getInstance().elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                Log.i("Se creo","Se creo con exito");
                            }, throwable -> {
                                Log.e("Error","Error al crear");
                            });
                        }
                    }
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
//            return false;

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float lastSpanX;
        private float lastSpanY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            lastSpanX = scaleGestureDetector.getCurrentSpanX();
            lastSpanY = scaleGestureDetector.getCurrentSpanY();
            return true;
        }

        @SuppressLint("CheckResult")
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//            float move = scaleGestureDetector.getCurrentSpan();

            float spanX = scaleGestureDetector.getCurrentSpanX();
            float spanY = scaleGestureDetector.getCurrentSpanY();

            float pastSpanX = scaleGestureDetector.getPreviousSpanX();
            float pastSpanY = scaleGestureDetector.getPreviousSpanY();

            if(pastSpanX == spanX) {
                return true;
            }

            if(spanY == pastSpanY) {
                return true;
            }


            float diffx = spanX - lastSpanX;
            float diffy = spanY - lastSpanY;

            float absX = Math.abs(diffx);
            float absY = Math.abs(diffy);

            float max = Math.max(absX, absY);
            if(max < 30) {
                return true;
            }

            if(max > 400) {
                return true;
            }

            if (listFiltered.size() > 0){
                newElement = listFiltered.get(listFiltered.size()-1);
                if(absX>absY) {
                    if(absX == 0 || absX == 0.0) {
                        return true;
                    }
                    Log.e("Expansion horizontal", "Expansion horizontal");
                    int plus = (int) (10*diffx/absX);
                    if(newElement.getTypeElement().equals("circle_figure")) {
                        newElement.setWidthElement(newElement.getWidthElement() + plus);
                        newElement.setHeightElement(newElement.getHeightElement() + plus);
                    }
                    if(newElement.getTypeElement().equals("square_figure")) {
                        newElement.setWidthElement(newElement.getWidthElement() + plus);
                    }
                    if(newElement.getTypeElement().equals("triangle_figure")) {
                        newElement.setWidthElement(newElement.getWidthElement() + plus);
                        newElement.setHeightElement(newElement.getHeightElement() + plus);
                    }
                } else {
                    if(absY == 0 || absY == 0.0) {
                        return true;
                    }
                    Log.e("Expansion vertical", "Expansion vertical");
                    int plus = (int) (10*diffy/absY);

                    if(newElement.getTypeElement().equals("circle_figure")) {
                        newElement.setWidthElement(newElement.getWidthElement() + plus);
                        newElement.setHeightElement(newElement.getHeightElement() + plus);
                    }
                    if(newElement.getTypeElement().equals("square_figure")) {
                        newElement.setHeightElement(newElement.getHeightElement() + plus);
                    }
                    if(newElement.getTypeElement().equals("triangle_figure")) {
                        newElement.setWidthElement(newElement.getWidthElement() + plus);
                        newElement.setHeightElement(newElement.getHeightElement() + plus);
                    }
                }

                if(newElement.getWidthElement() < 30 || newElement.getHeightElement() < 30) {
                    return true;
                }

                if(newElement.getWidthElement() > 200 || newElement.getHeightElement() > 200) {
                    return true;
                }

                if(newElement != null) {
                    ///Informamos a los dispositivos el cambio
                    getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement)));
                    AppDatabase.getInstance().elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                        Log.i("Se creo","Se creo con exito");
                    }, throwable -> {
                        Log.e("Error","Error al crear");
                    });
                }
            }
            return true;
        }

    }

}

