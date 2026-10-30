package functions.basic;

public class Log implements functions.Function {
    private double base;

    public Log(){
        this.base = Math.E;
    }

    public Log(double base){
        this.base = base;
    }
    @Override
    public double getLeftDomainBorder() {
        return 0.0;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        if(x<= getLeftDomainBorder() || x> getRightDomainBorder()){
            return Double.NaN;
        }
        return Math.log(x)/Math.log(base);
    }
}
