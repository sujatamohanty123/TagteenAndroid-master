package in.tagteen.tagteen.Fragments.youthtube.adapter;

public class LanguageDM {
  private int id;
  private String categoryName;
  private boolean isSelected;

  public LanguageDM(int id, String categoryName) {
    this.id = id;
    this.categoryName = categoryName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}
