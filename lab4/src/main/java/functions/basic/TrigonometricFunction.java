package functions.basic;

public abstract class TrigonometricFunction implements functions.Function {

    @Override
    public double getLeftDomainBorder() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public abstract double getFunctionValue(double x);
    
}
