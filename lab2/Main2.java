// Файл: Main.java
import functions.*;

public class Main2 {
    public static void main(String[] args) {
        // Создаем табулированную функцию для y = x^2
        System.out.println("=== Создание функции y = x^2 ===");
        double[] values = {25, 16, 9, 4, 1, 0, 1, 4, 9, 16, 25};
        TabulatedFunction function = new TabulatedFunction(-5, 5, values);
        dummyToString(function);

        System.out.println("Функция параболы");
        System.out.println("Область определения: [" + function.getLeftDomainBorder() + "; " + function.getRightDomainBorder() + "]");
        System.out.println("Количество точек: " + function.getPointsCount());

        System.out.println("\nТочки функции:");
        for (int i = 0; i < function.getPointsCount(); i++) {
            System.out.printf("f%d: (%.3f; %.3f)%n", i, function.getPointX(i), function.getPointY(i));
        }

        System.out.println("\nЗначения f(x)  разных точках: ");
        double[] test = {-5, -4.5, 4.5, 5, 0, 9};
        for (double x : test) {
            if (Double.isNaN(function.getFunctionValue(x))) {
                System.out.printf("f(%.3f): не определенно%n", x);
            } else {
                System.out.printf("f(%.3f) = %.3f%n", x, function.getFunctionValue(x));
            }
        }

        System.out.println("\nДобавление точки (0.5, 0.25): ");
        FunctionPoint test_point = new FunctionPoint(0.5, 0.25);
        function.addPoint(test_point);
        dummyToString(function);

        System.out.println("\nДобавление точки (6, 36):");
        test_point = new FunctionPoint(6, 36);
        function.addPoint(test_point);
        dummyToString(function);

        System.out.println("\nУдаление точки с индексом 4:");
        System.out.printf("Точка f4 = (%.3f, %.3f)%n", function.getPointX(4), function.getPointY(4));
        function.deletePoint(4);
        dummyToString(function);

        System.out.println("\nУдаление точки с индексом 0:");
        System.out.printf("Точка f0 = (%.3f, %.3f)%n", function.getPointX(0), function.getPointY(0));
        function.deletePoint(0);
        dummyToString(function);

        System.out.println("\nЗамена точки с индексом 2 на точку f = (-2.5, 6.25)");
        test_point = new FunctionPoint(-2.5, 6.25);
        System.out.printf("Исходная точка: f2= (%.3f; %.3f)%n", function.getPointX(2), function.getPointY(2));
        function.setPoint(2, test_point);
        System.out.printf("Измененная точка: f2= (%.3f; %.3f)%n", function.getPointX(2), function.getPointY(2));
        dummyToString(function);

        System.out.println("\nЗамена точки с номером 0 точку f = (-3.5, 12.25)");
        System.out.printf("Исходная точка: f0= (%.3f; %.3f)%n", function.getPointX(0), function.getPointY(0));
        function.setPointX(0, -3.5);
        function.setPointY(0, 12.25);
        System.out.printf("Измененная точка: f0= (%.3f; %.3f)%n", function.getPointX(0), function.getPointY(0));
        dummyToString(function);
       
    }

    // Функция для преобразования 
    public static void dummyToString(TabulatedFunction function) {
        System.out.println("Табулированная функция:");
        for (int i = 0; i < function.getPointsCount(); i++) {
            System.out.printf("(%6.2f; %6.2f)\n", function.getPointX(i), function.getPointY(i));
        }
    }
}