package in.tagteen.tagteen.FilePicker.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import in.tagteen.tagteen.FilePicker.FilePickerActivity;
import in.tagteen.tagteen.FilePicker.item.Document;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.FilePickerInterFace;

import java.util.List;



public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.FilePickerHolder> {
    private Context context;
    private List<Document> feedlist;
    private FilePickerActivity activity;
    FilePickerInterFace filePickerInterFace;
    public FilePickerAdapter(Context context, FilePickerActivity activity, List<Document>feedlist, FilePickerInterFace filePickerInterFace){
        this.context = context;
        this.activity = activity;
        this.feedlist = feedlist;
        this.filePickerInterFace=filePickerInterFace;
    }
    @Override
    public FilePickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.filepicker_item_document, parent, false);
        return new FilePickerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilePickerHolder holder, int position) {
        final Document document = feedlist.get(position);
        holder.imgIcon.setImageResource(document.getTypeDrawable());
        holder.txtName.setText(document.getTitle());
        String size=Formatter.formatShortFileSize(context, Long.parseLong(document.getSize())).toString();
        holder.txtSize.setText(size);
        holder.txtPath.setText(document.getPath());
/*
        holder.relativeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*activity.finishData(document.getPath());*//*
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return feedlist.size();
    }

    public class FilePickerHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtName,txtSize,txtPath;
        RelativeLayout relativeMain;
        public FilePickerHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtSize = (TextView) itemView.findViewById(R.id.txtSize);
            txtPath = (TextView) itemView.findViewById(R.id.txtPath);
            relativeMain = (RelativeLayout)itemView.findViewById(R.id.relativeMain);
            relativeMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position =getAdapterPosition();
                    Document document = feedlist.get(position);
                    filePickerInterFace.clickedDoc(document.getPath(),document.getTitle(),document.getSize());
                }
            });
        }
    }
}
