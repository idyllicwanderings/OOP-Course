import java.math.BigInteger;
import java.util.Objects;

public class Tri extends BasicFunction implements Cloneable {

    private Expression expr;
    private Function funcType;

    public Tri(Expression expr,Function funcType) {
        this.expr = expr.clone();
        this.funcType = funcType;
    }

    @Override
    public boolean isZero() {
        return funcType.equals(Function.SIN) && expr.isZero();
    }

    @Override
    public Term derive() {
        assert funcType.equals(Function.SIN) || funcType.equals(Function.COS);
        if (funcType.equals(Function.SIN)) {
            return new Term(expr.derive(),
                    new Factor(BigInteger.ONE,new Tri(expr,Function.COS)));
        }
        else {
            return new Term(expr.derive(),
                    new Factor(BigInteger.ONE,new Tri(expr,Function.SIN)),
                    new Factor(BigInteger.ONE,new Const(BigInteger.ONE.negate())));
        }
    }

    @Override
    public Derivative mult(Derivative derivative) {
        return super.mult(derivative);
    }

    @Override
    public Derivative negate() {
        return new Term(new Factor(BigInteger.ONE.negate()),
                new Factor(this.clone()));
    }

    @Override
    public boolean canAbbr() {
        return ((expr.isZero()) && funcType.equals(Function.COS));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (funcType.equals(Function.SIN)) {
            ret.append("sin(").append(expr.setNested(!expr.isFactor()).toString()).append(")");
        }
        else {
            ret.append("cos(").append(expr.setNested(!expr.isFactor()).toString()).append(")");
        }
        return ret.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Tri && ((Tri) obj).expr.equals(expr)
                && funcType.equals(((Tri) obj).funcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr,funcType);
    }

    public Tri clone() {
        return new Tri(expr.clone(),funcType);
    }

    public Function getFuncType() {
        return funcType;
    }

}
