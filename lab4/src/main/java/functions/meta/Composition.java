package functions.meta;

public class Composition implements functions.Function {

    private functions.Function first,second;
    
    public Composition(functions.Function first, functions.Function second){
        this.first = first;
        this.second = second;
    }

    @Override
    public double getLeftDomainBorder() {
        return first.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return first.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        return first.getFunctionValue(second.getFunctionValue(x));
    }
}
