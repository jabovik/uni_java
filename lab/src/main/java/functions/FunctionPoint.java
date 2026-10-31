package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    final private double x, y;

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FunctionPoint)) {
            return false;
        }
        FunctionPoint other = (FunctionPoint) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        int xHash = (int) (xBits ^ (xBits >>> 32));
        int yHash = (int) (yBits ^ (yBits >>> 32));
        return xHash ^ yHash;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new FunctionPoint(this);
    }
}
