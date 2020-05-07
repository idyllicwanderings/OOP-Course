import com.oocourse.TimableOutput;
import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ElementaryTest {

    private static Scheduler requestParser;
    private static AtomicBoolean flag;

    public static void main(String[] args) throws IOException {
        TimableOutput.initStartTimestamp();
        flag = new AtomicBoolean(false);
        requestParser = Scheduler.getInstance(flag);
        final Thread schedulerThread = new Thread(requestParser);
        schedulerThread.start();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        int elevatorNum = elevatorInput.getElevatorNum();
        //System.out.println(elevatorNum);
        init(elevatorNum);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                break;
            }
            requestParser.addRequest(request);
        }
        elevatorInput.close();
        requestParser.close();
    }

    private static void init(int num) {
        int div = 19 / num;
        for (int i = 0; i < num; i++) {
            requestParser.addElevator(new Elevator(((char)(i + 65)) + "", 200,
                    200, 400, div * i + -3, flag));
        }
    }
}
