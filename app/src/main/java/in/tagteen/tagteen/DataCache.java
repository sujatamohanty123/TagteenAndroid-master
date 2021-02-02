package in.tagteen.tagteen;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Model.GetPostResponseModel;

public class DataCache {
    private static DataCache dataCache;
    private List<GetPostResponseModel.PostDetails> postlist;
    private boolean doLoadMore = true;
    private int pageLoaded = 1;

    private DataCache() {
    }

    public static DataCache getInstance() {
        if (dataCache == null) {
            dataCache = new DataCache();
        }
        return dataCache;
    }

    public void setPostlist(List<GetPostResponseModel.PostDetails> postlist, boolean doLoadMore) {
        this.postlist = postlist;
        this.doLoadMore = doLoadMore;
    }

    public void addPosts(List<GetPostResponseModel.PostDetails> postlist) {
        if (postlist == null || postlist.isEmpty()) {
            return;
        }
        if (this.postlist == null) {
            this.postlist = new ArrayList<>();
        }
        this.postlist.addAll(postlist);
    }

    public boolean isDoLoadMore() {
        return doLoadMore;
    }

    public void setPageLoaded(int pageLoaded) {
        this.pageLoaded = pageLoaded;
    }

    public int getPageLoaded() {
        return pageLoaded;
    }

    public List<GetPostResponseModel.PostDetails> getPostlist() {
        return postlist;
    }
}
