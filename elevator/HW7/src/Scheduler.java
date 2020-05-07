import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scheduler implements Runnable {

    private ArrayList<Elevator> elevators = new ArrayList<>();
    private ArrayList<Thread> threads = new ArrayList<>();
    private ArrayList<TaskQueue> taskQueues = new ArrayList<>();
    private LinkedList<PersonRequest> buffer = new LinkedList<>();
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
            taskQueues.add(elevator.getTaskQueue());
        }
    }

    public synchronized void addRequest(PersonRequest request) {
        //add to buffer
        buffer.add(request);
        this.notifyAll();
    }

    private boolean parseRequest(PersonRequest request) {
        ArrayList<PriorityIndex> indexList = new ArrayList<>();
        for (int i = 0; i < elevators.size(); i++) {
            indexList.add(new PriorityIndex(i, elevators.get(i).getTaskNum()
                    + taskQueues.get(i).getTaskNum()));
        }
        indexList.sort(PriorityIndex::compareTo);

        if (!toSplit(request, indexList)) {
            divRequest(request, indexList);
            return true;
        }

        int index = getBestElevator(request);
        if (index == -1) {
            return false;
        }

        putRequest(index, request);
        return true;
    }

    private void putRequest(int index, PersonRequest request) {
        elevators.get(index).addRequest(request);
        startThread(index);
    }

    private void putTask(int index, Task task) {
        elevators.get(index).addTask(task);
        startThread(index);
    }

    private void putTask(int index,ArrayList<Task> ret) {
        taskQueues.get(index).putTask(ret);
        startThread(index);
    }

    public synchronized void close() {
        stopFlag = true;
        this.notifyAll();
    }

    private int getBestElevator(PersonRequest request) {

        HashMap<Integer, Integer> choices = new HashMap<>();
        boolean isupper = request.getToFloor() > request.getFromFloor();
        HashSet<Integer> availElevators = new HashSet<>();
        int from = request.getFromFloor();
        int to = request.getToFloor();
        //get available elevators
        for (int i = 0; i < elevators.size(); i++) {
            TreeSet<Integer> stopFloors = elevators.get(i).getStopFloors();
            if (stopFloors.contains(from) && stopFloors.contains(to)) {
                availElevators.add(i);
            }
        }

        //look for ALS elevators
        int indexMap = 0;
        for (Integer i : availElevators) {
            Elevator elevator = elevators.get(i);
            State curState = elevator.getCurState();
            if (elevator.getCurFloor() <= request.getFromFloor()
                    && curState.equals(State.UP) && isupper) {
                choices.put(indexMap++, i);
            } else if (elevator.getCurFloor() >= request.getToFloor()
                    && curState.equals(State.DOWN) && !isupper) {
                choices.put(indexMap++, i);
            }
        }

        if (!choices.isEmpty()) {
            return choices.get(new Random().nextInt(choices.size()));
        }

        //look for empty elevators
        choices.clear();
        indexMap = 0;
        for (Integer i : availElevators) {
            if (elevators.get(i).getCurState().equals(State.STILL)) {
                choices.put(indexMap++, i);
            }
        }

        if (!choices.isEmpty()) {
            return choices.get(new Random().nextInt(choices.size()));
        }

        return -1;
    }

    private synchronized void parseBufferRequest() {
        for (Iterator<PersonRequest> iter = buffer.iterator();
             iter.hasNext(); ) {
            PersonRequest tmp = iter.next();
            if (parseRequest(tmp)) {
                iter.remove();
                continue;
            }
            //choose less reqNum 's elevator
            int from = tmp.getFromFloor();
            int to = tmp.getToFloor();
            TreeMap<Integer, Integer> avails = new TreeMap<>();
            for (int i = 0; i < elevators.size(); i++) {
                TreeSet<Integer> stopFloors = elevators.get(i).getStopFloors();
                if (stopFloors.contains(from) && stopFloors.contains(to)) {
                    avails.put(elevators.get(i).getTaskNum()
                            + taskQueues.get(i).getTaskNum(), i);
                }
            }
            int index = avails.firstKey();
            putRequest(avails.get(index), tmp);
            iter.remove();

        }
    }

    private void divRequest(PersonRequest request,
                            ArrayList<PriorityIndex> avails) {

        int firElevator = -3;
        int srcFloor = request.getFromFloor();
        int secElevator = -3;
        int dstFloor = request.getToFloor();

        for (PriorityIndex ele : avails) {
            TreeSet<Integer> stopFloors = elevators.get(ele.getEleNum()).getStopFloors();
            if (stopFloors.contains(srcFloor) && firElevator == -3) {
                firElevator = ele.getEleNum();
            }
            if (secElevator == -3 && stopFloors.contains(dstFloor)) {
                secElevator = ele.getEleNum();
            }
        }
        int highFloor;
        int lowFloor;
        if (dstFloor < srcFloor) {
            highFloor = srcFloor;
            lowFloor = dstFloor;
        } else {
            highFloor = dstFloor;
            lowFloor = srcFloor;
        }
        //go up
        int up = 0;
        int down = 0;

        //middle floor
        TreeSet<Integer> firstStop = elevators.get(firElevator).getStopFloors();
        TreeSet<Integer> secondStop = elevators.get(secElevator).getStopFloors();
        for (int i = lowFloor + 1; i < highFloor; i++) {
            if (secondStop.contains(i) && firstStop.contains(i)) {
                //set relevant tasks
                addTask(firElevator, secElevator, i, request);
                return;
            }
        }

        for (int i = lowFloor + 1; i <= firstStop.last() && i <= secondStop.last(); i++) {
            if (secondStop.contains(i) && firstStop.contains(i)) {
                up = i;
                break;
            }
        }

        //go down
        for (int i = lowFloor - 1;i >= secondStop.first()
                && i >= firstStop.first(); i--) {
            if (secondStop.contains(i) && firstStop.contains(i)) {
                down = i;
                break;
            }
        }

        if (Math.abs(up - highFloor)
                < Math.abs(lowFloor - down)) {
            addTask(firElevator, secElevator, up, request);
        } else {
            addTask(firElevator, secElevator, down, request);
        }
    }

    private void addTask(int ele1, int ele2, int mid, PersonRequest request) {
        //System.err.println(request.getPersonId() + ":   "+ele1+" ;"+ele2+"."+mid);
        Request second = new Request(request.getPersonId(),
                mid, request.getToFloor(), null, false, false);
        Request first = new Request(request.getPersonId(), request.getFromFloor(),
                mid, second, true, true);
        putTask(ele1, first.getOutTask());
        putTask(ele1, first.getInTask());
        ArrayList<Task> ret = new ArrayList<>();
        ret.add(second.getInTask());
        ret.add(second.getOutTask());
        putTask(ele2,ret);
    }

    private void startThread(int index) {
        if (threads.get(index) == null || !threads.get(index).isAlive()) {
            threads.set(index, new Thread(elevators.get(index)));
            threads.get(index).start();
        }
    }

    public boolean toSplit(PersonRequest request, ArrayList<PriorityIndex> indexList) {
        int fromFloor = request.getFromFloor();
        int toFloor = request.getToFloor();
        for (PriorityIndex index : indexList) {
            int num = index.getEleNum();
            TreeSet<Integer> stopFloors = elevators.get(num).getStopFloors();
            if (stopFloors.contains(fromFloor) && stopFloors.contains(toFloor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        while (!stopFlag || !buffer.isEmpty()) {
            synchronized (this) {
                if (buffer.isEmpty() && !stopFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            parseBufferRequest();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        eleStopFlag.set(true);
        for (TaskQueue taskQueue:taskQueues) {
            taskQueue.kill();
        }
    }

}
