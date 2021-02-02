package in.tagteen.tagteen.GallaryPicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import in.tagteen.tagteen.R;

import java.util.List;


public class MyGallaryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Model_images> imageUrls ;
 /*   private List <String> imageBuckets ;*/
    private Context context;

    public MyGallaryAdapter(Context context, List<Model_images> imageUrls ) {
        this.context=context;

        this.imageUrls=imageUrls;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {


        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_layout, null);
        }

        TextView label=(TextView)convertView.findViewById(R.id.company);
        ImageView icon=(ImageView)convertView.findViewById(R.id.image);

        label.setText(imageUrls.get(position).getStr_folder());



        String url = "file://" + imageUrls.get(position).getAl_imagepath().get(0);

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.drawable_photofolder)
                .into(icon);

        return convertView;

    }

}