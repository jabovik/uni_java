package functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FunctionPointTest {
    @Test
    void defaultConstructorCreatesOrigin() {
        FunctionPoint point = new FunctionPoint();
        assertEquals(0.0, point.getX());
        assertEquals(0.0, point.getY());
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "-2.5, 3.75", "1000000, -1000000", "-0.0, -0.0"})
    void constructorAndGettersPreserveCoordinates(double x, double y) {
        FunctionPoint point = new FunctionPoint(x, y);
        assertEquals(x, point.getX());
        assertEquals(y, point.getY());
    }

    @Test
    void copyConstructorCreatesIndependentObjectWithSameCoordinates() {
        FunctionPoint original = new FunctionPoint(-2.5, 7.25);
        FunctionPoint copy = new FunctionPoint(original);
        assertNotSame(original, copy);
        assertEquals(original.getX(), copy.getX());
        assertEquals(original.getY(), copy.getY());
    }

    @Test
    void copyConstructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new FunctionPoint(null));
    }

    @Test
    void pointHolderPreservesSpecialDoubleValues() {
        FunctionPoint point = new FunctionPoint(Double.NaN, Double.POSITIVE_INFINITY);
        FunctionPoint copy = new FunctionPoint(point);
        assertTrue(Double.isNaN(copy.getX()));
        assertEquals(Double.POSITIVE_INFINITY, copy.getY());
        assertEquals(Double.NEGATIVE_INFINITY, new FunctionPoint(0, Double.NEGATIVE_INFINITY).getY());
    }
}
