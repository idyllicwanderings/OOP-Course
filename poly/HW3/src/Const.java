import java.math.BigInteger;
import java.util.Objects;

public class Const extends BasicFunction implements Cloneable {

    private BigInteger coef;

    public Const(BigInteger coef) {
        this.coef = coef;
    }

    public Const(String str) {
        this.coef = new BigInteger(str);
    }

    public Factor negate() {
        return new Factor(coef.negate());
    }

    @Override
    public boolean canAbbr() {
        return coef.abs().equals(BigInteger.ONE);
    }

    public boolean isStd() {
        return coef.equals(BigInteger.ONE);
    }

    public BigInteger getCoef() {
        return coef;
    }

    public int signum() {
        return coef.signum();
    }

    @Override
    public boolean isZero() {
        return coef.equals(BigInteger.ZERO);
    }

    @Override
    public Factor derive() {
        return new Factor(new Const("0"));
    }

    @Override
    public Derivative mult(Derivative derivative) {
        if (derivative instanceof Const) {
            return new Const(((Const) derivative).coef.multiply(coef));
        }
        else {
            return super.mult(derivative);
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (coef.signum() < 0) {
            ret.append("-");
        }
        else {
            ret.append("+");
        }
        if (!coef.abs().equals(BigInteger.ONE)) {
            ret.append(coef.abs().toString());
        }
        return ret.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Const && ((Const) obj).coef.equals(coef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coef);
    }

    public Const clone() {
        return new Const(coef);
    }

    public Function getFuncType() {
        return Function.CONST;
    }

}
