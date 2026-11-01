package functions.meta;

public class Shift implements functions.Function {

    private functions.Function func;
    private double shiftX, shiftY;

    public Shift(functions.Function func, double shiftX, double shiftY)
    {
        this.func = func;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public double getLeftDomainBorder() {
        return func.getLeftDomainBorder() + shiftX;
    }

    @Override
    public double getRightDomainBorder() {
        return func.getRightDomainBorder() + shiftX;
    }

    @Override
    public double getFunctionValue(double x) {
        return func.getFunctionValue(x-shiftX) + shiftY;
    }

}
