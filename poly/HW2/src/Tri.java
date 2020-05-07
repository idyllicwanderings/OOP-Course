import java.math.BigInteger;

public class Tri extends BasicFunction {

    public Tri(Function function, BigInteger integer) {
        super(function, integer);
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public Derivative derive() {
        //unfinished
        return null;
    }

}
