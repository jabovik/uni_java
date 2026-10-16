import functions.FunctionPoint;
import functions.TabulatedFunction;

public class Main {

        private static void check(String name, boolean result) {
                System.out.println((result ? "[OK] " : "[FAIL] ") + name);
        }

        private static boolean approximatelyEqual(double a, double b) {
                return Math.abs(a - b) < 1e-9;
        }

        public static void main(String[] args) {
                // y = x^2
                TabulatedFunction f = new TabulatedFunction(0.0, 4.0, new double[] { 0.0, 1.0, 4.0, 9.0, 16.0 });

                System.out.println("=== Создание и границы ===");
                check("Количество точек", f.getPointsCount() == 5);
                check("Левая граница", f.getLeftDomainBorder() == 0.0);
                check("Правая граница", f.getRightDomainBorder() == 4.0);

                System.out.println("\n=== Интерполяция ===");
                check("Значение в точке 0", f.getFunctionValue(0.0) == 0.0);
                check("Значение в точке 2", f.getFunctionValue(2.0) == 4.0);
                check("Интерполяция между точками", approximatelyEqual(f.getFunctionValue(1.5), 2.5));
                check("Правая граница", f.getFunctionValue(4.0) == 16.0);
                check("Слева от области определения", Double.isNaN(f.getFunctionValue(-1.0)));
                check("Справа от области определения", Double.isNaN(f.getFunctionValue(5.0)));

                System.out.println("\n=== Изменение точек ===");
                f.setPointY(2, 100.0);
                check("Изменение Y", f.getPointY(2) == 100.0);

                f.setPointX(2, 2.5);
                check("Изменение X", f.getPointX(2) == 2.5);

                double oldX = f.getPointX(2);
                f.setPointX(2, 10.0);
                check("Некорректный X не изменил точку", f.getPointX(2) == oldX);

                double oldY = f.getPointY(2);
                f.setPoint(2, new FunctionPoint(10.0, 200.0));
                check("Некорректная замена не произошла", f.getPointY(2) == oldY);

                System.out.println("\n=== Добавление точек ===");
                f.addPoint(new FunctionPoint(-1.0, 1.0));
                check("Добавление в начало", f.getPointsCount() == 6 && f.getPointX(0) == -1.0);

                f.addPoint(new FunctionPoint(1.5, 2.25));
                check("Добавление в середину", f.getPointsCount() == 7 && f.getPointX(3) == 1.5);

                f.addPoint(new FunctionPoint(5.0, 25.0));
                check("Добавление в конец", f.getPointsCount() == 8 && f.getPointX(f.getPointsCount() - 1) == 5.0);

                int count = f.getPointsCount();
                f.addPoint(new FunctionPoint(1.5, 999.0));
                check("Добавление существующего X заменяет точку", f.getPointsCount() == count && f.getPointY(3) == 999.0);

                System.out.println("\n=== Удаление точек ===");
                f.deletePoint(3);
                check("Удаление из середины", f.getPointsCount() == 7 && f.getPointX(3) == 2.5);

                f.deletePoint(0);
                check("Удаление из начала", f.getPointsCount() == 6 && f.getPointX(0) == 0.0);

                f.deletePoint(f.getPointsCount() - 1);
                check("Удаление из конца", f.getPointsCount() == 5);

                System.out.println("\n=== Проверка порядка точек ===");
                boolean sorted = true;
                for (int i = 1; i < f.getPointsCount(); i++) {
                        if (f.getPointX(i - 1) >= f.getPointX(i)) {
                                sorted = false;
                                break;
                        }
                }
                check("Точки отсортированы по X", sorted);

                System.out.println("\n=== Второй конструктор ===");
                TabulatedFunction zeros = new TabulatedFunction(-2.0, 2.0, 5);
                check("Конструктор с количеством точек", zeros.getPointsCount() == 5);
                check("Значения по умолчанию равны нулю", zeros.getPointY(0) == 0.0 && zeros.getPointY(4) == 0.0);
        }
}