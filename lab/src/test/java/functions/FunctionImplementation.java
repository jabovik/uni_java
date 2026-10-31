package functions;

/** Runs the same public contract against both storage implementations. */
enum FunctionImplementation {
    ARRAY {
        @Override
        TabulatedFunction create(double left, double right, int count) {
            return new ArrayTabulatedFunction(left, right, count);
        }

        @Override
        TabulatedFunction create(double left, double right, double... values) {
            return new ArrayTabulatedFunction(left, right, values);
        }
    },
    LINKED_LIST {
        @Override
        TabulatedFunction create(double left, double right, int count) {
            return new LinkedListTabulatedFunction(left, right, count);
        }

        @Override
        TabulatedFunction create(double left, double right, double... values) {
            return new LinkedListTabulatedFunction(left, right, values);
        }
    };

    abstract TabulatedFunction create(double left, double right, int count);

    abstract TabulatedFunction create(double left, double right, double... values);

    TabulatedFunction parabola() {
        return create(-2, 2, new double[]{4, 1, 0, 1, 4});
    }
}
