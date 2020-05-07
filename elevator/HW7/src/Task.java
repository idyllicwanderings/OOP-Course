import java.util.Objects;

public class Task {

    private boolean isMarching;
    private boolean isGetIn;
    private int dstFloor;
    private int pid;
    private Task relevantTask;

    public Task(int pid, int dstFloor, boolean isGetIn,boolean isActive, Task relevantTask) {
        this.pid = pid;
        this.dstFloor = dstFloor;
        this.isGetIn = isGetIn;
        this.isMarching = isActive;
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

    public void setRelevantActive() {
        if (relevantTask != null) {
            relevantTask.setMarching();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Task && pid == ((Task) obj).pid
                && dstFloor == ((Task) obj).dstFloor
                && isGetIn == ((Task) obj).isGetIn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid,dstFloor,isGetIn);
    }
}
