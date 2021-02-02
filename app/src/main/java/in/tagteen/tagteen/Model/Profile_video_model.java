package in.tagteen.tagteen.Model;

/**
 * Created by Mathivanan on 29-05-2017.
 */

public class Profile_video_model {
    public Profile_video_model() {

    }
    public Profile_video_model(String desc, int img){
        this.imag=img;
        this.desc=desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String desc;



    public Integer getImag() {
        return imag;
    }

    public void setImag(Integer imag) {
        this.imag = imag;
    }

    private Integer imag;
}
