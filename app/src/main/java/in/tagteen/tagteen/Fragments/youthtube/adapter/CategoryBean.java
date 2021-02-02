package in.tagteen.tagteen.Fragments.youthtube.adapter;

import in.tagteen.tagteen.AntonyChanges;

/**
 * Created by lovekushvishwakarma on 02/10/17.
 */

public class CategoryBean {
    private int id;
    private String categoryName;
    private boolean isSelected=false;

    public CategoryBean(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "CategoryBean{" +
                "category_name='" + this.categoryName + '\'' +
                ", _id=" + this.id +
                ", isSelected=" + this.isSelected +
                '}';
    }
}
