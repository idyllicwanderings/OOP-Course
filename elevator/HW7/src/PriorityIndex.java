public class PriorityIndex implements Comparable<PriorityIndex> {
    private int eleNum;
    private int reqNum;

    public PriorityIndex(int eleNum,int reqNum) {
        this.reqNum = reqNum;
        this.eleNum = eleNum;
    }

    public int getEleNum() {
        return eleNum;
    }

    public int getreqNum() {
        return reqNum;
    }

    @Override
    public int compareTo(PriorityIndex o) {
        return Integer.compare(reqNum,o.reqNum);
    }

}
