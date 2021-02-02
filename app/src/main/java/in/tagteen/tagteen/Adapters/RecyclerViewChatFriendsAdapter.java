package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.CommonAdapterLongPress;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewChatFriendsAdapter
    extends RecyclerView.Adapter<RecyclerViewChatFriendsAdapter.ItemRowHolder> {

  private ArrayList<SectionDataModel> dataList;
  private Context mContext;
  private String name[] =
      {"Ajit", "Rhul", "Pradeep", "Siba", "Sachin", "Jaya", "Mohit", "Ramesh", "Anooj", "Sandeep",
          "mohit"};
  private List<ChatMessage> friends;

  private int avatar[] = {R.drawable.pro1, R.drawable.pro2, R.drawable.pro3,
      R.drawable.pro4, R.drawable.pro5, R.drawable.pro6, R.drawable.pro1,
      R.drawable.pro3, R.drawable.pro4, R.drawable.pro5};

  private CommonAdapterLongPress commonAdapterViewListner;

  public RecyclerViewChatFriendsAdapter(Context context, ArrayList<SectionDataModel> dataList,
      List<ChatMessage> friends, CommonAdapterLongPress commonAdapterViewListner) {
    this.dataList = dataList;
    this.mContext = context;
    this.friends = friends;
    this.commonAdapterViewListner = commonAdapterViewListner;
  }

  @Override
  public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v =
        LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.online_user_list_item, null);
    ItemRowHolder mh = new ItemRowHolder(v);
    return mh;
  }

  @Override
  public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {
    ArrayList singleSectionItems = dataList.get(i).getAllItemsInSection();
    Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
    Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
    ChatMessage chatMessage = null;
    if (i < 1) {
      SectionListDataAdapter itemListDataAdapter =
          new SectionListDataAdapter(mContext, singleSectionItems);
      itemRowHolder.recycler_view_list.setHasFixedSize(true);
      itemRowHolder.recycler_view_list.setLayoutManager(
          new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
      itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);
      itemRowHolder.recycler_view_list.setNestedScrollingEnabled(false);
      itemRowHolder.relativeLayout.setVisibility(View.GONE);
      itemRowHolder.recycler_view_list.setVisibility(View.VISIBLE);
      itemRowHolder.border.setVisibility(View.VISIBLE);
    } else {
      chatMessage = friends.get(i - 1);
      final String sectionName = chatMessage.getFriendName();

      itemRowHolder.itemTitle.setText(sectionName);
      itemRowHolder.relativeLayout.setVisibility(View.VISIBLE);
      itemRowHolder.recycler_view_list.setVisibility(View.GONE);

      itemRowHolder.border.setVisibility(View.GONE);
      if (i == 3 || i == 5 || i == 1) {
        fadeIn.setDuration(1500);
        itemRowHolder.indicator.startAnimation(fadeIn);
        fadeIn.setRepeatCount(Animation.INFINITE);
      } else {
        itemRowHolder.indicator.setVisibility(View.GONE);
      }

      itemRowHolder.im.setScaleType(ImageView.ScaleType.CENTER_CROP);
      itemRowHolder.im.setCornerRadius(60);
      Glide
          .with(mContext)
          .load(chatMessage.getThumbNailImage()).placeholder(R.drawable.placeholder)
          .fitCenter()
          .into(itemRowHolder.im);

      itemRowHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          commonAdapterViewListner.getClickedRecyclerView(i);
          return true;
        }
      });

      String chatCount = chatMessage.getUnreadCount();
      if (chatCount != null && !chatCount.equalsIgnoreCase("0")) {

        itemRowHolder.chatCount.setVisibility(View.VISIBLE);
        itemRowHolder.chatCount.setText(chatCount);
      } else {

        itemRowHolder.chatCount.setVisibility(View.GONE);
      }

      //            itemRowHolder.lastChatTime.setText(chatMessage.getLastMsgDate());
      //            itemRowHolder.lastchat.setText(chatMessage.getLastMessage());

      itemRowHolder.lastChatTime.setText("");
      itemRowHolder.lastchat.setText(chatMessage.getMessage());
    }

    final ChatMessage finalFriendList = chatMessage;
    itemRowHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                        SharedPreferenceSingleton.getInstance().init(mContext);
                                                        SharedPreferenceSingleton.getInstance()
                                                            .writeStringPreference(RegistrationConstants.FRIEND_ID, finalFriendList.getFriendId());
                                                        SharedPreferenceSingleton.getInstance()
                                                            .writeStringPreference(RegistrationConstants.FRIEND_NAME,
                                                                finalFriendList.getFriendName());
                                                        //                                                                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.FRIEND_IMAGE, finalFriendList.getImg());
                                                        //                                                                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.FRIEND_TAG, finalFriendList.getTag());
                                                        SharedPreferenceSingleton.getInstance()
                                                            .writeIntPreference(RegistrationConstants.IS_LOCKED_FRIEND, finalFriendList.isLocked());
                                                        Intent it = new Intent(mContext, ChatTreadActivity.class);
                                                        mContext.startActivity(it);
                                                      }
                                                    }
    );
  }

  @Override
  public int getItemCount() {
    return (null != friends ? friends.size() + 1 : 0);
  }

  public class ItemRowHolder extends RecyclerView.ViewHolder {

    TextView itemTitle, chatCount, lastChatTime, lastchat;
    RecyclerView recycler_view_list;
    protected RelativeLayout relativeLayout;
    protected RoundedImageView im;
    private ImageView indicator;
    private TextView border;

    ItemRowHolder(View view) {
      super(view);
      this.itemTitle = (TextView) view.findViewById(R.id.list_friend_name);
      this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
      this.relativeLayout = (RelativeLayout) view.findViewById(R.id.list_item_container);
      this.im = (RoundedImageView) view.findViewById(R.id.userViewImage);
      this.border = (TextView) view.findViewById(R.id.border_line);
      this.indicator = (ImageView) view.findViewById(R.id.online_indicator);
      this.chatCount = (TextView) view.findViewById(R.id.chatCountIcon);
      this.lastChatTime = (TextView) view.findViewById(R.id.last_chat_time);
      this.lastchat = (TextView) view.findViewById(R.id.chatChat);
    }
  }
}




