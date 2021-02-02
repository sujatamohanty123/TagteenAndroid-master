package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import in.tagteen.tagteen.R;
import java.util.ArrayList;

/**
 * Created by lovekushvishwakarma on 08/10/17.
 */

public class CategorySpinnerAdapter extends ArrayAdapter<CategoryBean> {

  private Activity activity;
  private ArrayList<CategoryBean> data;
  public int res;
  LayoutInflater inflater;

  public CategorySpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
      @NonNull ArrayList<CategoryBean> objects) {
    super(context, resource, objects);
    data = objects;
    res = resource;
    activity = (Activity) context;
    inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {
    View row = inflater.inflate(res, parent, false);
    TextView label = (TextView) row.findViewById(R.id.lable);
    label.setText(data.get(position).getCategoryName());
       /* if(position%2==0){
            label.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }else{
            label.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }*/
    return row;
  }
}
