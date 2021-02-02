package in.tagteen.tagteen.Model;

public class Language {
    private String language;
    private int id;

    public Language(int id, String language) {
        this.id = id;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public int getId() {
        return id;
    }
}
