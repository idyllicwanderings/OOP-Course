import com.oocourse.elevator2.PersonRequest;

import java.util.ArrayList;
import java.util.Vector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scheduler implements Runnable {

    private ArrayList<Elevator> elevators = new ArrayList<>();
    private ArrayList<Thread> threads = new ArrayList<>();
    private LinkedHashMap<PersonRequest,Long> buffer = new LinkedHashMap<>();
    private boolean stopFlag = false;
    private AtomicBoolean eleStopFlag;
    private static volatile Scheduler scheduler;

    public Scheduler(AtomicBoolean eleStopFlag) {
        this.eleStopFlag = eleStopFlag;
    }

    public static Scheduler getInstance(AtomicBoolean eleStopFlag) {
        if (scheduler == null) {
            synchronized (Scheduler.class) {
                if (scheduler == null) {
                    scheduler = new Scheduler(eleStopFlag);
                }
            }
        }
        return scheduler;
    }

    public void addElevator(Elevator elevator) {
        synchronized (elevators) {
            elevators.add(elevator);
            threads.add(null);
        }
    }

    public synchronized void addRequest(PersonRequest request) {
        //add to buffer
        buffer.put(request,System.currentTimeMillis());
        this.notifyAll();
    }

    private boolean parseRequest(PersonRequest request) {
        int index = getBestElevator(request);
        if (index == -1) {
            return false;
        }
        putRequest(index,request);
        return true;
    }

    private synchronized void putRequest(int index, PersonRequest request) {
        elevators.get(index).addRequest(request);
        if (threads.get(index) == null || !threads.get(index).isAlive()) {
            threads.set(index, new Thread(elevators.get(index)));
            threads.get(index).start();
        }
    }

    public synchronized void close() {
        stopFlag = true;
        this.notifyAll();
    }

    private int getBestElevator(PersonRequest request) {

        HashMap<Integer,Integer> choices = new HashMap<>();
        boolean isUp = request.getToFloor() > request.getFromFloor();

        //look for ALS elevators
        int indexMap = 0;
        for (int i = 0; i < elevators.size(); i++) {
            Elevator elevator = elevators.get(i);
            State curState = elevator.getCurState();
            if (elevator.getCurFloor() <= request.getFromFloor()
                    && curState.equals(State.UP) && isUp) {
                choices.put(indexMap++,i);
            }
            else if (elevator.getCurFloor() >= request.getToFloor()
                    && curState.equals(State.DOWN) && !isUp) {
                choices.put(indexMap++,i);
            }
        }

        if (!choices.isEmpty()) {
            return choices.get(new Random().nextInt(choices.size()));
        }

        //look for empty elevators
        choices.clear();
        indexMap = 0;
        for (int i = 0; i < elevators.size(); i++) {
            if (elevators.get(i).getCurState().equals(State.STILL)) {
                choices.put(indexMap++,i);
            }
        }

        if (!choices.isEmpty()) {
            return choices.get(new Random().nextInt(choices.size()));
        }

        return -1;
    }

    private synchronized void parseBufferRequest() {
        Vector<PersonRequest> timedRequests = new Vector<>();
        for (Iterator<Map.Entry<PersonRequest, Long>> iter = buffer.entrySet().iterator();
             iter.hasNext();) {
            Map.Entry<PersonRequest, Long> tmp = iter.next();
            if (parseRequest(tmp.getKey())) {
                iter.remove();
            }

            //check timestamp in order to avoid hunger
            //choose nearest elevator
            if (System.currentTimeMillis() - tmp.getValue() > 500) {
                int dst = tmp.getKey().getFromFloor();
                double sub = Double.MAX_VALUE;
                int index = 0;
                for (int i = 0; i < elevators.size(); i++) {
                    Elevator elevator = elevators.get(i);
                    if (elevator.getPriorityValue(dst) < sub) {
                        sub = elevator.getPriorityValue(dst);
                        index = i;
                    }
                }
                putRequest(index, tmp.getKey());
                //System.err.println("Force ADD:  " + (char) (index + 65));
                iter.remove();
            }
        }
    }

    @Override
    public void run() {
        while (!stopFlag || !buffer.isEmpty()) {
            synchronized (this) {
                if (buffer.isEmpty() && !stopFlag) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        ;
                    }
                }
            }
            parseBufferRequest();
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
                ;
            }
        }
        eleStopFlag.set(true);
    }

}
