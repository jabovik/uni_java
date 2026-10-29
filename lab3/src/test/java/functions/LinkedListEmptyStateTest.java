package functions;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListEmptyStateTest {
    @Test
    void packagePrivateConstructorCreatesEmptyIterable() {
        LinkedListTabulatedFunction f = new LinkedListTabulatedFunction();
        assertEquals(0, f.getPointsCount());
        Iterator<FunctionPoint> iterator = f.iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.getPoint(0));
    }
}
