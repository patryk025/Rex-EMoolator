package pl.genschu.bloomooemulator.objects;

public class Rectangle {
    private int xLeft;
    private int yBottom;
    private int xRight;
    private int yTop;

    public Rectangle(int xLeft, int yBottom, int xRight, int yTop) {
        this.xLeft = xLeft;
        this.yBottom = yBottom;
        this.xRight = xRight;
        this.yTop = yTop;
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
    }

    public void setYBottom(int yBottom) {
        this.yBottom = yBottom;
    }

    public void setXRight(int xRight) {
        this.xRight = xRight;
    }

    public void setYTop(int yTop) {
        this.yTop = yTop;
    }

    public boolean contains(int x, int y) {
        return x >= xLeft && x <= xRight && y <= yBottom && y >= yTop;
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
}
