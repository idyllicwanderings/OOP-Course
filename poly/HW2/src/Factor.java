import Function;

import java.math.BigInteger;

public class Factor {

    private BigInteger coef;
    private static final int dimension = Function.values().length;
    private BigInteger[] idx = new BigInteger[dimension];

    public Factor(BigInteger coef, BigInteger... integers) {
        this.coef = coef;
        this.idx = integers.clone();
    }

    public Factor() {
        this.coef = BigInteger.ONE;
        for (int i = 0; i < dimension; i++) {
            this.idx[i] = BigInteger.ZERO;
        }
    }

    public void setCoef(BigInteger coef) {
        this.coef = coef;
    }

    public void setIdx(Function function, BigInteger integer) {
        this.idx[function.ordinal()] = integer;
    }

    public Factor negate() {
        this.coef = this.coef.negate();
        return this;
    }

    public void multiply(Factor factor) {
        this.coef = this.coef.multiply(factor.coef);
        for (int i = 0; i < dimension; i++) {
            this.idx[i] = this.idx[i].add(factor.idx[i]);
        }
    }

    public Term toTerm() {
        return new Term(this.coef, this.idx);
    }

}
