package pe.edu.upc.wallpapeer.model.figures;

public class Circle {
    private float x;
    private float y;

    public Circle(float x, float y){
        this.x = x;
        this.y =y;
    }

    public float getX(){
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
}
