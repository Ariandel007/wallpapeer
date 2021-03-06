package pe.edu.upc.wallpapeer.views.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;



import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ScaleGestureDetectorCompat;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pe.edu.upc.wallpapeer.R;
import pe.edu.upc.wallpapeer.dtos.EngagePinchEvent;
import pe.edu.upc.wallpapeer.dtos.NewElementInserted;
import pe.edu.upc.wallpapeer.entities.Canva;
import pe.edu.upc.wallpapeer.entities.Element;
import pe.edu.upc.wallpapeer.entities.Palette;
import pe.edu.upc.wallpapeer.entities.Project;
import pe.edu.upc.wallpapeer.model.figures.Circle;
import pe.edu.upc.wallpapeer.utils.AppDatabase;
import pe.edu.upc.wallpapeer.utils.CodeEvent;
import pe.edu.upc.wallpapeer.utils.GalleryMap;
import pe.edu.upc.wallpapeer.utils.JsonConverter;
import pe.edu.upc.wallpapeer.utils.LastProjectState;
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
    //private List<Path> pathTrazos;

    public boolean isPinchLocked = true;

    ///para el evento ontouch
    private Path mPath;

    public CanvasView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        paintList = new ArrayList<>();

        //pathTrazos = new ArrayList<>();

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

            //------
            if(element.getTypeElement().equals("trace")){
                posYelement = element.getPosyElement() - currentCanvaEntity.getPosY();
                posXelement = element.getPosxElement() - currentCanvaEntity.getPosX();
                float posYelement2 = element.getPosyElement2() - currentCanvaEntity.getPosY();
                float posXelement2 = element.getPosxElement2() - currentCanvaEntity.getPosX();
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
                //drawPath(posXelement, posYelement, canvas, mPaint);
                canvas.drawLine(posXelement, posYelement,posXelement2, posYelement2, mPaint );
                //canvas.drawCircle(posXelement,posYelement,element.getWidthElement(), mPaint);
                if(element.getRotation() != 0) {
                    canvas.restore();
                }
            }
            if ( element.getTypeElement().equals("text") ) { //texto
                posYelement = element.getPosyElement() - currentCanvaEntity.getPosY();
                posXelement = element.getPosxElement() - currentCanvaEntity.getPosX();
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                if(element.getColor() == null) {
                    mPaint.setColor(Color.BLUE);
                } else {
                    mPaint.setColor(Integer.parseInt(element.getColor()));
                }
                mPaint.setTextSize(element.heightElement);
                if(element.getRotation() != 0) {
                    canvas.save();
                    canvas.rotate(element.getRotation(), posXelement, posYelement);
                }
                drawText(posXelement, posYelement, element.getText(), canvas, mPaint);

                //canvas.drawCircle(posXelement,posYelement,element.getWidthElement(), mPaint);
                if(element.getRotation() != 0) {
                    canvas.restore();
                }
            }
            //-------
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
            if(element.getTypeElement().equals("image")) {
                posYelement = element.getPosyElement() - currentCanvaEntity.getPosY();
                posXelement = element.getPosxElement() - currentCanvaEntity.getPosX();
                /*
                mPaint = new Paint();
                mPaint.setStyle(Paint.Style.FILL);
                if(element.getColor() == null) {
                    mPaint.setColor(Color.BLUE);
                } else {
                    mPaint.setColor(Integer.parseInt(element.getColor()));
                }
                */
                mPaint = new Paint();
//                if(element.getRotation() != 0) {
//                    canvas.save();
//                    canvas.rotate(element.getRotation(), posXelement, posYelement);
//                }
                //drawTriangle(posXelement, posYelement, element.getWidthElement(), canvas, mPaint);
                int drawableResourceId = canvasContext.getResources().getIdentifier(element.getSource(), "drawable", canvasContext.getPackageName());
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res, drawableResourceId);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) element.getWidthElement(), (int) element.getHeightElement(), true);
                if(element.getRotation() != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(element.getRotation());
                    Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);
                    if(element.getFilter() == 0) {
                        canvas.drawBitmap(rotatedBitmap, posXelement, posYelement, mPaint);
                    } else if (element.getFilter() == 1){
                        Bitmap bwBitmap = toGrayscale(resizedBitmap);
                        canvas.drawBitmap(bwBitmap, posXelement, posYelement, mPaint);
                    } else if (element.getFilter() == 2){
                        Bitmap sepiaBitmap = toSepiaNice(rotatedBitmap);
                        canvas.drawBitmap(sepiaBitmap, posXelement, posYelement, mPaint);
                    }
                } else {
                    if(element.getFilter() == 0) {
                        canvas.drawBitmap(resizedBitmap, posXelement, posYelement, mPaint);
                    } else if (element.getFilter() == 1){
                        Bitmap bwBitmap = toGrayscale(resizedBitmap);
                        canvas.drawBitmap(bwBitmap, posXelement, posYelement, mPaint);
                    } else if (element.getFilter() == 2){
                        Bitmap sepiaBitmap = toSepiaNice(resizedBitmap);
                        canvas.drawBitmap(sepiaBitmap, posXelement, posYelement, mPaint);
                    }
                }
                /*canvas.drawBitmap(bitmap,
                        new Rect( (int) posXelement, (int) posYelement, (int) element.getWidthElement(), (int) element.getHeightElement()),
                        new RectF( (int) posXelement, (int) posYelement, (int) element.getWidthElement(), (int) element.getHeightElement()),
                        new Paint());

                 */
