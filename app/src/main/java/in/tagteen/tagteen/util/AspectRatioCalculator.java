package in.tagteen.tagteen.util;

import android.util.Log;


public class AspectRatioCalculator {
    private static final String TAG="AspectRatioCalc";
    private boolean isLandscape=false;
    private double aspectRatio;

    public AspectRatioCalculator(){
    }

    public void AspectRatioCalculatorT(int width,int height){
        this.isLandscape=width>height;
        this.aspectRatio=isLandscape?(width/(double)height): (height/(double)width);
        Log.d(TAG,"size: "+width+" * "+height +" aspectRatio: "+aspectRatio+" isLandscape : "+isLandscape);
    }

    public AspectRatioCalculator(double aspectRatio, boolean isLandscape){
        this.isLandscape=isLandscape;
        this.aspectRatio=aspectRatio;
        Log.d(TAG," aspectRatio: "+aspectRatio+" isLandscape : "+isLandscape);
    }

//    public int getHeightBy(int width){
//        return isLandscape?(int)(width/aspectRatio):(int)(width*aspectRatio);
//    }
//    public int getWidthBy(int height){
//        return isLandscape?(int)(height*aspectRatio):(int)(height/aspectRatio);
//    }

    public int getHeightBy(int width){
        return isLandscape?(int)Math.floor(width/aspectRatio):(int)Math.floor(width*aspectRatio);
    }
    public int getWidthBy(int height){
        return isLandscape?(int)Math.floor(height*aspectRatio):(int)Math.floor(height/aspectRatio);
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public boolean isLandscape() {
        return isLandscape;
    }
}
