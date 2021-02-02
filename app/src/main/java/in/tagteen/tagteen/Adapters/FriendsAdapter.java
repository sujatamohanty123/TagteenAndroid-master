package in.tagteen.tagteen.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Interfaces.OnConfirmDialogListener;
import in.tagteen.tagteen.Model.AcceptIgnoreFriendJsonInputModel;
import in.tagteen.tagteen.Model.BlockUserInput;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.Model.JsonAddBffModel;
import in.tagteen.tagteen.Model.JsonModelForBff;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SeeFrndPhotoActivity;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import static in.tagteen.tagteen.Fragments.FriendActivity.bfflist;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {
    private List<GetAllUserFriendlist.FriendsUserList> friendlist;
    String[] All =
            {"TAGGED", "BFF", "UNBFF", "Block", "Unfriend", "See Friend", "See Photos", "Message"};
    String[] All_Bff =
            {"TAGGED", "UNBFF", "Block", "Unfriend", "See Friend", "See Photos", "Message"};
    String[] All_UnBff =
            {"TAGGED", "BFF", "Block", "Unfriend", "See Friend", "See Photos", "Message"};
    String[] BFF = {"UNBFF", "BFF"};
    String[] Interest = {"TAGGED", "BFF", "Block", "Unfriend", "See Friend", "See Photos", "Message"};
    String[] Suggestion = {"TAGGED"};
    String[] Request = {"Confirm", "Delete",};
    Context context;
    int flag = 1;
    String userid;
    String token;
    public ArrayAdapter<String> dataAdapter;
    Dialog onLongPressdialog;

    String selectedItem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, tag, commonIntrest, unbffbtn;
        TextView spinnertag;
        public ImageView img;
        public ImageView bffsticker;

        public MyViewHolder(View view) {
            super(view);
            commonIntrest = (TextView) view.findViewById(R.id.common_intrest);
            name = (TextView) view.findViewById(R.id.name);
            unbffbtn = (TextView) view.findViewById(R.id.unbffbtn);
            tag = (TextView) view.findViewById(R.id.tag);
            img = (ImageView) view.findViewById(R.id.img);
            bffsticker = (ImageView) view.findViewById(R.id.bffsticker);
            spinnertag = (TextView) view.findViewById(R.id.number_spinner);
        }
    }

    public FriendsAdapter(Context context, List<GetAllUserFriendlist.FriendsUserList> friendlist,
                          int flag) {
        this.context = context;
        this.friendlist = friendlist;
        this.flag = flag;
        SharedPreferenceSingleton.getInstance().init(context);
        userid =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        token =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GetAllUserFriendlist.FriendsUserList friend = friendlist.get(position);
        if (friend == null) {
            return;
        }
        holder.name.setText(friend.getFirstName() + " " + friend.getLastName());
        holder.tag.setText(friend.getTaggedNumber());
        Utils.loadProfilePic(context, holder.img, friend.getProfileUrl());
    /*
        holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.img.setCornerRadius(60);*/

        if (friend.isBff() == true) {
            holder.bffsticker.setVisibility(View.VISIBLE);
            //dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, All_Bff);
        } else {
            holder.bffsticker.setVisibility(View.GONE);
            //dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, All_UnBff);
        }

        if (flag == 1) {
            holder.spinnertag.setVisibility(View.VISIBLE);
            holder.commonIntrest.setVisibility(View.GONE);
        } else if (flag == 2) {
            holder.spinnertag.setVisibility(View.GONE);
            holder.commonIntrest.setVisibility(View.GONE);
            //dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, All);
        } else if (flag == 3) {
            holder.spinnertag.setVisibility(View.GONE);
            holder.commonIntrest.setVisibility(View.GONE);
            holder.unbffbtn.setVisibility(View.VISIBLE);
            holder.bffsticker.setVisibility(View.VISIBLE);
            //dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, BFF);
        } else if (flag == 4) {
            holder.spinnertag.setVisibility(View.GONE);
            holder.commonIntrest.setVisibility(View.VISIBLE);
            // dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Interest);

        } else if (flag == 5) {
            holder.spinnertag.setVisibility(View.GONE);
            holder.commonIntrest.setVisibility(View.GONE);
            //dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Suggestion);
        } else if (flag == 6) {
            holder.spinnertag.setVisibility(View.VISIBLE);
            holder.commonIntrest.setVisibility(View.GONE);
            holder.spinnertag.setText("Respond");
            //dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Request);
        }

        holder.unbffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Are you sure you want to UNBFF "
                        + friend.getFirstName()
                        + " "
                        + friend.getLastName()
                        + " ?";
                Utils.showConfirmationDialog(context, msg, null, new OnConfirmDialogListener() {
                    @Override
                    public void onConfirmation() {
                        JsonModelForBff jsonObject = new JsonModelForBff();
                        jsonObject.setUser_id(userid);
                        jsonObject.setFriend_user_id(friend.getId());

                        Log.e("removebff", jsonObject + "");
                        CommonApicallModule.callApiforRemoveBff(jsonObject, token, context);
                        friend.setBff(false);
                        friendlist.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled() {

                    }
                });
            }
        });

        holder.spinnertag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLongPressdialog = new Dialog(context);
                onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                onLongPressdialog.setContentView(R.layout.chat_friend_list_dialog);
                LinearLayout BFF = (LinearLayout) onLongPressdialog.findViewById(R.id.hide_container);
                LinearLayout Block = (LinearLayout) onLongPressdialog.findViewById(R.id.lock_container);
                Block.setVisibility(View.GONE);
                LinearLayout Unfriend =
                        (LinearLayout) onLongPressdialog.findViewById(R.id.delete_container);
                LinearLayout Seefriend = (LinearLayout) onLongPressdialog.findViewById(R.id.mute_container);
                LinearLayout Seephotos =
                        (LinearLayout) onLongPressdialog.findViewById(R.id.block_container);
                LinearLayout Message =
                        (LinearLayout) onLongPressdialog.findViewById(R.id.viewProfile_container);
                final TextView bff_txt = (TextView) onLongPressdialog.findViewById(R.id.hide_text);
                final TextView block_txt = (TextView) onLongPressdialog.findViewById(R.id.lock_text);
                final TextView unfrnd_txt = (TextView) onLongPressdialog.findViewById(R.id.delete_text);
                TextView seefrnd_txt = (TextView) onLongPressdialog.findViewById(R.id.mute_text);
                TextView seephoto_txt = (TextView) onLongPressdialog.findViewById(R.id.block_text);
                TextView msg_txt = (TextView) onLongPressdialog.findViewById(R.id.viewProfile_text);
                TextView name_txt = (TextView) onLongPressdialog.findViewById(R.id.name_text);
                ImageView img1 = (ImageView) onLongPressdialog.findViewById(R.id.imageProfilePic);

                name_txt.setText(friend.getFirstName() + " " + friend.getLastName());
                if (friend.isBff() == true) {
                    holder.bffsticker.setVisibility(View.VISIBLE);
                    bff_txt.setText("UNBFF");
                } else {
                    holder.bffsticker.setVisibility(View.GONE);
                    bff_txt.setText("BFF");
                }
                if (flag == 6) {
                    bff_txt.setText("Confirm");
                    block_txt.setText("Delete");
                    Block.setVisibility(View.VISIBLE);
                    unfrnd_txt.setVisibility(View.GONE);
                    seefrnd_txt.setVisibility(View.GONE);
                    seephoto_txt.setVisibility(View.GONE);
                    msg_txt.setVisibility(View.GONE);
                } else {
                    msg_txt.setVisibility(View.GONE);
                    block_txt.setText("Block");
                    unfrnd_txt.setText("Unfriend");
                    seefrnd_txt.setText("See Friends");
                    seephoto_txt.setText("See Photos");
                    msg_txt.setText("Message");
                }

                BFF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAlertDialog(bff_txt.getText().toString(), position, holder, friend, block_txt);
                    }
                });
                Block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAlertDialog(block_txt.getText().toString(), position, holder, friend, block_txt);
                    }
                });
                Unfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAlertDialog(unfrnd_txt.getText().toString(), position, holder, friend, block_txt);
                    }
                });
                Seefriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLongPressdialog.dismiss();
                        Intent gotopprofile = new Intent(context, SeeFrndPhotoActivity.class);
                        gotopprofile.putExtra("user_id", friend.getId());
                        gotopprofile.putExtra("user_name", friend.getFirstName());
                        gotopprofile.putExtra("seeflag", "seefriend");
                        context.startActivity(gotopprofile);
                    }
                });
                Seephotos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLongPressdialog.dismiss();
                        Intent gotopprofile = new Intent(context, SeeFrndPhotoActivity.class);
                        gotopprofile.putExtra("user_id", friend.getId());
                        gotopprofile.putExtra("user_name", friend.getFirstName());
                        gotopprofile.putExtra("seeflag", "seephoto");
                        context.startActivity(gotopprofile);
                    }
                });
                Message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                onLongPressdialog.show();
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.gotoProfile(context, friend.getId());
            }
        });
        holder.tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.gotoProfile(context, friend.getId());
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.gotoProfile(context, friend.getId());
            }
        });
    }

    public void openAlertDialog(final String type, final int position, final MyViewHolder holder,
                                final GetAllUserFriendlist.FriendsUserList friend, final TextView bff_txt) {
        onLongPressdialog.dismiss();
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        String firstname = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.FIRST_NAME);
        String lastname = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.LAST_NAME);
        TextView confirm_ok_btn = (TextView) d.findViewById(R.id.confirm_ok_btn);
        confirm_ok_btn.setVisibility(View.GONE);
        name.setText("Hi " + firstname);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                holder.spinnertag.setText("Tagged");
            }
        });

        if (type.equalsIgnoreCase("BFF")) {
            msg.setText("Are you sure you want to add "
                    + friend.getFirstName()
                    + " "
                    + friend.getLastName()
                    + " as your BFF ?");
            d.show();
        } else if (type.equalsIgnoreCase("UNBFF")) {
            msg.setText("Are you sure you want to UNBFF "
                    + friend.getFirstName()
                    + " "
                    + friend.getLastName()
                    + " ?");
            d.show();
        } else if (type.equalsIgnoreCase("Unfriend")) {
            msg.setText("Are you sure you want to Unfriend "
                    + friend.getFirstName()
                    + " "
                    + friend.getLastName()
                    + " ?");
            d.show();
        } else if (type.equalsIgnoreCase("Confirm")) {
            msg.setText("Are you sure you want to confirm "
                    + friend.getFirstName()
                    + " "
                    + friend.getLastName()
                    + " as a friend ?");
            d.show();
        } else if (type.equalsIgnoreCase("Delete")) {
            msg.setText("Are you sure you want to delete "
                    + friend.getFirstName()
                    + " "
                    + friend.getLastName()
                    + " ?");
            d.show();
        } else if (type.equalsIgnoreCase("Block")) {
            msg.setText("Are you sure you want to Block "
                    + friend.getFirstName()
                    + " "
                    + friend.getLastName()
                    + " ?");
            d.show();
        }
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLongPressdialog.dismiss();
                if (type.equalsIgnoreCase("BFF")) {
                    holder.spinnertag.setText("Tagged");
                    JsonAddBffModel jsonObject = new JsonAddBffModel();
                    jsonObject.setUser_id(userid);
                    ArrayList<String> listof_friendusedid = new ArrayList<>();
                    listof_friendusedid.add(friendlist.get(position).getId());
                    jsonObject.setFrnduseridarray(listof_friendusedid);
                    bfflist.add(0, friendlist.get(position));
                    friend.setBff(true);
                    CommonApicallModule.callApiforAddBff(jsonObject, token, context);
                    bff_txt.setText("UNBFF");
                   /* dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, All_Bff);
                    dataAdapter.notifyDataSetChanged();*/
                    notifyDataSetChanged();
                    d.dismiss();
                }

                if (type.equalsIgnoreCase("UNBFF")) {
                    holder.spinnertag.setText("Tagged");
                    JsonModelForBff jsonObjectremove = new JsonModelForBff();
                    jsonObjectremove.setUser_id(userid);
                    jsonObjectremove.setFriend_user_id(friend.getId());
                    /*dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, All_UnBff);
                    dataAdapter.notifyDataSetChanged();*/
                    bff_txt.setText("BFF");
                    if (bfflist.contains(friendlist.get(position))) {
                        bfflist.remove(friendlist.get(position));
                        friend.setBff(false);
                    }
                    CommonApicallModule.callApiforRemoveBff(jsonObjectremove, token, context);
                    notifyDataSetChanged();
                    d.dismiss();
                }

                if (type.equalsIgnoreCase("Unfriend")) {
                    holder.spinnertag.setText("Tagged");
                    holder.bffsticker.setVisibility(View.GONE);
                    JsonModelForBff jsonObjectremove = new JsonModelForBff();
                    jsonObjectremove.setUser_id(userid);
                    jsonObjectremove.setFriend_user_id(friend.getId());
                   /* dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, All_UnBff);
                    dataAdapter.notifyDataSetChanged();*/
                    bff_txt.setText("BFF");
                    CommonApicallModule.callForUnfriend(jsonObjectremove, token, context);
                    friendlist.remove(position);
                    notifyDataSetChanged();
                    d.dismiss();
                }
                if (type.equalsIgnoreCase("Confirm")) {
                    holder.spinnertag.setText("Respond");
                    holder.bffsticker.setVisibility(View.GONE);
                    AcceptIgnoreFriendJsonInputModel jsonfriendadd = new AcceptIgnoreFriendJsonInputModel();
                    jsonfriendadd.setRequest_user_id(friend.getId());
                    jsonfriendadd.setFriend_user_id(userid);
                    jsonfriendadd.setRequest_status(true);
                    CommonApicallModule.accept_ignore_request(jsonfriendadd, context);
                    friendlist.remove(position);
                    notifyDataSetChanged();
                    d.dismiss();
                }
                if (type.equalsIgnoreCase("Delete")) {
                    holder.spinnertag.setText("Respond");
                    holder.bffsticker.setVisibility(View.GONE);
                    AcceptIgnoreFriendJsonInputModel jsonfriendadd = new AcceptIgnoreFriendJsonInputModel();
                    jsonfriendadd.setRequest_user_id(friend.getId());
                    jsonfriendadd.setFriend_user_id(userid);
                    jsonfriendadd.setRequest_status(false);
                    CommonApicallModule.accept_ignore_request(jsonfriendadd, context);
                    friendlist.remove(position);
                    notifyDataSetChanged();
                    d.dismiss();
                }
                if (type.equalsIgnoreCase("Block")) {
                    holder.spinnertag.setText("Tagged");
                    holder.bffsticker.setVisibility(View.GONE);
                    BlockUserInput jsonfriendadd = new BlockUserInput();
                    jsonfriendadd.setUser_id(userid);
                    jsonfriendadd.setFriend_id(friend.getId());
                    CommonApicallModule.blockFriend(jsonfriendadd, context);
                    friendlist.remove(position);
                    notifyDataSetChanged();
                    d.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return friendlist.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
