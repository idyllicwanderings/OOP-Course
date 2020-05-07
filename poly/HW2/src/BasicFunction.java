import java.math.BigInteger;

public abstract class BasicFunction implements Derivative {

    private Function type;
    private BigInteger idx;

    public BasicFunction(Function function, BigInteger integer) {
        this.type = function;
        this.idx = integer;
    }

    public BigInteger getIdx() {
        return this.idx;
    }

    public Function getFunctionType() {
        return this.type;
    }

    @Override
    public boolean isZero() {
        return false;
    }

}
