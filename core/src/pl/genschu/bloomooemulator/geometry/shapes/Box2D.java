package pl.genschu.bloomooemulator.geometry.shapes;

public class Box2D {
    // TODO: refactor like in Box3D class (it will be very much classes to refactor, whole rendering, collisions, buttons, etc.)
    private int xLeft;
    private int yBottom;
    private int xRight;
    private int yTop;

    private int width;
    private int height;

    public Box2D(int xLeft, int yBottom, int xRight, int yTop) {
        this.xLeft = xLeft;
        this.yBottom = yBottom;
        this.xRight = xRight;
        this.yTop = yTop;
        this.width = xRight - xLeft;
        this.height = yTop - yBottom;
    }

    public int getXLeft() {
        return xLeft;
    }

    public int getYBottom() {
        return yBottom;
    }

    public int getXRight() {
        return xRight;
    }

    public int getYTop() {
        return yTop;
    }

    public void setXLeft(int xLeft) {
        this.xLeft = xLeft;
        this.width = xRight - xLeft;
    }

    public void setYBottom(int yBottom) {
        this.yBottom = yBottom;
        this.height = yTop - yBottom;
    }

    public void setXRight(int xRight) {
        this.xRight = xRight;
        this.width = xRight - xLeft;
    }

    public void setYTop(int yTop) {
        this.yTop = yTop;
        this.height = yTop - yBottom;
    }

    public boolean contains(int x, int y) {
        return x >= xLeft && x <= xRight && y >= yBottom + getHeight() && y <= yTop + getHeight();
    }

    public boolean intersects(Box2D other) {
        return xLeft < other.getXRight() && xRight > other.getXLeft() && yBottom + getHeight() < other.getYTop() + other.getHeight() && yTop + getHeight() > other.getYBottom() + other.getHeight();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "xLeft=" + xLeft +
                ", yBottom=" + yBottom +
                ", xRight=" + xRight +
                ", yTop=" + yTop +
                '}';
    }

    public int area() {
        return width * height;
    }
}
