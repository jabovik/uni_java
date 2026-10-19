
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private final double[] values = {25, 16, 9, 4, 1, 0, 1, 4, 9, 16, 25};
    private final TabulatedFunction list = new LinkedListTabulatedFunction(-5, 5, values);
    @Test
    void TestPointsCount(){
        assertEquals(11, list.getPointsCount());
    }
}
