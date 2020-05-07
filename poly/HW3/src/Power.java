import java.math.BigInteger;
import java.util.Objects;

public class Power extends BasicFunction implements Cloneable {

    private static final String var = "x";

    public Power() {
        super();
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public Factor derive() {
        return new Factor(new Const(BigInteger.ONE));
    }

    @Override
    public Derivative mult(Derivative derivative) {
        return super.mult(derivative);
    }

    @Override
    public Derivative negate() {
        return new Term(new Factor(BigInteger.ONE.negate()),
                new Factor(new Power()));
    }

    @Override
    public boolean canAbbr() {
        return false;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Power;
    }

    public Power clone() {
        return new Power();
    }

    public Function getFuncType() {
        return Function.X;
    }

}
