package expression;

import java.math.BigInteger;
import java.util.Objects;

public class PolyItem implements Comparable<PolyItem>, Cloneable {

    private BigInteger coef;
    private BigInteger idx;

    public PolyItem(BigInteger coef, BigInteger idx) {
        this.coef = coef;
        this.idx = idx;
    }

    public boolean isPos() {
        return coef.compareTo(BigInteger.ZERO) >= 0;
    }

    private boolean isConst() {
        return idx.equals(BigInteger.ZERO);
    }

    public boolean isZero() {
        return coef.equals(BigInteger.ZERO);
    }

    private String simplifyNumber(BigInteger integer) {
        String coefString;
        if (integer.signum() >= 0) {
            coefString = integer.abs().toString();
        } else {
            coefString = "-" + integer.abs().toString();
        }
        return coefString;
    }

    public BigInteger getCoef() {
        return this.coef;
    }

    public BigInteger getIdx() {
        return this.idx;
    }

    public PolyItem add(PolyItem polyItem) {
        assert polyItem.idx.equals(this.idx);
        this.coef = this.coef.add(polyItem.coef);
        return clone();
    }

    public PolyItem derive() {
        if (isConst()) {
            return new PolyItem(BigInteger.ZERO, BigInteger.ZERO);
        }
        return new PolyItem(coef.multiply(idx), idx.subtract(BigInteger.ONE));
    }

    @Override
    public String toString() {
        //System.out.println(coef + " ???  "+idx);
        if (coef.equals(BigInteger.ZERO)) {
            return "";
        }

        String coefString = simplifyNumber(coef);
        if (isConst()) {
            return coefString;
        }

        if (coef.equals(BigInteger.ONE) && idx.equals(BigInteger.ONE)) {
            return "x";
        }

        if (coef.equals(BigInteger.ONE.negate())
                && idx.equals(BigInteger.ONE)) {
            return "-x";
        }

        String idxString = simplifyNumber(idx);

        if (coef.equals(BigInteger.ONE)) {
            return "x**" + idxString;
        }

        if (coef.equals(BigInteger.ONE.negate())) {
            return "-x**" + idxString;
        }

        if (idx.equals(BigInteger.ONE)) {
            return coefString + "*x";
        }
        return coefString + "*x**" + idxString;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PolyItem) {
            PolyItem tmp = (PolyItem) o;
            return tmp.getCoef().equals(coef) && tmp.getIdx().equals(idx);
        }
        return false;
    }

    public boolean equivalent(Object o) {
        if (o instanceof PolyItem) {
            PolyItem tmp = (PolyItem) o;
            return tmp.getIdx().equals(idx);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // System.out.println(coef + " " + idx + " " +Objects.hash(coef,idx));
        return Objects.hash(coef,idx);
    }

    @Override
    public int compareTo(PolyItem o) {
        if (o.getCoef().compareTo(this.getCoef()) != 0) {
            return o.getCoef().compareTo(this.getCoef());
        }
        return o.getIdx().compareTo(this.getIdx());
    }

    public PolyItem clone() {
        return new PolyItem(this.coef, this.idx);
    }
}
