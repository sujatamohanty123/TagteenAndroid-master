package in.tagteen.tagteen.profile.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.NotificationModel;
import in.tagteen.tagteen.Model.NotificationResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.profile.AboutActivity;
import in.tagteen.tagteen.profile.ChangePassWord;
import in.tagteen.tagteen.profile.DeactivateUserAccount;
import in.tagteen.tagteen.profile.FaqActivity;
import in.tagteen.tagteen.profile.FeedbackActivity;
import in.tagteen.tagteen.profile.LogoutDialog;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 10/10/17.
 */

public class AppSettingAdapter extends RecyclerView.Adapter<AppSettingAdapter.ImageViewHolder> {

    private ArrayList<String> categoryList;
    public static Context mContext;
    private boolean isLike = false;
    boolean miniOption=true;
    NotificationModel likeJsonInputModel=new NotificationModel();
    public AppSettingAdapter(ArrayList<String> categoryList, Context mContext) {
        this.categoryList = categoryList;
        this.mContext = mContext;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_list_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        holder.rightLayout.setVisibility(View.GONE);
        holder.layoutMiniOptions.setVisibility(View.GONE);
        holder.title.setText(categoryList.get(position));
        if (position == 0 || position == 9) {
            holder.rightLayout.setVisibility(View.VISIBLE);
        }/* else if (position == 3 || position == 4) {
            holder.layoutmain.setVisibility(View.GONE);
            holder.textNormal.setVisibility(View.GONE);
            //holder.imageMore.setVisibility(View.GONE);
            holder.switchSetting.setVisibility(View.VISIBLE);
        }*/ else if (position == 1 || position == 2 || position == 3 || position == 5) {
            holder.textNormal.setVisibility(View.GONE);
        } else {
            holder.textNormal.setVisibility(View.VISIBLE);
        }

        if(position==2){
            holder.switchSetting.setVisibility(View.VISIBLE);
        }else {
            holder.switchSetting.setVisibility(View.GONE);
        }
        String buttonState_swtch1 = LoadButtonState_swtch1();
        if (buttonState_swtch1.equals("active")) {
            holder.switchSetting.setChecked(true);
        } else if (buttonState_swtch1.equals("inactive")) {
            holder.switchSetting.setChecked(false);
        }
        holder.switchSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!holder.switchSetting.isChecked()) {
                    holder.switchSetting.setChecked(false);
                    callApi(false);
                }else{
                    holder.switchSetting.setChecked(true);
                    callApi(true);
                }

            }
        });

        if(position==0){
            holder.header_icon.setImageResource(R.drawable.ic_manage_account);
        }else if(position==1){
            holder.header_icon.setImageResource(R.drawable.ic_rate_us);
        }else if(position==2){
            holder.header_icon.setImageResource(R.drawable.ic_notification);
        }else if(position==3){
            holder.header_icon.setImageResource(R.drawable.ic_faq);
        }else if(position==4){
            holder.header_icon.setImageResource(R.drawable.ic_feedback);
        }else if(position==5){
            holder.header_icon.setImageResource(R.drawable.ic_about);
        }else if(position==6){
            holder.header_icon.setImageResource(R.drawable.ic_log_out);
        }


        holder.layoutmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position==0){
                    if(miniOption){
                        miniOption=false;
                        //holder.imageMore.setImageResource(R.drawable.ic_arrow_downward);
                        holder.layoutMiniOptions.setVisibility(View.VISIBLE);
                    }else{
                        miniOption=true;
                       // holder.imageMore.setImageResource(R.drawable.right_arrow);
                        holder.layoutMiniOptions.setVisibility(View.GONE);
                    }
                }else if(position==1) {
                    Uri uri = Uri.parse("market://details?id=in.tagteen.tagteen");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    try {
                        mContext.startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                    }
                }
                /*else if(position==2){
                    new NotificationSettings((Activity) mContext);
                }*/else if(position==3){
                    Intent faq=new Intent(mContext,FaqActivity.class);
                    mContext.startActivity(faq);
                }else if(position==4){
                    Intent faq=new Intent(mContext,FeedbackActivity.class);
                    mContext.startActivity(faq);
                }
                else if(position==5){
                    Intent about=new Intent(mContext,AboutActivity.class);
                    mContext.startActivity(about);
                }
                else if(position==6){
                    new LogoutDialog((Activity)mContext);
                }

            }
        });

        holder.layoutChangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // holder.imageMore.setImageResource(R.drawable.right_arrow);
                holder.layoutMiniOptions.setVisibility(View.GONE);
                Intent in=new Intent(mContext, ChangePassWord.class);
                mContext.startActivity(in);
            }
        });
        holder.layoutDeactivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.imageMore.setImageResource(R.drawable.right_arrow);
                holder.layoutMiniOptions.setVisibility(View.GONE);
                Intent in=new Intent(mContext, DeactivateUserAccount.class);
                mContext.startActivity(in);
            }
        });

    }

    private void callApi(final boolean on_off) {
        String userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        Apimethods methods = API_Call_Retrofit.getretrofit(mContext).create(Apimethods.class);
        likeJsonInputModel.setUser_id(userid);
        likeJsonInputModel.setNotification(on_off);
        Call<NotificationResponseModel> call =methods.notification_on_off(userid,likeJsonInputModel,"application/json");
        String url = call.request().url().toString();
        Log.d("url", "url=" + url);
        call.enqueue(new Callback<NotificationResponseModel>() {
            @Override
            public void onResponse(Call<NotificationResponseModel> call, Response<NotificationResponseModel> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    NotificationResponseModel model = response.body();
                    if(model.isSuccess()){
                        if (on_off) {
                            //Toast.makeText(mContext, "NotificationsData ON", Toast.LENGTH_SHORT).show();
                            SaveButtonState_swtch1("active");
                        } else {
                            //Toast.makeText(mContext, "NotificationsData OFF", Toast.LENGTH_SHORT).show();
                            SaveButtonState_swtch1("inactive");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<NotificationResponseModel> call, Throwable t) {
                Utils.showShortToast(mContext, Constants.SERVER_ERROR);
            }
        });
    }

    public void SaveButtonState_swtch1(String bState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("focus_swtch1_value", bState);
        edit.commit();
    }

    public String LoadButtonState_swtch1() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String buttonState = preferences.getString("focus_swtch1_value", "active");
        return buttonState;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

       // private ImageView imageMore, icon;
        TextView title, textNormal;
        Switch switchSetting;
        LinearLayout rightLayout;
        RelativeLayout layoutmain,layoutChangepass,layoutDeactivation;
        LinearLayout layoutMiniOptions;
        ImageView header_icon;
        public ImageViewHolder(View itemView) {
            super(itemView);
            //icon = (ImageView) itemView.findViewById(R.id.icon);
            //imageMore = (ImageView) itemView.findViewById(R.id.imageMore);
            title = (TextView) itemView.findViewById(R.id.title);
            switchSetting = (Switch) itemView.findViewById(R.id.switchSetting);
            textNormal = (TextView) itemView.findViewById(R.id.textNormal);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.rightLayout);
            layoutmain=(RelativeLayout)itemView.findViewById(R.id.layoutmain);
            layoutMiniOptions=(LinearLayout)itemView.findViewById(R.id.layoutMiniOptions);
            layoutChangepass=(RelativeLayout)itemView.findViewById(R.id.layoutChangepass);
            layoutDeactivation=(RelativeLayout)itemView.findViewById(R.id.layoutDeactivation);
            header_icon=(ImageView)itemView.findViewById(R.id.header_icon);
        }
    }


}
