package in.tagteen.tagteen.chatting.paging;

/**
 * Created by tony00 on 4/2/2019.
 */
public interface IPager {

    void loadInitial();

    void loadBefore();

    void loadAfter();
}
