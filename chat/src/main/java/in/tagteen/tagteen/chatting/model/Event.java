package in.tagteen.tagteen.chatting.model;

import java.io.Serializable;

public class Event implements Serializable {

    private transient String event;

    public Event(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
