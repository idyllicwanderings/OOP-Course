import com.oocourse.TimableOutput;
import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;

public class ElementaryTest {
    public static void main(String[] args) throws IOException {

        TimableOutput.initStartTimestamp();
        RequestParser requestParser = new RequestParser();
        requestParser.addElevator(new Elevator(200, 200, 400));
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                break;
            }
            requestParser.parseRequest(request);
        }
        elevatorInput.close();
    }
}
