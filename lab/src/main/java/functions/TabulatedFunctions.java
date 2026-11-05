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
    private static TabulatedFunctionFactory factory = new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory();

    public static void setTabulatedFunctionFactory(String type){
        if ("Array".equals(type)){
            factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
        }
        else if("LinkedList".equals(type)){
            factory = new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory();
        }
        else{
            throw new IllegalArgumentException("No such type: " + type);
        }
    }

    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory fact){
        factory = fact;
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount){
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values){
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points){
        return factory.createTabulatedFunction(points);
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> type, double leftX, double rightX, int pointsCount){
        try{
            return type.getConstructor(double.class, double.class, int.class).newInstance(leftX,rightX,pointsCount);
        }
        catch (ReflectiveOperationException e){
            throw new IllegalArgumentException("Could not create tabulated function", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> type, double leftX, double rightX, double[] values){
        try{
            return type.getConstructor(double.class, double.class, double[].class).newInstance(leftX,rightX,values);
        }
        catch (ReflectiveOperationException e){
            throw new IllegalArgumentException("Could not create tabulated function", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> type, FunctionPoint[] points){
        try{
            return type.getConstructor(FunctionPoint[].class).newInstance((Object) points);
        }
        catch (ReflectiveOperationException e){
            throw new IllegalArgumentException("Could not create tabulated function", e);
        }
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
        return createTabulatedFunction(points);
    }

    public static TabulatedFunction tabulate(Class<? extends TabulatedFunction> type, Function function, double leftX, double rightX, int pointsCount) {
        double arr[] = linspace(leftX, rightX, pointsCount);
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(arr[i], function.getFunctionValue(arr[i]));
        }
        return createTabulatedFunction(type,points);
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
        return createTabulatedFunction(points);
    }

    public static TabulatedFunction inputTabulatedFunction(Class<? extends TabulatedFunction> type,InputStream in) throws IOException {
        DataInputStream stream = new DataInputStream(in);
        int pointsCount = stream.readInt();
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(stream.readDouble(), stream.readDouble());
        }
        return createTabulatedFunction(type,points);
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
        return createTabulatedFunction(points);
    }

    public static TabulatedFunction readTabulatedFunction(Class<? extends TabulatedFunction> type,Reader in) throws IOException{
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
        return createTabulatedFunction(type,points);
    }
}
