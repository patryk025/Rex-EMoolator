package pl.genschu.bloomooemulator.interpreter.util;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }
    
    public void setX(double x) {
        this.x = (int) x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public void setY(double y) {
        this.y = (int) y;
    }
    
    @Override
    public String toString() {
        return "Point("+this.getX()+","+this.getY()+")";
    }
}
