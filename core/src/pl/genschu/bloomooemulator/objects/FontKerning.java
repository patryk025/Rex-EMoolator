package pl.genschu.bloomooemulator.objects;

public class FontKerning {
    private int left;
    private int right;

    public FontKerning(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }
}
