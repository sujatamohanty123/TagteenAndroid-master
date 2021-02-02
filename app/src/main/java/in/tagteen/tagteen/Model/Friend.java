package in.tagteen.tagteen.Model;


public class Friend {

    private String tag;
    private String img;
    private String name;
    private String id;
    private Integer imag;
    private boolean isSelected = false;
    private  String isGroup;
    private  String lastMessage;
    private  String lastMsgDate;
    private  String lastMsgTime;
    private  String chatCount;
    private  String msgStatus;
    private  String isLocked;
    private  String isMute;




    public Friend() {

    }
    public Friend(String name, String tag,Integer imag){
        this.name=name;
        this.tag=tag;
        this.imag=imag;
    }
    public Friend(String name, String tag, String img, String id){
        this.name=name;
        this.tag=tag;
        this.img=img;
        this.id=id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getImag() {
        return imag;
    }

    public void setImag(Integer imag) {
        this.imag = imag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMsgDate() {
        return lastMsgDate;
    }

    public void setLastMsgDate(String lastMsgDate) {
        this.lastMsgDate = lastMsgDate;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getChatCount() {
        return chatCount;
    }

    public void setChatCount(String chatCount) {
        this.chatCount = chatCount;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getIsMute() {
        return isMute;
    }

    public void setIsMute(String isMute) {
        this.isMute = isMute;
    }

}
