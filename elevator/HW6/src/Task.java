public class Task {

    private boolean isMarching;
    private boolean isGetIn;
    private int dstFloor;
    private int pid;
    private Task relevantTask;

    public Task(int pid, int dstFloor, boolean isGetIn, Task relevantTask) {
        this.pid = pid;
        this.dstFloor = dstFloor;
        this.isGetIn = isGetIn;
        this.isMarching = isGetIn;
        this.relevantTask = relevantTask;
    }

    public boolean isGetIn() {
        return isGetIn;
    }

    public int getDstFloor() {
        return dstFloor;
    }

    public int getId() {
        return pid;
    }

    public boolean isMarching() {
        return isMarching;
    }

    public Task setMarching() {
        isMarching = true;
        return this;
    }

    public Task setUnMarching() {
        isMarching = false;
        return this;
    }

    public Task getRelevantTask() {
        return relevantTask;
    }

}
