package in.tagteen.tagteen.chatting.socket;


/**
 * Created by tony00 on 3/30/2019.
 */
public class NoEmitterInstantiatedException extends RuntimeException{

    public NoEmitterInstantiatedException(){
        super("SocketEmitterAssistant not instantiated due to socket connect failure");
    }

}
