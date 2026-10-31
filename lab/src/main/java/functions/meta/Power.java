package functions.meta;

public class Power implements functions.Function {

    private functions.Function func;
    private double power;

    public Power(functions.Function func, double power) {
        this.func = func;
        this.power = power;
    }

    @Override
    public double getLeftDomainBorder() {
        return func.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return func.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        return Math.pow(func.getFunctionValue(x), power);
    }

}
