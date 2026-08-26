package pl.genschu.bloomooemulator.geometry.coordinates;

import java.util.Optional;

/**
 * An immutable, normalized rectangle in script-visible DirectDraw canvas coordinates.
 *
 * <p>The edges follow the Win32 {@code RECT}/{@code PtInRect} convention: left and top
 * are inclusive, right and bottom are exclusive. Consequently, width is
 * {@code right - left} and height is {@code bottom - top} without any axis-dependent
 * compensation.</p>
 */
public record CanvasRect(int left, int top, int right, int bottom) {
    public CanvasRect {
        if (right < left || bottom < top) {
            throw new IllegalArgumentException(
                    "Canvas rectangle must be normalized: left=" + left
                            + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
        }
    }

    public static CanvasRect fromPositionAndSize(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                    "Canvas rectangle size cannot be negative: " + width + "x" + height);
        }
        return new CanvasRect(x, y, Math.addExact(x, width), Math.addExact(y, height));
    }

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }

    public boolean isEmpty() {
        return left == right || top == bottom;
    }

    public boolean contains(int x, int y) {
        return x >= left && x < right && y >= top && y < bottom;
    }

    public boolean contains(CanvasPoint point) {
        return point.x() >= left && point.x() < right
                && point.y() >= top && point.y() < bottom;
    }

    /** Returns whether every edge of {@code other} lies within this rectangle. */
    public boolean contains(CanvasRect other) {
        return other.left >= left && other.top >= top
                && other.right <= right && other.bottom <= bottom;
    }

    public boolean intersects(CanvasRect other) {
        return left < other.right && right > other.left
                && top < other.bottom && bottom > other.top;
    }

    public Optional<CanvasRect> intersection(CanvasRect other) {
        int intersectionLeft = Math.max(left, other.left);
        int intersectionTop = Math.max(top, other.top);
        int intersectionRight = Math.min(right, other.right);
        int intersectionBottom = Math.min(bottom, other.bottom);
        if (intersectionLeft >= intersectionRight || intersectionTop >= intersectionBottom) {
            return Optional.empty();
        }
        return Optional.of(new CanvasRect(
                intersectionLeft, intersectionTop, intersectionRight, intersectionBottom));
    }

    public long area() {
        return (long) width() * height();
    }

    public long intersectionArea(CanvasRect other) {
        return intersection(other).map(CanvasRect::area).orElse(0L);
    }

    public double intersectionOverUnionPercent(CanvasRect other) {
        long intersectionArea = intersectionArea(other);
        if (intersectionArea == 0L) {
            return 0.0;
        }
        long unionArea = area() + other.area() - intersectionArea;
        return unionArea == 0L ? 0.0 : intersectionArea * 100.0 / unionArea;
    }

    public CanvasPoint topLeft() {
        return new CanvasPoint(left, top);
    }

    public CanvasPoint center() {
        return new CanvasPoint(left + width() / 2.0, top + height() / 2.0);
    }

    public CanvasRect translated(int dx, int dy) {
        return new CanvasRect(
                Math.addExact(left, dx),
                Math.addExact(top, dy),
                Math.addExact(right, dx),
                Math.addExact(bottom, dy));
    }
}
