package in.tagteen.tagteen.apimodule_retrofit;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.AcceptIgnoreFriendJsonInputModel;
import in.tagteen.tagteen.Model.Add_Remove_Bff;
import in.tagteen.tagteen.Model.BlockUserInput;
import in.tagteen.tagteen.Model.CommentLikeDislikeInputJson;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.Model.GetReactionModel;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.JsonAddBffModel;
import in.tagteen.tagteen.Model.JsonModelForBff;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.Model.ReactionInputJson;
import in.tagteen.tagteen.Model.SearchModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.Commons;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommonApicallModule {

    public Context context;

    public CommonApicallModule(Context context) {
        this.context = context;
    }

    public static void callApiforRemoveBff(JsonModelForBff jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.setremovebff(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, add_remove_bff.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    //Toast.makeText(context, "no api call", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }

    public static void callApiforAddBff(JsonAddBffModel jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.setaddbff(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, add_remove_bff.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }

    public static void callApiForLike(LikeJsonInputModel jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.addlike(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, add_remove_bff.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }

    public static void callApiInsertCommentLike(CommentLikeDislikeInputJson jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.insertCommentlike(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, " insert ic_svg_comment like", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });
    }

    public static void callApiforInsertCommentDislike(CommentLikeDislikeInputJson jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.insertCommentdislike(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, "insert ic_svg_comment dislike", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });
    }

    public static void callForUnfriend(JsonModelForBff jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.setUnfriend(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }

    public static void accept_ignore_request(final AcceptIgnoreFriendJsonInputModel acceptIgnoreFriendJsonInputModel, final Context context) {
        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Call<Add_Remove_Bff> call = methods.setAcceptIgonreRequest(acceptIgnoreFriendJsonInputModel, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {

                if (response.code() == 200) {
                    boolean req_status = acceptIgnoreFriendJsonInputModel.isRequest_status();
                    if (req_status == true) {
                        Toast.makeText(context, "friend request accepted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "friend request deleted", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == 401) {
                    //Toast.makeText(context, "acceptignore no api call", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }

    public static void blockFriend(final BlockUserInput acceptIgnoreFriendJsonInputModel, final Context context) {
        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Call<Add_Remove_Bff> call = methods.blockUnBlockUser(acceptIgnoreFriendJsonInputModel, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {

                if (response.code() == 200) {
                    Add_Remove_Bff obj = response.body();
                    boolean req_status = obj.getSuccess();
                    if (req_status == true) {
                        //Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error while Updating ", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() == 401) {
                    //Toast.makeText(context, "acceptignore no api call", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }

    public static void insertCoolSwagDebNerd(InsertCoolModel jsonObject, String token, final Context context) {

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.insertCoolSwagDebNerd(jsonObject, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, "Action updated", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });

    }
    public static void deleteCoolSwagDebNerd(InsertCoolModel json, String token,final Context context) {
        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Add_Remove_Bff> call = methods.deleteCoolSwagDebNerd(json, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(Call<Add_Remove_Bff> call, Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    //Toast.makeText(context, "Action updated", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Utils.showShortToast(context, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Add_Remove_Bff> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });
    }
}
