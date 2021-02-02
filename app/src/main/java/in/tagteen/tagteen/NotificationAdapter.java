package in.tagteen.tagteen;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;

    int image[] = {in.tagteen.tagteen.R.drawable.pro1, in.tagteen.tagteen.R.drawable.pro2, in.tagteen.tagteen.R.drawable.pro3,
            in.tagteen.tagteen.R.drawable.pro1, in.tagteen.tagteen.R.drawable.pro4, in.tagteen.tagteen.R.drawable.pro5,
            in.tagteen.tagteen.R.drawable.pro6, in.tagteen.tagteen.R.drawable.pro3, in.tagteen.tagteen.R.drawable.pro4,
            in.tagteen.tagteen.R.drawable.pro1, in.tagteen.tagteen.R.drawable.pro4};

    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifyrecycleradapt, parent, false);
        NotificationAdapter.NotificationViewHolder viewHolder = new NotificationAdapter.NotificationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.NotificationViewHolder holder, int position) {
        // profile url
        holder.imgPicture.setImageResource(image[position]);
        holder.lblFromUser.setText("Tagteen User " + position);
        holder.lblDescription.setText("Description of the notifi...");
        holder.lblTime.setText("5 mins ago");
    }

    @Override
    public int getItemCount() {
        return image.length;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPicture;
        private TextView lblFromUser;
        private TextView lblDescription;
        private TextView lblTime;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            this.imgPicture = (ImageView) itemView.findViewById(R.id.imgPicture);
            this.lblFromUser = (TextView) itemView.findViewById(R.id.lblFromUser);
            this.lblDescription = (TextView) itemView.findViewById(R.id.lblDescription);
            this.lblTime = (TextView) itemView.findViewById(R.id.lblTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Notification", "Clicked: " + lblFromUser.getText().toString());
                }
            });
        }
    }
}
