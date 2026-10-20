package functions;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    private int pointsCount;
    private FunctionNode head;

    private class FunctionNode {
        private FunctionNode prev, next;
        private FunctionPoint val;

        private FunctionNode() {
            val = null;
            prev = null;
            next = null;
        }

        private FunctionNode(FunctionPoint point) {
            val = point;
            prev = null;
            next = null;
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border is " + leftX + ", bigger than right " + rightX);
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException(pointsCount + " points given, 2 required");
        }
        head = new FunctionNode();
        head.next = head;
        head.prev = head;
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            addNodeToTail().val = new FunctionPoint(leftX + step * i, 0);
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double values[]) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Left border is " + leftX + ", bigger than right " + rightX);
        }
        if (values.length < 2) {
            throw new IllegalArgumentException(values.length + " points given, 2 required");
        }
        head = new FunctionNode();
        head.next = head;
        head.prev = head;
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addNodeToTail().val = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    public double getLeftDomainBorder() {
        return getPointX(0);
    }

    public double getRightDomainBorder() {
        return getPointX(pointsCount - 1);
    }

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
            if (getPointX(mid) <= x) {
                left = mid;
            } else {
                right = mid;
            }
        }

        // linear equation
        double x1 = getPointX(left);
        double x2 = getPointX(right);
        double y1 = getPointY(left);
        double y2 = getPointY(right);
        double k = (y2 - y1) / (x2 - x1);
        double b = y1 - k * x1;
        return k * x + b;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        if (index >= 0 && index < getPointsCount()) {
            return new FunctionPoint(getNodeByIndex(index).val);
        } else {
            throw new FunctionPointIndexOutOfBoundsException("incorrect index " + index);
        }
    }

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
            getNodeByIndex(index).val = new FunctionPoint(point);
        } else {
            throw new InappropriateFunctionPointException("Failed to set point");
        }
    }

    public double getPointX(int index) {
        return getPoint(index).getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        setPoint(index, new FunctionPoint(x, getPointY(index)));
    }

    public double getPointY(int index) {
        return getPoint(index).getY();
    }

    public void setPointY(int index, double y) throws InappropriateFunctionPointException {
        setPoint(index, new FunctionPoint(getPointX(index), y));
    }

    public void deletePoint(int index) {
        if (index < 0 || index >= getPointsCount()) {
            throw new FunctionPointIndexOutOfBoundsException("incorrect index " + index);
        }
        if (getPointsCount() < 3) {
            throw new IllegalStateException("trying to delete point when there are less than 3");
        }
        deleteNodeByIndex(index);
    }

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
            if (getPointX(right) == point.getX() || getPointX(left) == point.getX()) {
                throw new InappropriateFunctionPointException("point X matches existing");
            }
            index = right;
        }
        addNodeByIndex(index);
        setPoint(index, point);
    }

    LinkedListTabulatedFunction() {
        head = new FunctionNode();
        pointsCount = 0;
        head.next = head;
        head.prev = head;
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new IndexOutOfBoundsException();
        }
        FunctionNode node = head.next;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    private FunctionNode addNodeToTail() {
        return addNodeByIndex(pointsCount);
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new IndexOutOfBoundsException();
        }
        FunctionNode node = head;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        node.next.prev = new FunctionNode(); // prev of orig next is new
        node.next.prev.next = node.next; // new list next is next of orig
        node.next = node.next.prev; // next of orig is new
        node.next.prev = node; // next of new is orig
        pointsCount++;
        return node.next;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new IndexOutOfBoundsException();
        }
        FunctionNode node = head;
        for (int i = 0; i <= index; i++) {
            node = node.next;
        }
        node.next.prev = node.prev;
        node.prev.next = node.next;
        pointsCount--;
        return node; // maybe clear node.next and node.prev = null
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new ListFunctionIterator() {
        };
    }

    private class ListFunctionIterator implements Iterator<FunctionPoint> {
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