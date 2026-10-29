package functions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionTest {
    private static final double EPS = 1e-12;

    static Stream<Arguments> invalidBounds() {
        return Stream.of(FunctionImplementation.values()).flatMap(type -> Stream.of(
                Arguments.of(type, 0.0, 0.0),
                Arguments.of(type, 2.0, -2.0),
                Arguments.of(type, -1.0, -1.0)));
    }

    static Stream<Arguments> invalidCounts() {
        return Stream.of(FunctionImplementation.values()).flatMap(type -> Stream.of(
                Arguments.of(type, Integer.MIN_VALUE), Arguments.of(type, -1),
                Arguments.of(type, 0), Arguments.of(type, 1)));
    }

    static Stream<Arguments> invalidIndices() {
        return Stream.of(FunctionImplementation.values()).flatMap(type -> Stream.of(
                Arguments.of(type, Integer.MIN_VALUE), Arguments.of(type, -1),
                Arguments.of(type, 5), Arguments.of(type, Integer.MAX_VALUE)));
    }

    static Stream<Arguments> indices() {
        return Stream.of(FunctionImplementation.values()).flatMap(type ->
                Stream.of(0, 1, 2, 3, 4).map(index -> Arguments.of(type, index)));
    }

    static Stream<Arguments> insertions() {
        return Stream.of(FunctionImplementation.values()).flatMap(type ->
                Stream.of(-3.0, -1.5, -0.5, 0.5, 1.5, 3.0)
                        .map(x -> Arguments.of(type, x)));
    }

    static Stream<Arguments> invalidReplacements() {
        return Stream.of(FunctionImplementation.values()).flatMap(type -> Stream.of(
                Arguments.of(type, 0, -1.0), Arguments.of(type, 0, 3.0),
                Arguments.of(type, 4, 1.0), Arguments.of(type, 4, -3.0),
                Arguments.of(type, 2, -1.0), Arguments.of(type, 2, 1.0),
                Arguments.of(type, 2, -3.0), Arguments.of(type, 2, 3.0),
                Arguments.of(type, 0, Double.NaN), Arguments.of(type, 2, Double.NaN),
                Arguments.of(type, 4, Double.NaN)));
    }

    static Stream<Arguments> outsideArguments() {
        return Stream.of(FunctionImplementation.values()).flatMap(type ->
                Stream.of(-3.0, 3.0, Math.nextDown(-2.0), Math.nextUp(2.0),
                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)
                        .map(x -> Arguments.of(type, x)));
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void countConstructorCreatesEquallySpacedZeroPoints(FunctionImplementation type) {
        TabulatedFunction f = type.create(-2, 2, 5);
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{0, 0, 0, 0, 0});
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void valuesConstructorPreservesCoordinatesAndValues(FunctionImplementation type) {
        assertPoints(type.parabola(), new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void constructorsAcceptMinimumTwoPointsAndFractionalBorders(FunctionImplementation type) {
        assertPoints(type.create(-0.5, 1.5, 2), new double[]{-0.5, 1.5}, new double[]{0, 0});
        assertPoints(type.create(-0.5, 1.5, new double[]{-3, 7}),
                new double[]{-0.5, 1.5}, new double[]{-3, 7});
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void valuesArrayAndFunctionDoNotShareMutableState(FunctionImplementation type) throws Exception {
        double[] values = {4, 1, 0, 1, 4};
        TabulatedFunction f = type.create(-2, 2, values);
        values[0] = 99;
        assertEquals(4, f.getPointY(0));
        f.setPointY(1, 77);
        assertEquals(1, values[1]);
    }

    @ParameterizedTest
    @MethodSource("invalidBounds")
    void bothConstructorsRejectUnorderedOrEqualBorders(FunctionImplementation type, double left, double right) {
        assertThrows(IllegalArgumentException.class, () -> type.create(left, right, 3));
        assertThrows(IllegalArgumentException.class, () -> type.create(left, right, new double[]{1, 2, 3}));
    }

    @ParameterizedTest
    @MethodSource("invalidCounts")
    void countConstructorRejectsFewerThanTwoPoints(FunctionImplementation type, int count) {
        assertThrows(IllegalArgumentException.class, () -> type.create(-2, 2, count));
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void valuesConstructorRejectsEmptySingletonAndNullArray(FunctionImplementation type) {
        assertThrows(IllegalArgumentException.class, () -> type.create(-2, 2, new double[0]));
        assertThrows(IllegalArgumentException.class, () -> type.create(-2, 2, new double[]{1}));
        assertThrows(NullPointerException.class, () -> type.create(-2, 2, (double[]) null));
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void interpolationReturnsStoredValuesAtAllNodes(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        for (int i = 0; i < f.getPointsCount(); i++) {
            assertEquals(f.getPointY(i), f.getFunctionValue(f.getPointX(i)), EPS);
        }
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void interpolationIsLinearBetweenEachPairOfPoints(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        double[] x = {-1.75, -1.5, -0.5, 0.5, 1.5, 1.75};
        double[] y = {3.25, 2.5, 0.5, 0.5, 2.5, 3.25};
        for (int i = 0; i < x.length; i++) assertEquals(y[i], f.getFunctionValue(x[i]), EPS);
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void interpolationHandlesTwoPointAndConstantFunctions(FunctionImplementation type) {
        TabulatedFunction line = type.create(-1, 3, new double[]{5, -3});
        assertEquals(5, line.getFunctionValue(-1), EPS);
        assertEquals(3, line.getFunctionValue(0), EPS);
        assertEquals(1, line.getFunctionValue(1), EPS);
        assertEquals(-3, line.getFunctionValue(3), EPS);
        TabulatedFunction constant = type.create(-2, 2, new double[]{7, 7, 7});
        assertEquals(7, constant.getFunctionValue(-0.25), EPS);
    }

    @ParameterizedTest
    @MethodSource("outsideArguments")
    void valuesOutsideDomainOrAtNaNAreUndefined(FunctionImplementation type, double x) {
        assertTrue(Double.isNaN(type.parabola().getFunctionValue(x)));
    }

    @ParameterizedTest
    @MethodSource("invalidIndices")
    void everyIndexedOperationRejectsInvalidIndexAndPreservesState(FunctionImplementation type, int index) {
        TabulatedFunction f = type.parabola();
        assertAll(
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.getPoint(index)),
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.getPointX(index)),
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.getPointY(index)),
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class,
                        () -> f.setPoint(index, new FunctionPoint(0, 9))),
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.setPointX(index, 0)),
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.setPointY(index, 9)),
                () -> assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.deletePoint(index)));
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @MethodSource("indices")
    void setPointReplacesFirstMiddleAndLastPoints(FunctionImplementation type, int index) throws Exception {
        TabulatedFunction f = type.parabola();
        double[] xs = {-2, -1, 0, 1, 2};
        double[] ys = {4, 1, 0, 1, 4};
        xs[index] += 0.25;
        ys[index] = -9;
        FunctionPoint point = new FunctionPoint(xs[index], ys[index]);
        f.setPoint(index, point);
        assertPoints(f, xs, ys);
        assertEquals(-9, f.getFunctionValue(xs[index]), EPS);
    }

    @ParameterizedTest
    @MethodSource("indices")
    void setPointXPreservesYAndUpdatesDomain(FunctionImplementation type, int index) throws Exception {
        TabulatedFunction f = type.parabola();
        double[] xs = {-2, -1, 0, 1, 2};
        xs[index] += 0.25;
        f.setPointX(index, xs[index]);
        assertPoints(f, xs, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @MethodSource("indices")
    void setPointYPreservesXAndChangesInterpolation(FunctionImplementation type, int index) throws Exception {
        TabulatedFunction f = type.parabola();
        double[] ys = {4, 1, 0, 1, 4};
        ys[index] = -10;
        f.setPointY(index, -10);
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, ys);
        assertEquals(-10, f.getFunctionValue(f.getPointX(index)), EPS);
        int neighbor = index == 4 ? 3 : index + 1;
        double midpoint = (f.getPointX(index) + f.getPointX(neighbor)) / 2;
        assertEquals((ys[index] + ys[neighbor]) / 2, f.getFunctionValue(midpoint), EPS);
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void replacingWithSameXIsAllowed(FunctionImplementation type) throws Exception {
        TabulatedFunction f = type.parabola();
        for (int i = 0; i < 5; i++) {
            f.setPoint(i, new FunctionPoint(i - 2, 10 + i));
            f.setPointX(i, i - 2);
        }
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{10, 11, 12, 13, 14});
    }

    @ParameterizedTest
    @MethodSource("invalidReplacements")
    void unorderedDuplicateAndNaNReplacementsAreRejectedWithoutMutation(
            FunctionImplementation type, int index, double x) {
        TabulatedFunction f = type.parabola();
        assertThrows(InappropriateFunctionPointException.class, () -> f.setPoint(index, new FunctionPoint(x, 99)));
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
        assertThrows(InappropriateFunctionPointException.class, () -> f.setPointX(index, x));
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void nullPointIsRejectedWithoutMutation(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        assertThrows(NullPointerException.class, () -> f.setPoint(2, null));
        assertThrows(NullPointerException.class, () -> f.addPoint(null));
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @MethodSource("insertions")
    void insertionBeforeInsideAndAfterDomainKeepsPointsSorted(FunctionImplementation type, double x) throws Exception {
        TabulatedFunction f = type.parabola();
        TreeMap<Double, Double> expected = snapshot(f);
        f.addPoint(new FunctionPoint(x, 17));
        expected.put(x, 17.0);
        assertMatches(f, expected);
        assertEquals(17, f.getFunctionValue(x), EPS);
    }

    @ParameterizedTest
    @MethodSource("indices")
    void duplicateInsertionAtAnyExistingPointIsRejectedWithoutMutation(FunctionImplementation type, int index) {
        TabulatedFunction f = type.parabola();
        assertThrows(InappropriateFunctionPointException.class,
                () -> f.addPoint(new FunctionPoint(index - 2, 99)));
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @MethodSource("indices")
    void deletingFirstMiddleOrLastPointUpdatesAllAccessors(FunctionImplementation type, int index) {
        TabulatedFunction f = type.parabola();
        TreeMap<Double, Double> expected = snapshot(f);
        expected.remove((double) index - 2);
        f.deletePoint(index);
        assertMatches(f, expected);
        if (index == 0) assertTrue(Double.isNaN(f.getFunctionValue(-2)));
        if (index == 4) assertTrue(Double.isNaN(f.getFunctionValue(2)));
        if (index == 2) assertEquals(1, f.getFunctionValue(0), EPS);
        assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.getPoint(4));
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void deletionStopsAtTwoPointsAndInvalidIndexStillHasPriority(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        f.deletePoint(0);
        f.deletePoint(1);
        f.deletePoint(2);
        assertPoints(f, new double[]{-1, 1}, new double[]{1, 1});
        assertThrows(IllegalStateException.class, () -> f.deletePoint(0));
        assertThrows(IllegalStateException.class, () -> f.deletePoint(1));
        assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.deletePoint(-1));
        assertThrows(FunctionPointIndexOutOfBoundsException.class, () -> f.deletePoint(2));
        assertPoints(f, new double[]{-1, 1}, new double[]{1, 1});
    }

    @ParameterizedTest
    @MethodSource("insertions")
    void insertionAfterDeletionReusesCapacityWithoutLosingPoints(FunctionImplementation type, double x) throws Exception {
        TabulatedFunction f = type.parabola();
        f.deletePoint(2);
        TreeMap<Double, Double> expected = snapshot(f);
        f.addPoint(new FunctionPoint(x, 7));
        expected.put(x, 7.0);
        assertMatches(f, expected);
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void twoPointFunctionSupportsInsertionAndEndpointReplacement(FunctionImplementation type) throws Exception {
        TabulatedFunction f = type.create(0, 2, new double[]{0, 2});
        f.setPoint(0, new FunctionPoint(-1, -1));
        f.setPointX(1, 3);
        f.addPoint(new FunctionPoint(1, 5));
        assertPoints(f, new double[]{-1, 1, 3}, new double[]{-1, 5, 2});
        assertEquals(2, f.getFunctionValue(0), EPS);
        assertEquals(3.5, f.getFunctionValue(2), EPS);
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void nonUniformIntervalsAreInterpolatedAfterMutation(FunctionImplementation type) throws Exception {
        TabulatedFunction f = type.create(0, 4, new double[]{0, 8});
        f.addPoint(new FunctionPoint(1, 6));
        assertEquals(3, f.getFunctionValue(0.5), EPS);
        assertEquals(7, f.getFunctionValue(2.5), EPS);
        f.setPointX(1, 2);
        assertEquals(3, f.getFunctionValue(1), EPS);
        f.deletePoint(1);
        assertEquals(2, f.getFunctionValue(1), EPS);
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void iteratorVisitsEachPointOnceAndThrowsAfterExhaustion(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        Iterator<FunctionPoint> iterator = f.iterator();
        for (int i = 0; i < 5; i++) {
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext(), "hasNext must not advance the iterator");
            FunctionPoint point = iterator.next();
            assertEquals(i - 2, point.getX());
            assertEquals((i - 2) * (i - 2), point.getY());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertThrows(NoSuchElementException.class, iterator::next);
        assertFalse(iterator.hasNext());
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void iteratorsAreIndependentAndNextDoesNotRequireHasNext(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        Iterator<FunctionPoint> first = f.iterator();
        Iterator<FunctionPoint> second = f.iterator();
        assertEquals(-2, first.next().getX());
        assertEquals(-1, first.next().getX());
        assertEquals(-2, second.next().getX());
        assertEquals(0, first.next().getX());
        assertEquals(-1, second.next().getX());
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void iteratorRemovalIsUnsupportedAndDoesNotChangeFunction(FunctionImplementation type) {
        TabulatedFunction f = type.parabola();
        Iterator<FunctionPoint> iterator = f.iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertPoints(f, new double[]{-2, -1, 0, 1, 2}, new double[]{4, 1, 0, 1, 4});
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void iteratorForEachRemainingStartsAtCurrentPosition(FunctionImplementation type) {
        Iterator<FunctionPoint> iterator = type.parabola().iterator();
        iterator.next();
        List<Double> xs = new ArrayList<>();
        iterator.forEachRemaining(point -> xs.add(point.getX()));
        assertEquals(List.of(-1.0, 0.0, 1.0, 2.0), xs);
        iterator.forEachRemaining(point -> fail("Iterator is exhausted"));
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @ParameterizedTest
    @EnumSource(FunctionImplementation.class)
    void deterministicMutationSequenceMatchesIndependentSortedModel(FunctionImplementation type) throws Exception {
        TabulatedFunction f = type.parabola();
        TreeMap<Double, Double> expected = snapshot(f);
        Random random = new Random(20261019);
        for (int operation = 0; operation < 300; operation++) {
            int index = random.nextInt(expected.size());
            double key = new ArrayList<>(expected.keySet()).get(index);
            switch (operation % 4) {
                case 0 -> {
                    double x = random.nextInt(1000) / 8.0 - 60;
                    if (!expected.containsKey(x)) {
                        double y = random.nextInt(100) - 50;
                        f.addPoint(new FunctionPoint(x, y));
                        expected.put(x, y);
                    }
                }
                case 1 -> {
                    double y = random.nextInt(100) - 50;
                    f.setPointY(index, y);
                    expected.put(key, y);
                }
                case 2 -> {
                    if (expected.size() > 2) {
                        f.deletePoint(index);
                        expected.remove(key);
                    }
                }
                case 3 -> {
                    double y = random.nextInt(100) - 50;
                    f.setPoint(index, new FunctionPoint(key, y));
                    expected.put(key, y);
                }
            }
            assertMatches(f, expected);
            List<Double> xs = new ArrayList<>(expected.keySet());
            for (int i = 1; i < xs.size(); i++) {
                double a = xs.get(i - 1);
                double b = xs.get(i);
                assertEquals((expected.get(a) + expected.get(b)) / 2,
                        f.getFunctionValue((a + b) / 2), 1e-9);
            }
        }
    }

    static void assertPoints(TabulatedFunction f, double[] xs, double[] ys) {
        assertEquals(xs.length, f.getPointsCount());
        assertEquals(xs[0], f.getLeftDomainBorder());
        assertEquals(xs[xs.length - 1], f.getRightDomainBorder());
        Iterator<FunctionPoint> iterator = f.iterator();
        for (int i = 0; i < xs.length; i++) {
            assertEquals(xs[i], f.getPointX(i), "x at index " + i);
            assertEquals(ys[i], f.getPointY(i), "y at index " + i);
            FunctionPoint point = f.getPoint(i);
            assertEquals(xs[i], point.getX());
            assertEquals(ys[i], point.getY());
            assertTrue(iterator.hasNext());
            FunctionPoint iterated = iterator.next();
            assertEquals(xs[i], iterated.getX());
            assertEquals(ys[i], iterated.getY());
        }
        assertFalse(iterator.hasNext());
    }

    private static TreeMap<Double, Double> snapshot(TabulatedFunction f) {
        TreeMap<Double, Double> result = new TreeMap<>();
        for (FunctionPoint point : f) result.put(point.getX(), point.getY());
        return result;
    }

    private static void assertMatches(TabulatedFunction f, TreeMap<Double, Double> expected) {
        assertPoints(f, expected.keySet().stream().mapToDouble(Double::doubleValue).toArray(),
                expected.values().stream().mapToDouble(Double::doubleValue).toArray());
    }
}
