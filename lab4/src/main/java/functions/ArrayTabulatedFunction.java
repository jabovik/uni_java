package functions;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private FunctionPoint[] points;
    private int pointsCount;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border is " + leftX + ", bigger than right " + rightX);
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException(pointsCount + " points given, 2 required");
        }
        points = new FunctionPoint[pointsCount];
        this.pointsCount = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + step * i, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double values[]) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border is " + leftX + ", bigger than right " + rightX);
        }
        this.pointsCount = values.length;
        if (pointsCount < 2) {
            throw new IllegalArgumentException(pointsCount + " points given, 2 required");
        }
        points = new FunctionPoint[values.length];
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            points[i] = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint points[]){
        if(points.length <2){
            throw new IllegalArgumentException("Not enough points given");
        }
        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount];
        FunctionPoint prev = null;
        for(int i = 0; i<pointsCount;i++){
            FunctionPoint point = points[i];
            if(prev != null && point.getX() <= prev.getX()){
                throw new IllegalArgumentException("Points array is not ordered");
            }
            this.points[i] = new FunctionPoint(point);
            prev = point;
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getLeftDomainBorder()
     */
    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getRightDomainBorder()
     */
    @Override
    public double getRightDomainBorder() {
        return points[getPointsCount() - 1].getX();
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getFunctionValue(double)
     */
    @Override
    public double getFunctionValue(double x) {
        double leftX = getLeftDomainBorder();
        double rightX = getRightDomainBorder();
        if (x < leftX || x > rightX) {
            return Double.NaN;
        }
        // binary search
        int left = 0;
        int right = getPointsCount() - 1;
        while (right - left > 1) {
            int mid = (left + right) / 2;
            if (points[mid].getX() <= x) {
                left = mid;
            } else {
                right = mid;
            }
        }

        // linear equation
        double x1 = points[left].getX();
        double x2 = points[right].getX();
        double y1 = points[left].getY();
        double y2 = points[right].getY();
        double k = (y2 - y1) / (x2 - x1);
        double b = y1 - k * x1;
        return k * x + b;
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getPointsCount()
     */
    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getPoint(int)
     */
    @Override
    public FunctionPoint getPoint(int index) {
        if (index >= 0 && index < getPointsCount()) {
            return new FunctionPoint(points[index]);
        } else {
            throw new FunctionPointIndexOutOfBoundsException("incorrect index " + index);
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#setPoint(int, functions.FunctionPoint)
     */
    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= getPointsCount()) {
            throw new FunctionPointIndexOutOfBoundsException("incorrect index " + index);
        }
        boolean valid = false;
        if (getPointsCount() == 1 && index == 0) {
            valid = true;
        } else if (index == 0) {
            valid = getPointX(index + 1) > point.getX();
        } else if (index == getPointsCount() - 1) {
            valid = getPointX(index - 1) < point.getX();
        } else {
            valid = getPointX(index - 1) < point.getX() && getPointX(index + 1) > point.getX();
        }
        if (valid) {
            points[index] = new FunctionPoint(point);
        } else {
            throw new InappropriateFunctionPointException("Failed to set point");
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getPointX(int)
     */
    @Override
    public double getPointX(int index) {
        return getPoint(index).getX();
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#setPointX(int, double)
     */
    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        setPoint(index, new FunctionPoint(x, getPointY(index)));
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#getPointY(int)
     */
    @Override
    public double getPointY(int index) {
        return getPoint(index).getY();
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#setPointY(int, double)
     */
    @Override
    public void setPointY(int index, double y) throws InappropriateFunctionPointException {
        setPoint(index, new FunctionPoint(getPointX(index), y));
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#deletePoint(int)
     */
    @Override
    public void deletePoint(int index) {
        if (index < 0 || index >= getPointsCount()) {
            throw new FunctionPointIndexOutOfBoundsException("incorrect index " + index);
        }
        if (getPointsCount() < 3) {
            throw new IllegalStateException("trying to delete point when there are less than 3");
        }
        if (index != getPointsCount() - 1) {
            System.arraycopy(points, index + 1, points, index, getPointsCount() - index - 1);
        }
        pointsCount--;
    }

    /**
     * (non-Javadoc)
     * 
     * @see functions.TabulatedFunction#addPoint(functions.FunctionPoint)
     */
    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int index;
        if (point.getX() < getLeftDomainBorder()) {
            index = 0;
        } else if (point.getX() > getRightDomainBorder()) {
            index = getPointsCount();
        } else {
            // binary search
            int left = 0;
            int right = getPointsCount() - 1;
            while (right - left > 1) {
                int mid = (left + right) / 2;
                if (getPointX(mid) <= point.getX()) {
                    left = mid;
                } else {
                    right = mid;
                }
            }
            if (Math.abs(getPointX(right) - point.getX())< 1e-9 || Math.abs(getPointX(left) - point.getX()) < 1e-9) {
                throw new InappropriateFunctionPointException("point X matches existing");
            }
            index = right;
        }
        if (getPointsCount() < points.length) {
            System.arraycopy(points, index, points, index + 1, getPointsCount() - index);
        } else {
            FunctionPoint[] newPoints = new FunctionPoint[getPointsCount() + 1];
            System.arraycopy(points, 0, newPoints, 0, index);
            System.arraycopy(points, index, newPoints, index + 1, getPointsCount() - index);
            points = newPoints;
        }
        pointsCount++;
        setPoint(index, point);
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new FunctionIterator();
    }

    private class FunctionIterator implements Iterator<FunctionPoint> {
        int count = 0;

        @Override
        public boolean hasNext() {
            return count < pointsCount;
        }

        @Override
        public FunctionPoint next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return getPoint(count++);
        }
    }
}