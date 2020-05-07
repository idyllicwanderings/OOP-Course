import com.oocourse.TimableOutput;
import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Elevator implements Runnable {

    private String name;
    private final long openDoorTime;
    private final long closeDoorTime;
    private final long perFloorTime;
    private final int maxLoad;
    private final TreeSet<Integer> stopFloors;
    private volatile TaskContainer people = new TaskContainer();
    private State curState;
    private int curFloor;
    private int pasNum = 0;
    private AtomicBoolean stopFlag;
    private volatile TaskQueue taskQueue = new TaskQueue();
    private volatile int destination;

    public Elevator(String name, long openDoorTime, long closeDoorTime,
                    long perFloorTime, int maxLoad, TreeSet<Integer> stopFloors,
                    AtomicBoolean stopFlag) {
        this.name = name;
        this.openDoorTime = openDoorTime;
        this.closeDoorTime = closeDoorTime;
        this.perFloorTime = perFloorTime;
        this.stopFloors = stopFloors;
        this.maxLoad = maxLoad;
        curFloor = 1;
        curState = State.STILL;
        this.stopFlag = stopFlag;
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public String getName() {
        return name;
    }

    private void moveFloor(int destination) throws InterruptedException {
        if (destination > curFloor) {
            moveUpFloor();
        } else if (destination < curFloor) {
            moveDownFloor();
        } else {
            ;
        }
    }

    @Override
    public void run() {
        //System.err.println(name + ":start!");
        out:
        while (true) {
            synchronized (this) {
                //make sure we take the active/inactive GetOn Task!
                Task curTask = taskQueue.getTask(curFloor);
                if (curTask == null && people.isEnd()) {
                    break out;
                }

                if (curTask != null && !curTask.isMarching()) {
                    destination = curTask.getDstFloor();
                } else {
                    destination = 0;
                }

                while (true) {
                    try {
                        ArrayList<Task> addTasks = taskQueue.getReadyTasks();
                        for (Task task : addTasks) {
                            people.addWaitingTask(task);
                        }
                        ArrayList<Task> tasks = people.getCurrentTasks(curFloor,pasNum,maxLoad);
                        if (!tasks.isEmpty()) {
                            openDoor();
                            finishTasks(tasks);
                            closeDoor();
                        }

                        if (people.isEnd()) {
                            if (taskQueue.isEnd()) {
                                break out;
                            }
                            if (destination != 0
                                    && curFloor != destination) {
                                //System.err.println(destination);
                                moveFloor(destination);
                                continue;
                            } else {
                                curState = State.STILL;
                                break;
                            }
                            //curState = State.STILL;
                            //break;
                        }
                        curState = people.getDirection(curFloor, curState);
                        if (curState.equals(State.UP)) {
                            moveUpFloor();
                        } else if (curState.equals(State.DOWN)) {
                            moveDownFloor();
                        } else {
                            //System.err.println(name + ":STILL");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //System.err.println(name + ":end!");
    }

    private void finishTasks(ArrayList<Task> tasks) throws InterruptedException {
        ArrayList<Task> inTask = new ArrayList<>();
        for (Task task : tasks) {
            if (!task.isGetIn()) {
                pasNum--;
                TimableOutput.println(
                        String.format("OUT-%d-%d-%s", task.getId(), curFloor, name));
                task.setRelevantActive();
            }
            else {
                inTask.add(task);
            }
        }
        for (Task task : inTask) {
            pasNum++;
            TimableOutput.println(
                    String.format("IN-%d-%d-%s", task.getId(), curFloor, name));
            task.setRelevantActive();
        }
    }

    private void printFloor(String eleState) {
        TimableOutput.println(String.format(eleState + "-%d-%s", curFloor, name));
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
        ArrayList<Task> addTasks = taskQueue.getReadyTasks();
        for (Task task : addTasks) {
            people.addWaitingTask(task);
        }
        ArrayList<Task> tasks = people.getCurrentTasks(curFloor,pasNum,maxLoad);
        if (!tasks.isEmpty()) {
            finishTasks(tasks);
        }
        printFloor("CLOSE");
    }

    public void addRequest(PersonRequest request) {
        synchronized (people) {
            people.addTask(request);
            if (curState.equals(State.STILL)) {
                if (request.getFromFloor() > curFloor) {
                    curState = State.UP;
                } else {
                    curState = State.DOWN;
                }
            }
        }

    }

    public void addTask(Task task) {
        synchronized (people) {
            //System.err.println(name + ":" + task.getId() +",
            // "+task.getDstFloor()+","+task.isGetIn());
            people.addWaitingTask(task);
            if (curState.equals(State.STILL)) {
                if (!task.isMarching()) {
                    return;
                }
                if (task.getDstFloor() > curFloor) {
                    curState = State.UP;
                } else {
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

    public TreeSet<Integer> getStopFloors() {
        return stopFloors;
    }

    public int getTaskNum() {
        return people.size();
    }
}
