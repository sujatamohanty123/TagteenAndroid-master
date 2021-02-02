package in.tagteen.tagteen.Filter.activity;

import android.graphics.Bitmap;

import in.tagteen.tagteen.Filter.GPUImageFilterTools;


public class ModelClass {

    private   String mName;
    private   GPUImageFilterTools.FilterType mfilterType;
    private   int mFilterPercent;
    private   Bitmap mBitmap;
    private   Boolean isDrawable;
    private   int drawableValue;


    public  ModelClass(String name, GPUImageFilterTools.FilterType mfilterType, Bitmap mBitmap,int mFilterPercent,boolean isDrawable, int drawableValue) {

        this.mName = name;
        this.mfilterType = mfilterType;
        this.mFilterPercent = mFilterPercent;
        this.mBitmap = mBitmap;
        this.isDrawable = isDrawable;
        this.drawableValue=drawableValue;
    }

    public int getDrawableValue() {
        return drawableValue;
    }

    public void setDrawableValue(int drawableValue) {
        this.drawableValue = drawableValue;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public GPUImageFilterTools.FilterType getMfilterType() {
        return mfilterType;
    }

    public void setMfilterType(GPUImageFilterTools.FilterType mfilterType) {
        this.mfilterType = mfilterType;
    }

    public int getmFilterPercent() {
        return mFilterPercent;
    }

    public void setmFilterPercent(int mFilterPercent) {
        this.mFilterPercent = mFilterPercent;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Boolean getDrawable() {
        return isDrawable;
    }

    public void setDrawable(Boolean drawable) {
        isDrawable = drawable;
    }


}
