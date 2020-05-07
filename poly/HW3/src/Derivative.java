public interface Derivative extends Cloneable {

    public boolean isZero();

    public Derivative derive();

    public Derivative mult(Derivative derivative);

    public Derivative negate();

    public boolean canAbbr();

    public Derivative clone();

}
