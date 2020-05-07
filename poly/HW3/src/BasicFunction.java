import java.math.BigInteger;

public abstract class BasicFunction implements Derivative {

    private Function type;

    public BasicFunction() {
        ;
    }

    public BasicFunction(Function function,BigInteger idx) {
        this.type = function;
    }

    @Override
    public Derivative mult(Derivative derivative) {
        return new Factor(this).mult(derivative);
    }

    public abstract BasicFunction clone();

    public abstract Function getFuncType();

}
