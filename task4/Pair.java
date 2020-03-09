public class Pair implements Comparable<Pair> {

    private Integer xa;
    private Integer xb;

    public Pair(int a, int b) {
        xa = a;
        xb = b;
    }

    public int getXa() {
        return xa;
    }

    public int getXb() {
        return xb;
    }

    public void setXa(int xa) {
        this.xa = xa;
    }

    public void setXb(int xb) {
        this.xb = xb;
    }

    @Override
    public int compareTo(Pair o) {
        if (xa.compareTo(o.xa) == 0) {
            return xb.compareTo(o.xb);
        }
        return xa.compareTo(o.xa);
    }

    @Override
    public String toString() {
        return xa  + "," + xb;
    }
}
