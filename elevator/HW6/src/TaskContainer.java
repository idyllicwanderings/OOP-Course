import com.oocourse.elevator2.PersonRequest;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

public class TaskContainer {

    private volatile TreeMap<Integer, Vector<Task>> tasks = new TreeMap<>();

    public TaskContainer() {

    }

    public void addWaitingTask(Task task) {
        int dstFloor = task.getDstFloor();
        if (!tasks.containsKey(dstFloor)) {
            tasks.put(dstFloor, new Vector<>());
        }
        tasks.get(dstFloor).add(task);
    }

    public void addTask(PersonRequest request) {
        int pid = request.getPersonId();
        int dstFloor1 = request.getFromFloor();
        int dstFloor2 = request.getToFloor();
        if (!tasks.containsKey(dstFloor2)) {
            tasks.put(dstFloor2, new Vector<>());
        }
        Task getOff = new Task(pid, dstFloor2, false, null);
        tasks.get(dstFloor2).add(getOff);
        if (!tasks.containsKey(dstFloor1)) {
            tasks.put(dstFloor1, new Vector<>());
        }
        tasks.get(dstFloor1).add(new Task(pid, dstFloor1, true, getOff));

    }

    public boolean isEnd() {
        return tasks.isEmpty();
    }

    public Vector<Task> getCurrentTasks(int curFloor) {
        Vector<Task> ret = new Vector<>();
        Vector<Task> unSimulatedTasks = new Vector<>();
        if (tasks.containsKey(curFloor)) {
            ret = tasks.get(curFloor);
            for (Iterator<Task> iter = ret.iterator(); iter.hasNext(); ) {
                Task task = iter.next();
                if (!task.isMarching()) {
                    unSimulatedTasks.add(task);
                    iter.remove();
                } else if (task.getRelevantTask() != null) {
                    Task relevantTask = task.getRelevantTask();
                    relevantTask.setMarching();
                }
            }
            if (!unSimulatedTasks.isEmpty()) {
                tasks.put(curFloor, unSimulatedTasks);
            } else {
                tasks.remove(curFloor);
            }
        }
        return ret;
    }

    public State getDirection(int curFloor, State curState) {
        int min = tasks.firstKey();
        int max = tasks.lastKey();
        boolean isStill = curState.equals(State.STILL);
        if (min < curFloor && (curState.equals(State.DOWN) || isStill)) {
            return State.DOWN;
        } else if (max > curFloor && (curState.equals(State.UP) || isStill)) {
            return State.UP;
        } else if (min < curFloor && (curState.equals(State.UP) || isStill)) {
            return State.DOWN;
        } else if (max > curFloor && (curState.equals(State.DOWN) || isStill)) {
            return State.UP;
        } else {
            assert curState.equals(State.STILL);
            return State.STILL;
        }
    }

    public int size() {
        return tasks.size();
    }

}
