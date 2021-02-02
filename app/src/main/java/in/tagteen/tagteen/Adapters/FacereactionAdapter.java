package in.tagteen.tagteen.Adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

import in.tagteen.tagteen.Model.FaceReactionModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.FacereactionInterface;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.utils.CircleTransform;


public class FacereactionAdapter extends RecyclerView.Adapter<in.tagteen.tagteen.Adapters.FacereactionAdapter.ViewHolder> {

        Activity context;
        List<FaceReactionModel> al_menu;
        public GalleryClickedPosition delegate=null;
        private FacereactionInterface facereactionInterface;
         String rr="https://s3.ap-south-1.amazonaws.com/ttprofileurl/Self_Reaction/Happy.png";
         public FacereactionAdapter(Activity context, List<FaceReactionModel> al_menu, FacereactionInterface facereactionInterface) {
            this.context=context;
            this.al_menu = al_menu;
             this.facereactionInterface=facereactionInterface;
        }


        @Override
        public in.tagteen.tagteen.Adapters.FacereactionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.face_reaction_iteam, null);
            in.tagteen.tagteen.Adapters.FacereactionAdapter.ViewHolder mh = new in.tagteen.tagteen.Adapters.FacereactionAdapter.ViewHolder(v);
            return mh;
        }


        @Override
        public void onBindViewHolder(in.tagteen.tagteen.Adapters.FacereactionAdapter.ViewHolder itemRowHolder, int position) {

            FaceReactionModel reactionModel=al_menu.get(position);
            Glide.with(context).load(rr).error(R.drawable.pr_pic).transform(new CircleTransform(context)).into(itemRowHolder.iv_image);
            itemRowHolder.reactionName.setText(reactionModel.getReactionName());


        }

        @Override
        public int getItemCount() {
            return al_menu.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_image;
            ImageButton EditImage;
            TextView reactionName;
            public ViewHolder(final View convertView) {

                super(convertView);
                this.reactionName=(TextView) convertView.findViewById(R.id.reaction_Name);
                this.iv_image = (ImageView) convertView.findViewById(R.id.reaction_Image);


                iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position =getAdapterPosition();

                        facereactionInterface.sendReaction(rr);
                    }
                });

                reactionName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position =getAdapterPosition();
                        FaceReactionModel re =al_menu.get(position);
                        facereactionInterface.SetReaction(re.getReactionName(),position);
                    }
                });
            }

        }





    }
