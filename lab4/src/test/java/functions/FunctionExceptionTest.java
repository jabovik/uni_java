package functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionExceptionTest {
    @Test
    void inappropriatePointDefaultConstructorHasNoMessageOrCause() {
        InappropriateFunctionPointException exception = new InappropriateFunctionPointException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertInstanceOf(Exception.class, exception);
        assertFalse(RuntimeException.class.isInstance(exception));
    }

    @Test
    void inappropriatePointMessageConstructorPreservesMessage() {
        InappropriateFunctionPointException exception = new InappropriateFunctionPointException("duplicate x");
        assertEquals("duplicate x", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void inappropriatePointCauseConstructorPreservesCause() {
        Throwable cause = new IllegalArgumentException("invalid x");
        InappropriateFunctionPointException exception = new InappropriateFunctionPointException(cause);
        assertSame(cause, exception.getCause());
        assertEquals(cause.toString(), exception.getMessage());
    }

    @Test
    void inappropriatePointMessageAndCauseConstructorPreservesBoth() {
        Throwable cause = new IllegalArgumentException("invalid x");
        InappropriateFunctionPointException exception = new InappropriateFunctionPointException("point", cause);
        assertEquals("point", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void inappropriatePointConstructorsAcceptNullMessageAndCause() {
        assertNull(new InappropriateFunctionPointException((String) null).getMessage());
        InappropriateFunctionPointException causeOnly = new InappropriateFunctionPointException((Throwable) null);
        assertNull(causeOnly.getCause());
        assertNull(causeOnly.getMessage());
        InappropriateFunctionPointException both = new InappropriateFunctionPointException(null, null);
        assertNull(both.getMessage());
        assertNull(both.getCause());
    }

    @Test
    void indexExceptionDefaultConstructorAndInheritanceAreCorrect() {
        FunctionPointIndexOutOfBoundsException exception = new FunctionPointIndexOutOfBoundsException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertInstanceOf(IndexOutOfBoundsException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void indexExceptionPreservesMessageAndAcceptsNull() {
        FunctionPointIndexOutOfBoundsException exception = new FunctionPointIndexOutOfBoundsException("index -1");
        assertEquals("index -1", exception.getMessage());
        assertNull(exception.getCause());
        assertNull(new FunctionPointIndexOutOfBoundsException(null).getMessage());
    }
}
