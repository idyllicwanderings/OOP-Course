import java.math.BigInteger;
import java.util.Objects;

public class Factor implements Derivative {

    private BigInteger idx;
    private BasicFunction func;

    public Factor() {
        this.idx = BigInteger.ONE;
    }

    public Factor(BasicFunction func) {
        this.idx = BigInteger.ONE;
        this.func = func.clone();
        setZero();
    }

    public Factor(BigInteger coef,BasicFunction func) {
        this.idx = coef;
        this.func = func.clone();
        setZero();
    }

    public Factor(BigInteger coef) {
        /*update a new const*/
        this.idx = BigInteger.ONE;
        this.func = new Const(coef);
        setZero();
    }

    private void setZero() {
        if (func.isZero()) {
            func = new Const("0");
        }
        if (idx.equals(BigInteger.ZERO)) {
            func = new Const("1");
        }
    }

    public BasicFunction getFunc() {
        return func;
    }

    public BigInteger getIdx() {
        return idx;
    }

    public void setIdx(BigInteger idx) {
        this.idx = idx;
    }

    public Term negate() {
        return new Term(new Factor(new BigInteger("-1")),this);
    }

    private boolean isConst() {
        return func.getFuncType().equals(Function.CONST);
    }

    protected Function getFuncType() {
        return func.getFuncType();
    }

    public Factor clone() {
        return new Factor(this.idx,func);
    }

    boolean isStandard() {
        return func.equals(new Power()) && idx.equals(new BigInteger("1"));
    }

    boolean isMergable(Factor factor) {
        return func.equals(factor.func) || canAbbr() || factor.canAbbr();
    }

    @Override
    public boolean isZero() {
        return func instanceof Const && func.isZero();
    }

    @Override
    public Derivative derive() {
        Factor con = new Factor(BigInteger.ONE,new Const(this.idx));
        //System.out.println(con.toString());
        Factor sub = new Factor(this.idx.subtract(BigInteger.ONE),func);
        //System.out.println(sub.toString());
        Derivative der = func.derive();
        //System.out.println(der.toString());
        return new Term(con,sub,der);
    }

    @Override
    public Factor mult(Derivative derivative) {
        if (derivative instanceof Factor) {
            Factor factor = (Factor) derivative;
            if (factor.isZero() || isZero()) {
                return new Factor(new Const(BigInteger.ZERO));
            }
            if (factor.canAbbr()) {
                return this;
            }
            if (canAbbr()) {
                return factor;
            }
            if (isConst()) {
                return new Factor(BigInteger.ONE,
                        (Const)func.mult(factor.func));
            }
            return new Factor(idx.add((factor).getIdx()),
                        func);
        }
        else {
            throw new ClassCastException("Expected Class Factor in mult(), Factor.java!");
        }
    }

    @Override
    public boolean canAbbr() {
        return idx.equals(BigInteger.ZERO) || func.equals(new Const("1"));
    }

    protected boolean canAbsAbbr() {
        return canAbbr() || func.equals(new Const("-1"));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (isZero()) {
            return "";
        }
        ret.append(func.toString());
        if (!idx.equals(BigInteger.ONE)) {
            ret.append("**");
            if (idx.signum() < 0) {
                ret.append("-");
            }
            ret.append(idx.abs().toString());
        }
        return ret.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Factor && idx.equals(((Factor) obj).idx)
                && func.equals(((Factor) obj).func);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idx,func);
    }

}
