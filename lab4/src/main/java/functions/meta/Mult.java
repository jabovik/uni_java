package functions.meta;

public class Mult implements functions.Function {
    private functions.Function first;
    private functions.Function second;

    public Mult(functions.Function first, functions.Function second){
        this.first = first;
        this.second = second;
    }

    @Override
    public double getLeftDomainBorder() {
        return Math.max(first.getLeftDomainBorder(), second.getLeftDomainBorder());
    }

    @Override
    public double getRightDomainBorder() {
        return Math.min(first.getRightDomainBorder(), second.getRightDomainBorder());
    }

    @Override
    public double getFunctionValue(double x) {
        return first.getFunctionValue(x) * second.getFunctionValue(x);
    }
}
