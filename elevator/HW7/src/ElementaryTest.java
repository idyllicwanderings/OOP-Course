import com.oocourse.TimableOutput;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class ElementaryTest {

    private static final int doorTime = 200;
    private static ArrayList<TreeSet<Integer>> stopFloors = new ArrayList<>();
    private static ArrayList<Integer> maxLoad = new ArrayList<>();
    private static ArrayList<Long> moveTime = new ArrayList<>();
    private static Scheduler requestParser;
    private static AtomicBoolean flag;

    public static void main(String[] args) throws IOException {
        TimableOutput.initStartTimestamp();
        flag = new AtomicBoolean(false);
        requestParser = Scheduler.getInstance(flag);
        final Thread schedulerThread = new Thread(requestParser);
        schedulerThread.start();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        init();
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                break;
            }
            if (request instanceof PersonRequest) {
                requestParser.addRequest((PersonRequest) request);
            } else if (request instanceof ElevatorRequest) {
                ElevatorRequest elevator = (ElevatorRequest) request;
                int index = elevator.getElevatorType().toCharArray()[0] - 'A';
                requestParser.addElevator(new Elevator(elevator.getElevatorId(),
                        doorTime, doorTime, moveTime.get(index), maxLoad.get(index),
                        stopFloors.get(index), flag));
            }
        }
        elevatorInput.close();
        requestParser.close();
    }

    private static void init() {

        maxLoad.add(6);
        maxLoad.add(8);
        maxLoad.add(7);

        moveTime.add(400L);
        moveTime.add(500L);
        moveTime.add(600L);

        TreeSet<Integer> typeA = new TreeSet<>();
        TreeSet<Integer> typeB = new TreeSet<>();
        TreeSet<Integer> typeC = new TreeSet<>();

        typeA.add(-3);
        typeB.add(2);
        for (int i = -2; i <= 1; i++) {
            if (i != 0) {
                typeA.add(i);
                typeB.add(i);
            }
        }

        for (int i = 4; i <= 15; i++) {
            typeB.add(i);
        }

        for (int i = 15; i <= 20; i++) {
            typeA.add(i);
        }
        stopFloors.add(typeA);
        stopFloors.add(typeB);
        for (int i = 1; i <= 15; i += 2) {
            typeC.add(i);
        }
        stopFloors.add(typeC);


        requestParser.addElevator(new Elevator("A", doorTime, doorTime,
                moveTime.get(0), maxLoad.get(0), stopFloors.get(0), flag));
        requestParser.addElevator(new Elevator("B", doorTime, doorTime,
                moveTime.get(1), maxLoad.get(1), stopFloors.get(1), flag));
        requestParser.addElevator(new Elevator("C", doorTime, doorTime,
                moveTime.get(2), maxLoad.get(2), stopFloors.get(2), flag));
    }
}
