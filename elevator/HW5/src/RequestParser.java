import com.oocourse.elevator1.PersonRequest;

public class RequestParser {

    private Elevator elevators;
    private Thread threads;

    public RequestParser() {
    }

    public void addElevator(Elevator elevator) {
        elevators = elevator;
        threads = null;
    }

    public void parseRequest(PersonRequest request) {
        elevators.addRequest(request);
        if (threads == null || !threads.isAlive()) {
            threads = new Thread(elevators);
            threads.start();
        }
    }

}
