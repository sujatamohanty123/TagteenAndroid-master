package in.tagteen.tagteen.chatting.socket;

public class NoSocketListenerFoundException extends RuntimeException {

    public NoSocketListenerFoundException() {
        super("No SocketEventListener found in the SocketConnection");
    }

}
