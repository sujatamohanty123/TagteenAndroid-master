package in.tagteen.tagteen.Fragments.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lovekushvishwakarma on 15/01/18.
 */

public class SearchInputJson {

    @SerializedName("search_data")
    @Expose
    private String search_data;

    public String getSearch_data() {
        return search_data;
    }

    public void setSearch_data(String search_data) {
        this.search_data = search_data;
    }
}
