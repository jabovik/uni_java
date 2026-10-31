/**
 * Основной класс для тестирования табличных функций (лабораторная работа 4).
 * Проверяет создание, табулирование, запись/чтение функций и сериализацию.
 */
import functions.*;
import java.io.*;
import functions.basic.*;
import functions.meta.Composition;

class Lab4 {  // Переименовано в PascalCase для соответствия конвенциям Java

    /**
     * Точка входа в программу. Запускает все тесты заданий 8 и 9.
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== ЗАДАНИЕ 8: ПРОВЕРКА РАБОТЫ НАПИСАННЫХ КЛАССОВ ===\n");

            // Часть 1: Тестирование Sin и Cos, табулирование и сравнение
            System.out.println(">>> Часть 1: Sin и Cos, табулирование и сравнение");
            testTask8Part1();

            // Часть 2: Сумма квадратов sin и cos
            System.out.println("\n>>> Часть 2: Сумма квадратов sin и cos для разных точек");
            testTask8Part2();

            // Часть 3: Экспонента в текстовом формате
            System.out.println("\n>>> Часть 3: Экспонента в текстовом формате, запись и чтение");
            TabulatedFunction textFunc = testTask8Part3();  // Возвращаем прочитанную функцию

            // Часть 4: Логарифм в бинарном формате
            System.out.println("\n>>> Часть 4: Логарифм в бинарном формате, запись и чтение");
            TabulatedFunction binaryFunc = testTask8Part4();  // Возвращаем прочитанную функцию

            System.out.println("\n=== ЗАДАНИЕ 9: СЕРИАЛИЗАЦИЯ ===\n");

            // Создаем композицию логарифма от экспоненты
            Function composition = Functions.composition(new Log(Math.E), new Exp());   // ln(exp(x))
            // Serializable для ArrayTabulatedFunction
            System.out.println(">>> Serializable (ArrayTabulatedFunction)");
            ArrayTabulatedFunction tabSer = testTask9Serializable(composition);  // Функция после десериализации

            // Externalizable для LinkedListTabulatedFunction
            System.out.println(">>> Externalizable (LinkedListTabulatedFunction)");
            LinkedListTabulatedFunction tabExt = testTask9Externalizable(composition);  // Функция после десериализации

            // Сравнение всех способов хранения
            System.out.println("\n=== СРАВНЕНИЕ ВСЕХ СПОСОБОВ ХРАНЕНИЯ ===");
            compareAllMethods(textFunc, binaryFunc, tabSer, tabExt);

        } catch (Exception e) {
            // Обработка ошибок тестирования
            System.out.println("Ошибка при тестировании: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Часть 1 задания 8: Создание Sin/Cos, табулирование на [0, π] и сравнение.
     * Выводит значения, точки и разности.
     */
    public static void testTask8Part1() throws Exception {
        System.out.println("1. СОЗДАНИЕ Sin И Cos, ВЫВОД ЗНАЧЕНИЙ НА [0, π] С ШАГОМ 0.1:");

        // Создание математических функций
        Sin sin = new Sin();
        Cos cos = new Cos();

        // Вывод значений с шагом 0.1
        System.out.println("\n Значения Sin и Cos:");
        System.out.println(" x\t\tSin(x)\t\tCos(x)");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf(" %.1f\t\t%.6f\t%.6f%n", x, sin.getFunctionValue(x), cos.getFunctionValue(x));
        }

        System.out.println("\n2. СОЗДАНИЕ ТАБУЛИРОВАННЫХ АНАЛОГОВ С 10 ТОЧКАМИ:");

        // Табулирование функций
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

        // Вывод точек табулированных функций
        System.out.println("\n Точки табулированного синуса:");
        printPoints(tabulatedSin);
        System.out.println("\n Точки табулированного косинуса:");
        printPoints(tabulatedCos);

        System.out.println("\n3. СРАВНЕНИЕ ИСХОДНЫХ И ТАБУЛИРОВАННЫХ ФУНКЦИЙ:");
        System.out.println(" x\t\tSin(x)\t\tTabSin(x)\tРазница\t\t\tCos(x)\t\tTabCos(x)\tРазница");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double sinExact = sin.getFunctionValue(x);
            double sinTab = tabulatedSin.getFunctionValue(x);
            double sinDiff = Math.abs(sinExact - sinTab);

            double cosExact = cos.getFunctionValue(x);
            double cosTab = tabulatedCos.getFunctionValue(x);
            double cosDiff = Math.abs(cosExact - cosTab);