//                if(element.getRotation() != 0) {
//                    canvas.restore();
//                }

            }
        }

    }
    //---
    public void drawPath(float x, float y, Canvas canvas, Paint mPaint){
        Path path = new Path();
        path.moveTo(x,y);
        canvas.drawPath(path,mPaint);
    }
    public void drawText (float x, float y, String text , Canvas canvas, Paint mPaint){
        //Path path = new Path();
        canvas.drawText(text, x, y, mPaint);
    }
    //---
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
            if(element.getTypeElement().equals("image")){
                int yPosEle = (int) (element.getPosyElement());
                int xPosEle = (int) (element.getPosxElement());
                int yPosEleFinal = (int) (element.getPosyElement()) + (int) element.getHeightElement();
                int xPosEleFinal = (int) (element.getPosxElement()) + (int) element.getWidthElement();
                if(checkIfPointIsInside(xPosEle, yPosEle, xPosEleFinal, yPosEleFinal,(int) posX, (int)posY)) {
                    fileteredElements.add(element);
                }
            } else {
                int yPosEle = (int) (element.getPosyElement()) - (int) element.getHeightElement()/2;
                int xPosEle = (int) (element.getPosxElement()) - (int) element.getWidthElement()/2;
                int yPosEleFinal = (int) (element.getPosyElement()) + (int) element.getHeightElement()/2;
                int xPosEleFinal = (int) (element.getPosxElement()) + (int) element.getWidthElement()/2;
                if(checkIfPointIsInside(xPosEle, yPosEle, xPosEleFinal, yPosEleFinal,(int) posX, (int)posY)) {
                    fileteredElements.add(element);
                }
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
    final int[] values = getResources().getIntArray(R.array.images_array);

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

                        if(PaletteState.getInstance().getSelectedOption() == 3){    // texto
                            newElement = new Element();
                            newElement.setId(UUID.randomUUID().toString());
                            //newElement.setTypeElement("trace");
                            newElement.setTypeElement("text");
                            newElement.setText(PaletteState.getInstance().getTextToPrint());
                            newElement.setRotation(0);
                            newElement.setzIndex(0);
                            newElement.setHeightElement(60);
                            newElement.setWidthElement(60);
                            newElement.setPosxElement(posX);
                            newElement.setPosyElement(posY);
                            newElement.setColor(PaletteState.getInstance().getColor().toString());
                            newElement.setDateCreation(new Date());
                            newElement.setId_project(currentProjectEntity.id);
                        }
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
                        if(PaletteOption.IMAGE == PaletteState.getInstance().getSelectedOption()) {
                            newElement = new Element();
                            newElement.setId(UUID.randomUUID().toString());
                            newElement.setTypeElement("image");
                            newElement.setRotation(0);
                            newElement.setzIndex(0);
                            newElement.setHeightElement(400);
                            newElement.setWidthElement(400);
                            newElement.setPosxElement(posX);
                            newElement.setPosyElement(posY);
                            //newElement.setColor(String.valueOf(-0));
                            newElement.setDateCreation(new Date());
                            newElement.setId_project(currentProjectEntity.id);
                            newElement.setSource(GalleryMap.getInstance().getGalleryHashSet().get(PaletteState.getInstance().getSubOption()));
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
                getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement, LastProjectState.getInstance().getDeviceName())));
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
                    getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, element, LastProjectState.getInstance().getDeviceName())));
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
                        getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, element, LastProjectState.getInstance().getDeviceName())));
                        AppDatabase.getInstance().elementDAO().insert(element).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Log.i("Se creo","Se creo con exito");
                        }, throwable -> {
                            Log.e("Error","Error al crear");
                        });
                    }
                }
            }
            if(PaletteOption.FILTER == PaletteState.getInstance().getSelectedOption() && move == 1){
                if(listFiltered.size() > 0){
                    Element element = listFiltered.get(listFiltered.size() - 1);
                    if(getElementListCanvas().size() > 0){
                        switch (PaletteState.getInstance().getSubOption()) {
                            case PaletteOption.FILTER_GRAY_SCALE:
                                element.setFilter(1);
                                break;
                            case PaletteOption.FILTER_SEPIA:
                                element.setFilter(2);
                                break;
                        }
                        getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, element, LastProjectState.getInstance().getDeviceName())));
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
                        if(newElement.getTypeElement().equals("image")) {
                            newElement.setPosxElement(xMoved-newElement.getWidthElement()/2);
                            newElement.setPosyElement(yMoved-newElement.getHeightElement()/2);
                        } else {
                            newElement.setPosxElement(xMoved);
                            newElement.setPosyElement(yMoved);
                        }

                        if(newElement != null) {
                            ///Informamos a los dispositivos el cambio
                            getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement, LastProjectState.getInstance().getDeviceName())));
                            AppDatabase.getInstance().elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                Log.i("Se creo","Se creo con exito");
                            }, throwable -> {
                                Log.e("Error","Error al crear");
                            });
                        }
                    }
                    else{
                        if(PaletteState.getInstance().getSelectedOption() == 0){    // trazo
                            newElement = new Element();
                            newElement.setId(UUID.randomUUID().toString());
                            newElement.setTypeElement("trace");
                            newElement.setRotation(0);
                            newElement.setzIndex(0);
                            newElement.setHeightElement(200);
                            newElement.setWidthElement(200);
                            newElement.setPosxElement(posX);
                            newElement.setPosyElement(posY);
                            newElement.setPosxElement2(xMoved);
                            newElement.setPosyElement2(yMoved);
                            newElement.setColor(PaletteState.getInstance().getColor().toString());
                            newElement.setDateCreation(new Date());
                            newElement.setId_project(currentProjectEntity.id);
                            if(newElement != null) {
                                ///Informamos a los dispositivos el cambio
                                getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement, LastProjectState.getInstance().getDeviceName())));
                                AppDatabase.getInstance().elementDAO().insert(newElement).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                            Log.i("Se creo","Se creo con exito");
                                        }, throwable -> {
                                            Log.e("Error","Error al crear");
                                        });
                            }
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

            if(listFiltered == null) {
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
                    if(newElement.getTypeElement().equals("image")) {
                        newElement.setWidthElement(newElement.getWidthElement() + plus);
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
                    if(newElement.getTypeElement().equals("image")) {
                        newElement.setHeightElement(newElement.getHeightElement() + plus);
                    }
                }

                /*if(newElement.getWidthElement() < 30 || newElement.getHeightElement() < 30) {
                    return true;
                }

                if(newElement.getWidthElement() > 200 || newElement.getHeightElement() > 200) {
                    return true;
                }*/

                if(newElement != null) {
                    ///Informamos a los dispositivos el cambio
                    getModel().sendMessage(JsonConverter.getGson().toJson(new NewElementInserted(CodeEvent.INSERT_NEW_ELEMENT, newElement, LastProjectState.getInstance().getDeviceName())));
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

    public Bitmap toSepiaNice(Bitmap color) {
        int red, green, blue, pixel, gry;
        int height = color.getHeight();
        int width = color.getWidth();
        int depth = 20;

        Bitmap sepia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[width * height];
        color.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i++) {
            pixel = pixels[i];

            red = (pixel >> 16) & 0xFF;
            green = (pixel >> 8) & 0xFF;
            blue = pixel & 0xFF;

            red = green = blue = (red + green + blue) / 3;

            red += (depth * 2);
            green += depth;

            if (red > 255)
                red = 255;
            if (green > 255)
                green = 255;
            pixels[i] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
        }
        sepia.setPixels(pixels, 0, width, 0, 0, width, height);
        return sepia;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

}

