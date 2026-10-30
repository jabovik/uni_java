package functions.meta;

public class Scale implements functions.Function {

    private functions.Function func;
    private double scaleX, scaleY;

    public Scale(functions.Function func, double scaleX, double scaleY){
        if(Math.abs(scaleX)<1e-9){
            throw new IllegalArgumentException("X scale cannot be zero");
        }
        this.func = func;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        return Math.min(
            func.getLeftDomainBorder() * scaleX,
            func.getRightDomainBorder() * scaleX
        ); // negative scale reverses borders
    }

    @Override
    public double getRightDomainBorder() {
        return Math.max(
            func.getLeftDomainBorder() * scaleX,
            func.getRightDomainBorder() * scaleX
        );
    }

    @Override
    public double getFunctionValue(double x) {
        return func.getFunctionValue(x / scaleX) * scaleY;
    }

    
}
