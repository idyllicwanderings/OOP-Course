import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/* CONST * X^A * SIN(X) ^ B * COS(X)^C */
public class Term implements Derivative, Comparable<Term> {

    private BigInteger coef;
    private static final int dimension = Function.values().length;
    private BigInteger[] idx = new BigInteger[dimension];

    public Term(BigInteger coef, BigInteger... integers) {
        assert integers.length == dimension;
        this.coef = coef;
        this.idx = integers.clone();
    }

    public Term() {
        this.coef = BigInteger.ONE;
        for (int i = 0; i < dimension; i++) {
            this.idx[i] = BigInteger.ZERO;
        }
    }

    public BigInteger getCoef() {
        return coef;
    }

    public BigInteger[] getIdx() {
        return idx;
    }

    public BigInteger getPowrIdx() {
        return idx[Function.X.ordinal()];
    }

    public BigInteger getSinIdx() {
        return idx[Function.SIN.ordinal()];
    }

    public BigInteger getCosIdx() {
        return idx[Function.COS.ordinal()];
    }

    @Override
    public boolean isZero() {
        return coef.equals(BigInteger.ZERO);
    }

    @Override
    public Expression derive() {
        Expression re = new Expression();
        for (int i = 0; i < dimension; i++) {
            Term newTerm = new Term();
            newTerm.idx = this.idx.clone();
            newTerm.coef = this.coef.multiply(idx[i]);
            newTerm.idx[i] = this.idx[i].subtract(BigInteger.ONE);
            if (i == 1) {
                newTerm.idx[2] =
                        newTerm.idx[2].add(BigInteger.ONE);
            } else if (i == 2) {
                newTerm.idx[1] =
                        newTerm.idx[1].add(BigInteger.ONE);
                //System.out.println(newTerm.coef.negate());
                newTerm.coef = newTerm.coef.negate();
            } else {
                ;
            }
            re.add(newTerm);
        }
        return re;
    }

    public void add(Derivative derivative) {
        assert derivative instanceof Term;
        Term term = (Term) derivative;
        assert equivalent(term);
        this.coef = this.coef.add(term.coef);
    }

    public Term negate() {
        this.coef = this.coef.negate();
        return this;
    }

    public void setIdx(Function function, BigInteger integer) {
        this.idx[function.ordinal()] = integer;
    }

    @Override

    public String toString() {
        /*Warning: output the ABS of coef!*/
        StringBuilder str = new StringBuilder();
        if (coef.equals(BigInteger.ZERO)) {
            return "";
        }
        boolean isAbbr = false;
        boolean otherFactor = false;
        for (int i = 0; i < dimension; i++) {
            if (!idx[i].equals(BigInteger.ZERO)) {
                otherFactor = true;
            }
        }
        /*if (coef.signum() < 0){
            str.append("-");
        }*/
        if (otherFactor) {
            if (!coef.abs().equals(BigInteger.ONE)) {
                str.append(coef.abs().toString());
            } else {
                isAbbr = true;
            }
        } else {
            str.append(coef.abs().toString());
            return str.toString();
        }

        boolean isFirst = true;
        for (int i = 0; i < dimension; i++) {
            if (idx[i].equals(BigInteger.ZERO)) {
                continue;
            }
            if (!isAbbr || !isFirst) {
                str.append("*");
            }

            if (i == Function.X.ordinal()) {
                str.append("x");
            } else if (i == Function.SIN.ordinal()) {
                str.append("sin(x)");
            } else if (i == Function.COS.ordinal()) {
                str.append("cos(x)");
            }

            isFirst = false;

            if (!idx[i].equals(BigInteger.ONE)) {
                str.append("**");
                if (idx[i].signum() < 0) {
                    str.append("-");
                }
                str.append(idx[i].abs().toString());
            }
        }

        //System.err.println(str.toString());
        return str.toString();
    }

    public boolean equivalent(Term term) {
        return Arrays.equals(this.idx, term.idx);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Term) {
            Term term = (Term) obj;
            return this.coef.equals(term.coef) && equivalent(term);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.coef, this.idx);
    }

    @Override
    public int compareTo(Term o) {
        /*unfinished*/
        return coef.compareTo(o.coef);
    }
}
