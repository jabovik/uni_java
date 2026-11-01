/**
 * Основной класс для лабораторной работы №5: Переопределение методов Object.
 * Тестирует корректную реализацию toString(), equals(), hashCode() и clone()
 * для классов FunctionPoint, ArrayTabulatedFunction и LinkedListTabulatedFunction.
 */
import functions.*;

public class Main5 {

    /**
     * Точка входа в программу. Запускает все тесты переопределенных методов Object.
     * @throws InappropriateFunctionPointException 
     */
    public static void main(String[] args) throws InappropriateFunctionPointException {
        System.out.println("=== ЛАБОРАТОРНАЯ РАБОТА №5: ПЕРЕОПРЕДЕЛЕНИЕ МЕТОДОВ OBJECT ===\n");
        
        // Последовательное выполнение всех тестов
        testFunctionPoint();
        testArrayTabulatedFunction();
        testLinkedListTabulatedFunction();
        testCrossComparison();
        testComplexScenarios();
    }

    /**
     * Тест 1: Проверка методов Object для FunctionPoint.
     * Тестирует toString(), equals(), hashCode() и clone().
     */
    private static void testFunctionPoint() {
        System.out.println("=== ТЕСТ 1: FunctionPoint ===");

        // Создание тестовых точек
        FunctionPoint p1 = new FunctionPoint(1.0, 2.0);
        FunctionPoint p2 = new FunctionPoint(1.0, 2.0);  // Одинаковая с p1
        FunctionPoint p3 = new FunctionPoint(1.0, 3.0);  // Разная Y

        // 1. Тестирование toString()
        System.out.println("1. toString():");
        System.out.println("   p1: " + p1);

        // 2. Тестирование equals()
        System.out.println("\n2. equals():");
        System.out.println("   p1.equals(p2): " + p1.equals(p2) + " (ожидается true)");
        System.out.println("   p1.equals(p3): " + p1.equals(p3) + " (ожидается false)");
        System.out.println("   p1.equals(null): " + p1.equals(null) + " (ожидается false)");

        // 3. Тестирование hashCode()
        System.out.println("\n3. hashCode():");
        System.out.println("   p1.hashCode(): " + p1.hashCode());
        System.out.println("   p2.hashCode(): " + p2.hashCode());
        System.out.println("   p1.hashCode() == p2.hashCode(): " + (p1.hashCode() == p2.hashCode()));

        // 4. Тестирование clone()
        System.out.println("\n4. clone():");
        FunctionPoint clone = p1.clone();
        System.out.println("   p1.equals(clone): " + p1.equals(clone) + " (ожидается true)");
        System.out.println("   p1 == clone: " + (p1 == clone) + " (ожидается false - разные ссылки)");
        
        // Изменение клона для проверки независимости
        clone.setX(10.0);
        System.out.println("   После изменения клона:");
        System.out.println("   p1.equals(clone): " + p1.equals(clone) + " (ожидается false)");
        System.out.println();
    }

    /**
     * Тест 2: Проверка методов Object для ArrayTabulatedFunction.
     * Проверяет глубокое клонирование массива точек.
     * @throws InappropriateFunctionPointException 
     */
    private static void testArrayTabulatedFunction() throws InappropriateFunctionPointException {
        System.out.println("=== ТЕСТ 2: ArrayTabulatedFunction ===");

        // Создание идентичных массивов точек
        FunctionPoint[] points1 = createTestPoints();
        FunctionPoint[] points2 = createTestPoints();

        ArrayTabulatedFunction f1 = new ArrayTabulatedFunction(points1);
        ArrayTabulatedFunction f2 = new ArrayTabulatedFunction(points2);

        // 1. toString()
        System.out.println("1. toString():");
        System.out.println("   f1: " + f1);

        // 2. equals()
        System.out.println("\n2. equals():");
        System.out.println("   f1.equals(f2): " + f1.equals(f2) + " (ожидается true)");

        // 3. hashCode()
        System.out.println("\n3. hashCode():");
        printHashCodeComparison(f1, f2);

        // 4. clone() с проверкой глубокого копирования
        System.out.println("\n4. clone() и глубокое клонирование:");
        testDeepClone(f1, "ArrayTabulatedFunction");
        System.out.println();
    }

    /**
     * Тест 3: Проверка методов Object для LinkedListTabulatedFunction.
     * Аналогично ArrayTabulatedFunction, но для связанного списка.
     * @throws InappropriateFunctionPointException 
     */
    private static void testLinkedListTabulatedFunction() throws InappropriateFunctionPointException {
        System.out.println("=== ТЕСТ 3: LinkedListTabulatedFunction ===");

        FunctionPoint[] points = createTestPoints();

        LinkedListTabulatedFunction f1 = new LinkedListTabulatedFunction(points);
        LinkedListTabulatedFunction f2 = new LinkedListTabulatedFunction(points);

        // 1. toString()
        System.out.println("1. toString():");
        System.out.println("   f1: " + f1);

        // 2. equals()
        System.out.println("\n2. equals():");
        System.out.println("   f1.equals(f2): " + f1.equals(f2) + " (ожидается true)");

        // 3. hashCode()
        System.out.println("\n3. hashCode():");
        printHashCodeComparison(f1, f2);

        // 4. clone()
        System.out.println("\n4. clone() и глубокое клонирование:");
        testDeepClone(f1, "LinkedListTabulatedFunction");
        System.out.println();
    }

