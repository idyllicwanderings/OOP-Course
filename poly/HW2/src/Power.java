import java.math.BigInteger;

public class Power extends BasicFunction {

    public Power(Function function, BigInteger integer) {
        super(function, integer);
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public Derivative derive() {
        return null;
    }

}
