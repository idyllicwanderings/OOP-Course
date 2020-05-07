import Derivative;
import Function;

import java.math.BigInteger;
import java.util.Arrays;
/*without const*/

public class SimpTerm implements Derivative, Cloneable {

    private static final int dimension = Function.values().length;
    private BigInteger[] idx = new BigInteger[dimension];

    public SimpTerm(BigInteger... integers) {
        assert integers.length == dimension;
        this.idx = integers.clone();
    }

    public SimpTerm() {
        for (int i = 0; i < dimension; i++) {
            this.idx[i] = BigInteger.ZERO;
        }
    }

    public BigInteger getPowrIdx() {
        return idx[0];
    }

    public BigInteger getSinIdx() {
        return idx[1];
    }

    public BigInteger getCosIdx() {
        return idx[2];
    }

    public BigInteger[] getIdx() {
        return idx;
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public Derivative derive() {
        return null;
    }

    @Override
    public int hashCode() {
        StringBuilder ret = new StringBuilder();
        ret.append(idx[Function.X.ordinal()]);
        ret.append("%");
        ret.append(idx[Function.SIN.ordinal()]);
        ret.append("%");
        ret.append(idx[Function.COS.ordinal()]);
        ret.append("%");
        return ret.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpTerm) {
            SimpTerm term = (SimpTerm) obj;
            return Arrays.equals(this.idx, term.idx);
        } else {
            return false;
        }
    }

    @Override
    public SimpTerm clone() {
        return new SimpTerm(idx.clone());
    }
}
