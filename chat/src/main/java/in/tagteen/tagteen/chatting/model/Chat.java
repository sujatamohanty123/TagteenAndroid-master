package in.tagteen.tagteen.chatting.model;

import java.io.Serializable;

public class Chat implements Serializable{
	private long id;
	private Friend friend;
	private NewMessage message;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Friend getFriend() {
		return friend;
	}

	public void setFriend(Friend friend) {
		this.friend = friend;
	}

	public NewMessage getMessage() {
		return message;
	}

	public void setMessage(NewMessage message) {
		this.message = message;
	}
}