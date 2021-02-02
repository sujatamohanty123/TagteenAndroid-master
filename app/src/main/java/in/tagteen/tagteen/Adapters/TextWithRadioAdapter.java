package in.tagteen.tagteen.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import in.tagteen.tagteen.Model.GuidNameTuple;
import in.tagteen.tagteen.R;

public class TextWithRadioAdapter extends RecyclerView.Adapter<TextWithRadioAdapter.ViewHolder> {
    private List<GuidNameTuple> infos;

    private Context context;
    private GuidNameTuple selectedItem;
    private String selectedItemId;

    public TextWithRadioAdapter(Context context, List<GuidNameTuple> infos, String selectedItemId) {
        this.context = context;
        this.infos = infos;
        this.selectedItemId = selectedItemId;
    }

    public GuidNameTuple getSelectedItem() {
        return selectedItem;
    }

    @NonNull
    @Override
    public TextWithRadioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_text_with_radio, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TextWithRadioAdapter.ViewHolder holder, int position) {
        final GuidNameTuple item = this.infos.get(position);

        if (this.selectedItemId == null) {
            this.selectedItemId = item.getId();
            this.selectedItem = item;
        } else {
            if (item.getId().equals(this.selectedItemId)) {
                this.selectedItem = item;
            }
        }
        boolean isChecked = item.getId().equals(this.selectedItemId);
        String text = item.getName();

        holder.lblText.setText(text);
        holder.radioOption.setChecked(isChecked);
        holder.radioOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemId = item.getId();
                selectedItem = item;
                notifyDataSetChanged();
            }
        });
        holder.lblText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemId = item.getId();
                selectedItem = item;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.infos != null ? this.infos.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblText;
        private RadioButton radioOption;

        public ViewHolder(View view) {
            super(view);
            this.lblText = (TextView) view.findViewById(R.id.lblText);
            this.radioOption = (RadioButton) view.findViewById(R.id.radioOption);
        }
    }
}
