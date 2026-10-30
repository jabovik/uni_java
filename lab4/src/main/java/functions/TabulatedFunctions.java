package functions;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;

public final class TabulatedFunctions {
    private TabulatedFunctions() {
    }

    private static double[] linspace(double left, double right, int steps) {
        if (left >= right) {
            throw new IllegalArgumentException("Left border is " + left + ", bigger than right " + right);
        }
        if (steps < 2) {
            throw new IllegalArgumentException(steps + " steps given, 2 required");
        }
        double arr[] = new double[steps];
        double step = (right - left) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            arr[i] = left + step * i;
        }
        return arr;
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        double arr[] = linspace(leftX, rightX, pointsCount);
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(arr[i], function.getFunctionValue(arr[i]));
        }
        return new ArrayTabulatedFunction(points);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream stream = new DataOutputStream(out);
        stream.writeInt(function.getPointsCount());
        for (FunctionPoint point : function) {
            stream.writeDouble(point.getX());
            stream.writeDouble(point.getY());
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream stream = new DataInputStream(in);
        int pointsCount = stream.readInt();
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(stream.readDouble(), stream.readDouble());
        }
        return new LinkedListTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        BufferedWriter writer = new BufferedWriter(out);
        writer.write(Integer.toString(function.getPointsCount()));
        writer.write(" ");
        for (FunctionPoint point : function) {
            writer.write(Double.toString(point.getX()));
            writer.write(" ");
            writer.write(Double.toString(point.getY()));
            writer.write(" "); // fix end space
        }
        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException{
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.nextToken();
        int pointsCount = (int)tokenizer.nval;
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for(int i = 0;i<pointsCount;i++){
            tokenizer.nextToken();
            double x = tokenizer.nval;
            tokenizer.nextToken();
            double y = tokenizer.nval;
            points[i] = new FunctionPoint(x,y);
        }
        return new LinkedListTabulatedFunction(points);
    }
}
