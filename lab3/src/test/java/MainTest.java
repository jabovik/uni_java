import functions.ArrayTabulatedFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@ResourceLock("SYSTEM_OUT")
@ResourceLock("DEFAULT_LOCALE")
class MainTest {
    @Test
    void mainPrintsAllParabolaCoordinatesInOrder() throws Throwable {
        String output = captureOutput(() -> Main.main(new String[0]));
        assertEquals("-3.0\n-2.0\n-1.0\n0.0\n1.0\n2.0\n3.0\n", output);
    }

    @Test
    void lab3DemonstrationCompletesForBothImplementationsAndCatchesExpectedErrors() throws Throwable {
        String output = captureOutput(() -> lab_3.main(new String[0]));
        assertTrue(output.contains("ПРОВЕРКА ArrayTabulatedFunction:"));
        assertTrue(output.contains("ПРОВЕРКА LinkedListTabulatedFunction:"));
        assertEquals(2, output.split("Количество точек: 11", -1).length - 1);
        assertEquals(2, output.split("f\\(-4\\.500\\) = 20\\.500", -1).length - 1);
        assertEquals(2, output.split("f\\(9\\.000\\): не определенно", -1).length - 1);
        assertFalse(output.contains("успешно сработал"), "All deliberately invalid demo operations must throw");
        assertEquals(2, output.split("point X matches existing", -1).length - 1);
    }

    @Test
    void dummyToStringPrintsEachPoint() throws Throwable {
        String output = captureOutput(() -> lab_3.dummyToString(
                new ArrayTabulatedFunction(0, 1, new double[]{2, 3})));
        assertEquals("Табулированная функция:\n(  0.00;   2.00)\n(  1.00;   3.00)\n", output);
    }

    private static String captureOutput(Executable action) throws Throwable {
        PrintStream previousOutput = System.out;
        Locale previousLocale = Locale.getDefault();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            Locale.setDefault(Locale.ROOT);
            action.execute();
        } finally {
            System.setOut(previousOutput);
            Locale.setDefault(previousLocale);
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
