package in.tagteen.tagteen.profile;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class FanListModel {

    String name;
    boolean isSelected=false;

    public FanListModel(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
