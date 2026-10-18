package functions;

public class TabulatedFunction {
    private FunctionPoint[] points;
    private int pointsCount;

    public TabulatedFunction(double leftX, double rightX, int pointsCount) {
        points = new FunctionPoint[pointsCount];
        this.pointsCount = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + step * i, 0);
        }
    }

    public TabulatedFunction(double leftX, double rightX, double values[]) {
        points = new FunctionPoint[values.length];
        this.pointsCount = values.length;
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            points[i] = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[getPointsCount() - 1].getX();
    }

    public double getFunctionValue(double x) { // test for border x values
        // out of border
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

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        if (index >= 0 && index < getPointsCount()) {
            return new FunctionPoint(points[index]);
        } else {
            return new FunctionPoint(Double.NaN, Double.NaN);
        }
    }

    public void setPoint(int index, FunctionPoint point) {
        boolean valid = false;
        if (index < 0 || index >= getPointsCount()) {
        } else if (getPointsCount() == 1 && index == 0) {
            valid = true;
        } else if (index == 0) {
            valid = getPointX(index + 1) > point.getX();
        } else if (index == getPointsCount() - 1) {
            valid = getPointX(index - 1) < point.getX();
        } else if (index > 0 && index < getPointsCount() - 1) {
            valid = getPointX(index - 1) < point.getX() && getPointX(index + 1) > point.getX();
        }
        if (valid) {
            points[index] = new FunctionPoint(point);
        } else {
            // throw new IllegalArgumentException("Invalid input");
        }
    }

    public double getPointX(int index) {
        return getPoint(index).getX();
    }

    public void setPointX(int index, double x) {
        setPoint(index, new FunctionPoint(x, getPointY(index)));
    }

    public double getPointY(int index) {
        return getPoint(index).getY();
    }

    public void setPointY(int index, double y) {
        setPoint(index, new FunctionPoint(getPointX(index), y));
    }

    public void deletePoint(int index) {
        if (index != getPointsCount() - 1) {
            System.arraycopy(points, index + 1, points, index, getPointsCount() - index - 1);
        }
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) {
        int index;
        if (point.getX() < getLeftDomainBorder()) {
            index = 0;
        } else if (point.getX() > getRightDomainBorder()) {
            index = getPointsCount();
        } else {
            // binary search
            int left = 0;
            int right = getPointsCount(); // +1 for end
            while (right - left > 1) {
                int mid = (left + right) / 2;
                if (getPointX(mid) <= point.getX()) {
                    left = mid;
                } else {
                    right = mid;
                }
            }
            if (Math.abs(getPointX(right) - point.getX()) < 1e-9) { // if matches w/ existing -> rewrite point
                points[right] = new FunctionPoint(point);
                return;
            } else if (Math.abs(getPointX(left) - point.getX()) < 1e-9) {
                points[left] = new FunctionPoint(point);
                return;
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
        points[index] = new FunctionPoint(point);
    }
}