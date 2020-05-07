import com.oocourse.TimableOutput;
import com.oocourse.elevator2.PersonRequest;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Elevator implements Runnable {

    private static final int maxFloor = 16;
    private static final int minFloor = -3;
    private static final int maxLoad = 7;

    private String name;
    private long openDoorTime;
    private long closeDoorTime;
    private long perFloorTime;
    private int stillFloor;
    private volatile TaskContainer people = new TaskContainer();
    private State curState;
    private int curFloor;
    private int pasNum = 0;
    private int reqNum = 0;
    private AtomicBoolean stopFlag;

    public Elevator(String name,long openDoorTime, long closeDoorTime,
                    long perFloorTime,int stillFloor, AtomicBoolean stopFlag) {
        this.name = name;
        this.openDoorTime = openDoorTime;
        this.closeDoorTime = closeDoorTime;
        this.perFloorTime = perFloorTime;
        this.stillFloor = stillFloor;
        curFloor = 1;
        curState = State.STILL;
        this.stopFlag = stopFlag;
    }

    @Override
    public void run() {
        out: while (true) {
            synchronized (this) {
                try {
                    Vector<Task> tasks = people.getCurrentTasks(curFloor);
                    if (!tasks.isEmpty()) {
                        openDoor();
                        finishTasks(tasks);
                        closeDoor();
                    }
                    if (people.isEnd()) {
                        //System.err.println(name + "-END!");
                        curState = State.STILL;
                        break out;
                        /*if (stopFlag.compareAndSet(false,true)
                                || stopFlag.compareAndSet(true,true)
                                || curFloor == stillFloor) {
                            System.err.println(name + "-END!");
                            curState = State.STILL;
                            break out;
                        }
                        if (curFloor > stillFloor) {
                            moveDownFloor();
                            curState = State.DOWN;
                            continue;
                        }
                        else if (curFloor < stillFloor) {
                            moveUpFloor();
                            curState = State.UP;
                            continue;
                        }
                        //System.out.println(name);
                        break out;*/
                    }
                    curState = people.getDirection(curFloor, curState);
                    if (curState.equals(State.UP)) {
                        moveUpFloor();
                    } else if (curState.equals(State.DOWN)) {
                        moveDownFloor();
                    } else {
                        ;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void finishTasks(Vector<Task> tasks) throws InterruptedException {
        Vector<Task> inTask = new Vector<>();
        for (Task task : tasks) {
            if (!task.isGetIn()) {
                pasNum--;
                reqNum--;
                TimableOutput.println(
                        String.format("OUT-%d-%d-%s", task.getId(), curFloor,name));
            }
            else {
                inTask.add(task);
            }
        }
        for (Task task: inTask) {
            if (pasNum >= maxLoad) {
                synchronized (this) {
                    task.getRelevantTask().setUnMarching();
                    people.addWaitingTask(task);
                }
                continue;
            }
            pasNum++;
            TimableOutput.println(
                    String.format("IN-%d-%d-%s", task.getId(), curFloor,name));
        }
    }

    private void printFloor(String eleState) {
        TimableOutput.println(String.format(eleState + "-%d-%s", curFloor,name));
    }

    private void moveUpFloor() throws InterruptedException {
        wait(perFloorTime);
        curFloor++;
        if (curFloor == 0) {
            curFloor++;
        }
        printFloor("ARRIVE");
    }

    private void moveDownFloor() throws InterruptedException {
        wait(perFloorTime);
        curFloor--;
        if (curFloor == 0) {
            curFloor--;
        }
        printFloor("ARRIVE");
    }

    private void openDoor() throws InterruptedException {
        printFloor("OPEN");
        wait(openDoorTime);
    }

    private void closeDoor() throws InterruptedException {
        wait(closeDoorTime);
        //unfinished,check tasks
        Vector<Task> tasks = people.getCurrentTasks(curFloor);
        if (!tasks.isEmpty()) {
            finishTasks(tasks);
        }
        printFloor("CLOSE");
    }

    public void addRequest(PersonRequest request) {
        synchronized (this) {
            people.addTask(request);
            reqNum++;
            if (curState.equals(State.STILL)) {
                if (request.getFromFloor() > curFloor) {
                    curState = State.UP;
                }
                else {
                    curState = State.DOWN;
                }
            }
        }
    }

    public int getCurFloor() {
        return curFloor;
    }

    public State getCurState() {
        return curState;
    }

    public double getPriorityValue(int targetFloor) {
        int target = targetFloor;
        if (targetFloor < 0) {
            target++;
        }
        int floor = curFloor;
        if (curFloor < 0) {
            floor++;
        }
        return (double) Math.abs(target - floor) / 19 * 0.8
                + (double)reqNum / 50 * 0.8;
    }

    public boolean isFull() {
        assert pasNum <= maxLoad;
        return pasNum == maxLoad;
    }

}
