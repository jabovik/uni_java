public class Main {
    public static void main(String[] args) {
        functions.TabulatedFunction parabola = new functions.ArrayTabulatedFunction(-3, 3,
                new double[] { 9, 4, 1, 0, 1, 4, 9 });
        for(functions.FunctionPoint point : parabola){
        System.out.println(point.getX());
        }
    }
}
