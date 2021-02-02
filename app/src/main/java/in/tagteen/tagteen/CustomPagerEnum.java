package in.tagteen.tagteen;



public enum CustomPagerEnum {

    DJFSKS(in.tagteen.tagteen.R.string.Red_1, in.tagteen.tagteen.R.layout.tutorial_activity);
    private int mTitleResId;
    private int mLayoutResId;

    CustomPagerEnum(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}