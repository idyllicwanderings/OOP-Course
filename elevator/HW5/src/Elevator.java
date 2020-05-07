import com.oocourse.TimableOutput;
import com.oocourse.elevator1.PersonRequest;

import java.util.Vector;

public class Elevator implements Runnable {

    private static final int maxFloor = 15;
    private static final int minFloor = 1;

    private long openDoorTime;
    private long closeDoorTime;
    private long perFloorTime;
    private volatile TaskContainer people = new TaskContainer();
    private State curState;
    private int curFloor;

    public Elevator(long openDoorTime, long closeDoorTime, long perFloorTime) {
        this.openDoorTime = openDoorTime;
        this.closeDoorTime = closeDoorTime;
        this.perFloorTime = perFloorTime;
        curFloor = 1;
        curState = State.UP;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    Vector<Task> tasks = people.getCurrentTasks(curFloor);
                    if (!tasks.isEmpty()) {
                        openDoor();
                        finishTasks(tasks);
                        closeDoor();
                    }
                    if (people.isEnd()) {
                        //System.err.println("END!");
                        break;
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
        for (Task task : tasks) {
            if (task.isGetIn()) {
                TimableOutput.println(
                        String.format("IN-%d-%d", task.getId(), curFloor));

            } else {
                TimableOutput.println(
                        String.format("OUT-%d-%d", task.getId(), curFloor));
            }
        }
    }

    private void printFloor(String eleState) {
        TimableOutput.println(String.format(eleState + "-%d", curFloor));
    }

    private void moveUpFloor() throws InterruptedException {
        wait(perFloorTime);
        curFloor++;
        printFloor("ARRIVE");
    }

    private void moveDownFloor() throws InterruptedException {
        wait(perFloorTime);
        curFloor--;
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
        }
    }

}
