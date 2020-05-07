
public class Request {

    private int personId;
    private int from;
    private int to;
    private boolean isActive;
    private Request relevantRequest;

    private Task inTask;
    private Task outTask;

    public Request(int personId,int from,int to,Request relevantRequest,boolean isActive,
                   boolean isFirst) {
        this.personId = personId;
        this.from = from;
        this.to = to;
        this.relevantRequest = relevantRequest;
        this.isActive = isActive;
        if (relevantRequest != null) {
            outTask = new Task(personId, to, false, false,relevantRequest.getInTask());
        }
        else {
            outTask = new Task(personId,to,false,false,null);
        }
        if (isFirst) {
            inTask = new Task(personId, from, true, true, outTask);
        }
        else {
            inTask = new Task(personId, from, true, false, outTask);
        }
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public Request getRelevantRequest() {
        return relevantRequest;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public Task getInTask() {
        return inTask;
    }

    public Task getOutTask() {
        return outTask;
    }

}
