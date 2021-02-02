package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SendTagActivity;

import java.util.ArrayList;

/**
 * Created by ADMIN on 04-06-2017.
 */

public class CalcAdapter extends RecyclerView.Adapter<CalcAdapter.MyViewHolder> {
        Context context;

        ArrayList<CalcAdapter> arrayList=new ArrayList<>();

        int image[] = {R.drawable.pro1, R.drawable.pro2, R.drawable.pro3,
        R.drawable.pro1, R.drawable.pro4, R.drawable.pro5,
        R.drawable.pro6, R.drawable.pro3, R.drawable.pro4,
        R.drawable.pro1, R.drawable.pro4};

        String text[] = {"Body Mass Index",
        "Calories for weight gain/loss",
        "Target Heart Rate", "Blood Volume",
        "Blood Alcohol", "Water Intake",
        "Ideal Weight", "Body Fat",
        "Pregnancy Du Date",
        "Ovulation Period",
        "Blood Donation"};

        String text2[] = {"send a tag",
                "send a tag","send a tag","send a tag",
                "send a tag","send a tag",
                "send a tag","send a tag","send a tag",
                "send a tag","send a tag",
                "send a tag",};


    /*public CalcAdapter(Context context, ArrayList<CalcAdapter> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
*/
public CalcAdapter(SendTagActivity mainActivity) {
        return;
        }

@Override
public CalcAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleradapt, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view,context,arrayList);
        return myViewHolder;
        }

@Override
public void onBindViewHolder(CalcAdapter.MyViewHolder holder, int position) {

        holder.imageView.setImageResource(image[position]);
        holder.textView.setText(text[position]);
       // holder.button1.setText(text2[position]);
        }


@Override
public int getItemCount() {
        return text.length;
        }



private void startActivityForResult(Intent myIntent, int i) {
        return;
        }

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imageView;
    TextView textView;
    Button button1;

    Context context1;
    ArrayList<CalcAdapter> arrayList=new ArrayList<>();

    public MyViewHolder(View itemView,Context context1,ArrayList<CalcAdapter> arrayList) {
        super(itemView);
        this.arrayList=arrayList;
        this.context1=context1;
        itemView.setOnClickListener(this);
        imageView = (ImageView) itemView.findViewById(R.id.iv_image);
        textView = (TextView) itemView.findViewById(R.id.tv_text);
        button1 = (Button) itemView.findViewById(R.id.forwardbtn);


    }
    @Override
    public void onClick(View v) {
       /* int position1=getAdapterPosition();
        CalcAdapter calcAdapter=this.arrayList.get(position1);
        Intent iny = new Intent(this.context1, MainActivity.class);
        iny.putExtra("iv_image",calcAdapter.image);
        iny.putExtra("tv_text", calcAdapter.text);
        iny.putExtra("buton1", calcAdapter.text2);
        this.context1.startActivity(iny);*/
    }
}


}
