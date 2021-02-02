package in.tagteen.tagteen.Model;

public class GuidNameTuple {
    private String id;
    private String name;

    public GuidNameTuple(String id, String name) {
        this.id = id;
        this.name = name;
        // capitalize first letter
        if (this.name != null && this.name.length() > 1) {
            this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