    /**
     * Тест 4: Кросс-сравнение ArrayTabulatedFunction и LinkedListTabulatedFunction.
     * Проверяет equals() между разными реализациями.
     */
    private static void testCrossComparison() {
        System.out.println("=== ТЕСТ 4: Сравнение разных реализаций ===");

        FunctionPoint[] points = createTestPoints();

        // Создание функций разных типов с одинаковыми данными
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);
        LinkedListTabulatedFunction linkedFunc = new LinkedListTabulatedFunction(points);

        // Кросс-сравнение equals()
        System.out.println("arrayFunc.equals(linkedFunc): " + arrayFunc.equals(linkedFunc) + " (ожидается true)");
        System.out.println("linkedFunc.equals(arrayFunc): " + linkedFunc.equals(arrayFunc) + " (ожидается true)");

        // Клонирование через интерфейс TabulatedFunction
        System.out.println("\nКлонирование через интерфейс:");
        TabulatedFunction arrayClone = arrayFunc.clone();
        TabulatedFunction linkedClone = linkedFunc.clone();
        
        System.out.println("arrayFunc.equals(arrayClone): " + arrayFunc.equals(arrayClone) + " (ожидается true)");
        System.out.println("linkedFunc.equals(linkedClone): " + linkedFunc.equals(linkedClone) + " (ожидается true)");
        System.out.println();
    }

    /**
     * Тест 5: Комплексные сценарии с изменением hashCode и глубоким клонированием.
     */
    private static void testComplexScenarios() throws InappropriateFunctionPointException {
        System.out.println("=== ТЕСТ 5: Комплексные сценарии ===");

        // Создание функции x² с 5 точками
        FunctionPoint[] quadraticPoints = createQuadraticPoints();

        ArrayTabulatedFunction quadratic = new ArrayTabulatedFunction(quadraticPoints);

        // 1. Проверка изменения hashCode при модификации
        System.out.println("1. Проверка hashCode() при изменении:");
        testHashCodeChange(quadratic);

        // 2. Глубокое клонирование LinkedList
        System.out.println("\n2. Глубокое клонирование:");
        testLinkedListDeepClone(quadraticPoints);
        System.out.println();
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Создает стандартный массив тестовых точек: (0,0), (1,1), (2,4).
     */
    private static FunctionPoint[] createTestPoints() {
        return new FunctionPoint[] {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0)
        };
    }

    /**
     * Создает массив точек для функции x²: (0,0), (1,1), (2,4), (3,9), (4,16).
     */
    private static FunctionPoint[] createQuadraticPoints() {
        FunctionPoint[] points = new FunctionPoint[5];
        for (int i = 0; i < 5; i++) {
            points[i] = new FunctionPoint(i, i * i);
        }
        return points;
    }

    /**
     * Выводит сравнение hashCode двух функций.
     */
    private static void printHashCodeComparison(TabulatedFunction f1, TabulatedFunction f2) {
        System.out.println("   f1.hashCode(): " + f1.hashCode());
        System.out.println("   f2.hashCode(): " + f2.hashCode());
        System.out.println("   equals и hashCode согласованы: " + 
            (f1.equals(f2) == (f1.hashCode() == f2.hashCode())));
    }

    /**
     * Тестирует глубокое клонирование функции.
     * @throws InappropriateFunctionPointException 
     */
    private static void testDeepClone(TabulatedFunction original, String funcType) throws InappropriateFunctionPointException {
        TabulatedFunction clone = original.clone();
        System.out.println("   " + funcType + ".equals(clone): " + original.equals(clone) + " (ожидается true)");

        // Изменение оригинала для проверки независимости клона
        original.setPointY(1, 999.0);
        System.out.println("   После изменения оригинала:");
        System.out.println("   original.equals(clone): " + original.equals(clone) + " (ожидается false)");
        System.out.println("   clone.getPointY(1): " + clone.getPointY(1) + " (ожидается 1.0)");
    }

    /**
     * Тестирует изменение hashCode при модификации функции.
     */
    private static void testHashCodeChange(ArrayTabulatedFunction func) throws InappropriateFunctionPointException {
        int originalHash = func.hashCode();
        System.out.println("   Исходный hashCode: " + originalHash);

        // Минимальное изменение значения
        func.setPointY(2, func.getPointY(2) + 0.001);
        int newHash = func.hashCode();
        System.out.println("   После изменения Y[2]: " + newHash);
        System.out.println("   Хэш изменился: " + (originalHash != newHash) + " (ожидается true)");
    }

    /**
     * Тестирует глубокое клонирование LinkedListTabulatedFunction.
     */
    private static void testLinkedListDeepClone(FunctionPoint[] points) throws InappropriateFunctionPointException {
        TabulatedFunction original = new LinkedListTabulatedFunction(points);
        TabulatedFunction clone = original.clone();

        System.out.println("   original.equals(clone): " + original.equals(clone) + " (ожидается true)");

        original.setPointY(1, 999.0);
        System.out.println("   После изменения оригинала:");
        System.out.println("   original.equals(clone): " + original.equals(clone) + " (ожидается false)");
        System.out.println("   clone.getPointY(1): " + clone.getPointY(1) + " (ожидается 1.0)");
    }
}
