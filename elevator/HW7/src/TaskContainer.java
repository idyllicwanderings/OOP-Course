import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class TaskContainer {

    private volatile TreeMap<Integer, ArrayList<Task>> tasks = new TreeMap<>();

    public TaskContainer() {

    }

    public void addWaitingTask(Task task) {
        int dstFloor = task.getDstFloor();
        if (!tasks.containsKey(dstFloor)) {
            tasks.put(dstFloor, new ArrayList<>());
        }
        tasks.get(dstFloor).add(task);
    }

    public void addTask(PersonRequest request) {
        int pid = request.getPersonId();
        int dstFloor1 = request.getFromFloor();
        int dstFloor2 = request.getToFloor();
        if (!tasks.containsKey(dstFloor2)) {
            tasks.put(dstFloor2, new ArrayList<>());
        }
        Task getOff = new Task(pid, dstFloor2, false, false, null);
        tasks.get(dstFloor2).add(getOff);
        if (!tasks.containsKey(dstFloor1)) {
            tasks.put(dstFloor1, new ArrayList<>());
        }
        tasks.get(dstFloor1).add(new Task(pid, dstFloor1, true, true, getOff));
    }

    public boolean isEnd() {
        return tasks.isEmpty();
    }

    private synchronized void releaseTask() {

    }

    public ArrayList<Task> getCurrentTasks(int curFloor,int pasNum,int maxLoad) {
        int pas = pasNum;
        ArrayList<Task> ret = new ArrayList<>();
        ArrayList<Task> unSimulatedTasks = new ArrayList<>();
        if (tasks.containsKey(curFloor)) {
            ret = tasks.get(curFloor);
            for (Iterator<Task> iter = ret.iterator(); iter.hasNext(); ) {
                Task task = iter.next();
                if (!task.isMarching()) {
                    unSimulatedTasks.add(task);
                    iter.remove();
                }
            }
            for (Iterator<Task> iter = ret.iterator();iter.hasNext();) {
                Task task = iter.next();
                if (!task.isGetIn()) {
                    pas--;
                }
            }
            for (Iterator<Task> iter = ret.iterator();iter.hasNext();) {
                Task task = iter.next();
                if (task.isGetIn()) {
                    if (pas >= maxLoad) {
                        iter.remove();
                        unSimulatedTasks.add(task);
                    }
                    pas++;
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

    private boolean hasActiveTask(int curFloor) {
        if (tasks.containsKey(curFloor)) {
            ArrayList<Task> curFloorTasks = tasks.get(curFloor);
            for (Task j : curFloorTasks) {
                if (j.isMarching()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasOnTask(int curFloor) {
        if (tasks.containsKey(curFloor)) {
            ArrayList<Task> curFloorTasks = tasks.get(curFloor);
            for (Task j : curFloorTasks) {
                if (j.isGetIn()) {
                    return true;
                }
            }
        }
        return false;
    }

    public State getDirection(int curFloor, State curState) {
        //change it
        //先寻找激活的getOn/getOff
        //再寻找未激活的getOn。getOff
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (Integer i: tasks.descendingKeySet()) {
            if (hasActiveTask(i)) {
                max = i;
                break;
            }
        }

        for (Integer i: tasks.keySet()) {
            boolean hasActive = hasActiveTask(i);
            if (hasActive) {
                min = i;
                break;
            }
        }

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