            System.out.printf(" %.1f\t\t%.4f\t\t%.4f\t\t%.6f\t\t%.4f\t\t%.4f\t\t%.6f%n",
                    x, sinExact, sinTab, sinDiff, cosExact, cosTab, cosDiff);
        }
    }

    /**
     * Вспомогательный метод для вывода точек табличной функции.
     */
    private static void printPoints(TabulatedFunction func) {
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.printf(" Точка %d: (%.6f, %.6f)%n", i, func.getPointX(i), func.getPointY(i));
        }
    }

    /**
     * Часть 2 задания 8: Сумма квадратов sin²(x) + cos²(x) для разного числа точек.
     * Проверяет приближение к 1.
     */
    public static void testTask8Part2() throws Exception {
        System.out.println("\n4. СУММА КВАДРАТОВ ТАБУЛИРОВАННЫХ СИНУСА И КОСИНУСА:");

        // Массив количества точек для теста
        int[] pointCounts = {5, 10, 20, 50};

        for (int pointsCount : pointCounts) {
            System.out.println("\n Количество точек в табулированных аналогах: " + pointsCount);

            // Табулирование sin и cos
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, pointsCount);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, pointsCount);

            // Композиция: sin²(x) + cos²(x)
            Function sumOfSquares = Functions.sum(
                    Functions.power(tabulatedSin, 2),
                    Functions.power(tabulatedCos, 2)
            );

            // Вывод результатов с шагом 0.1
            System.out.println(" x\t\tsin²(x)+cos²(x)");
            for (double x = 0; x <= Math.PI; x += 0.1) {
                double result = sumOfSquares.getFunctionValue(x);
                System.out.printf(" %.1f\t\t%.8f%n", x, result);
            }

            // Вычисление среднего отклонения от 1
            double totalDeviation = 0;
            int samples = 0;
            for (double x = 0; x <= Math.PI; x += 0.05) {
                double deviation = Math.abs(1.0 - sumOfSquares.getFunctionValue(x));
                totalDeviation += deviation;
                samples++;
            }
            System.out.printf(" Среднее отклонение от 1: %.10f%n", totalDeviation / samples);
        }
    }

    /**
     * Часть 3 задания 8: Запись/чтение экспоненты в текстовом формате.
     * @return прочитанная функция для сравнения
     */
    public static TabulatedFunction testTask8Part3() throws Exception {
        System.out.println("\n5. ЭКСПОНЕНТА - ТЕКСТОВЫЙ ФОРМАТ:");

        // Табулирование экспоненты на [0, 10] с 11 точками
        TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);

        System.out.println("\n Исходная табулированная экспонента (11 точек):");
        printPoints(tabulatedExp);

        // Запись в текстовый файл
        try (FileWriter writer = new FileWriter("exp_function.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabulatedExp, writer);
            System.out.println("\n Функция записана в файл: exp_function.txt");
        }

        // Чтение из файла
        TabulatedFunction readExp;
        try (FileReader reader = new FileReader("exp_function.txt")) {
            readExp = TabulatedFunctions.readTabulatedFunction(reader);
            System.out.println(" Функция прочитана из файла: exp_function.txt");
        }

        // Сравнение исходной и прочитанной
        System.out.println("\n СРАВНЕНИЕ ИСХОДНОЙ И ПРОЧИТАННОЙ ЭКСПОНЕНТЫ:");
        compareFunctions(tabulatedExp, readExp, 0, 10, 1.0);

        return readExp;  // Возврат для общего сравнения
    }

    /**
     * Часть 4 задания 8: Запись/чтение логарифма в бинарном формате.
     * @return прочитанная функция для сравнения
     */
    public static TabulatedFunction testTask8Part4() throws Exception {
        System.out.println("\n6. ЛОГАРИФМ - БИНАРНЫЙ ФОРМАТ:");

        // Табулирование натурального логарифма на [1, 10] с 11 точками
        TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 11);

        System.out.println("\n Исходный табулированный логарифм (11 точек):");
        printPoints(tabulatedLog);

        // Запись в бинарный файл
        try (FileOutputStream fos = new FileOutputStream("log_function.bin")) {
            TabulatedFunctions.outputTabulatedFunction(tabulatedLog, fos);
            System.out.println("\n Функция записана в файл: log_function.bin");
        }

        // Чтение из бинарного файла
        TabulatedFunction readLog;
        try (FileInputStream fis = new FileInputStream("log_function.bin")) {
            readLog = TabulatedFunctions.inputTabulatedFunction(fis);
            System.out.println(" Функция прочитана из файла: log_function.bin");
        }

        // Сравнение
        System.out.println("\n СРАВНЕНИЕ ИСХОДНОГО И ПРОЧИТАННОГО ЛОГАРИФМА:");
        compareFunctions(tabulatedLog, readLog, 1, 10, 1.0);

        return readLog;
    }

    /**
     * Вспомогательный метод для сравнения двух функций на интервале.
     */
    private static void compareFunctions(TabulatedFunction original, TabulatedFunction read, double start, double end, double step) {
        for (double x = start; x <= end; x += step) {
            double orig = original.getFunctionValue(x);
            double r = read.getFunctionValue(x);
            boolean matches = Math.abs(orig - r) < 1e-10;
            System.out.printf(" %.1f: Исходная=%.6f, Прочитанная=%.6f, Совпадают=%s%n",
                    x, orig, r, matches ? "Да" : "НЕТ!");
        }
    }

    /**
     * Задание 9: Тест Serializable для ArrayTabulatedFunction.
     * @return десериализованная функция
     */
    public static ArrayTabulatedFunction testTask9Serializable(Function fun) throws Exception {

        // Создание тестовой функции
        // Предполагается, что TabulatedFunctions.tabulate() возвращает объект ArrayTabulatedFunction
        ArrayTabulatedFunction func = (ArrayTabulatedFunction) TabulatedFunctions.tabulate(fun, 0, 10, 11);

        // Сериализация
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("ser.bin"))) {
            out.writeObject(func);
        }

        // Десериализация
        ArrayTabulatedFunction funcRead;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("ser.bin"))) {
            funcRead = (ArrayTabulatedFunction) in.readObject();
        }

        System.out.println("ArrayTabulatedFunction сериализована и десериализована успешно.");
        return funcRead;
    }

    /**
     * Задание 9: Тест Externalizable для LinkedListTabulatedFunction.
     * @return десериализованная функция
     */
    public static LinkedListTabulatedFunction testTask9Externalizable(Function func) throws Exception {
        
        LinkedListTabulatedFunction funcRead = new LinkedListTabulatedFunction(0, 10, 11);
        for (int i = 0; i < funcRead.getPointsCount(); i++) {
            double x = funcRead.getPointX(i);
            funcRead.setPointY(i, func.getFunctionValue(x)); // ln(e^x) = x
        }

        // Сериализация (предполагается реализация writeExternal/readExternal в классе)
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("ext.bin"))) {
            out.writeObject(funcRead);  // Вызовет writeExternal, если класс реализует Externalizable
        }

        // Десериализация
        LinkedListTabulatedFunction funcReadDeserialized;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("ext.bin"))) {
            funcReadDeserialized = (LinkedListTabulatedFunction) in.readObject();   // Вызовет readExternal, если класс реализует Externalizable
        }

        System.out.println("LinkedListTabulatedFunction внешне сериализована и десериализована успешно.");
        return funcReadDeserialized;
    }

    /**
     * Сравнение значений всех сохраненных функций и размеров файлов.
     */
    private static void compareAllMethods(TabulatedFunction textFunc, TabulatedFunction binaryFunc,
                                          ArrayTabulatedFunction tabSer, LinkedListTabulatedFunction tabExt) throws Exception {
        // Минимальное число точек для сравнения
        int points = Math.min(Math.min(textFunc.getPointsCount(), binaryFunc.getPointsCount()),
                Math.min(tabSer.getPointsCount(), tabExt.getPointsCount()));

        System.out.println(" x\t\t\tText\t\t\tBinary\t\t\tSerializable\t\t\tExternalizable");
        for (int i = 0; i < points; i++) {
            double vText = textFunc.getPointY(i);
            double vBin = binaryFunc.getPointY(i);
            double vSer = tabSer.getPointY(i);
            double vExt = tabExt.getPointY(i);

            System.out.printf(" %.1f\t\t%.6f\t\t\t%.6f\t\t%.6f\t\t\t\t%.6f%n",
                    textFunc.getPointX(i), vText, vBin, vSer, vExt);
        }

        // Размеры файлов
        System.out.println("\n Размер файлов:");
        printFileSize("exp_function.txt");
        printFileSize("log_function.bin");
        printFileSize("ser.bin");
        printFileSize("ext.bin");

        System.out.println("\n=== ВСЕ ЗАДАНИЯ УСПЕШНО ВЫПОЛНЕНЫ ===");
    }

    /**
     * Вспомогательный метод для вывода размера файла.
     */
    private static void printFileSize(String filename) {
        File file = new File(filename);
        System.out.println(" " + file.getName() + ": " + file.length() + " байт");
    }
}
