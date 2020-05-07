import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class TaskQueue {

    private volatile ArrayList<Task> taskQueue = new ArrayList<>();
    private boolean stop = false;

    public synchronized void kill() {
        this.stop = true;
        this.notifyAll();
    }

    public boolean isEnd() {
        return stop && taskQueue.isEmpty();
    }

    public synchronized Task getTask(int curFloor) {
        while (taskQueue.size() == 0) {
            if (stop) {
                return null;
            }
            try {
                this.wait();
            } catch (InterruptedException e) {
                ;
            }
        }

        Optional<Task> ret = taskQueue.stream().filter(Task::isMarching).findFirst();
        if (ret.isPresent()) {
            return ret.get();
        }


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            ;
        }

        assert taskQueue.stream().anyMatch(Task::isGetIn);
        return taskQueue.stream().filter(Task::isGetIn).findFirst().get();
    }

    public synchronized void putTask(ArrayList<Task> persontask) {
        for (Task task:persontask) {
            this.taskQueue.add(task);
            //System.err.println("taskqueue:" + task.getId() +","+
            // task.getDstFloor()+","+task.isGetIn()+","
            //+task.isMarching());
        }
        this.notifyAll();
    }

    public synchronized ArrayList<Task> getReadyTasks() {
        ArrayList<Task> ret = new ArrayList<>();
        for (Iterator<Task> iter = taskQueue.iterator();iter.hasNext();) {
            Task task = iter.next();
            if (task.isMarching() && task.isGetIn()) {
                Task offTask = task.getRelevantTask();
                ret.add(task);
                ret.add(task.getRelevantTask());
            }
        }
        for (Task task:ret) {
            taskQueue.remove(task);
        }
        return ret;
    }

    public int getTaskNum() {
        return taskQueue.size();
    }

}
