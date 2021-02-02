package in.tagteen.tagteen.Fragments.share;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.videoCreatePostJsonResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 29/10/17.
 */

public class ShareDialog {

    String option[]={"Public","Private","Friends","BFF","Supporters"};
    TextView content_text;
    ImageView img,play_icon,sharePost;
    CreatePostJsonInputModel json = new CreatePostJsonInputModel();
    String categorie_id,post_owner_id,contentoriginal,date_created,owner_post_type_id,owner_post_id;
    ArrayList<CreatePostJsonInputModel.ImageDataAllToSend> imaglist=new ArrayList<>();
    ArrayList<CreatePostJsonInputModel.ImageDataAllToSend> vid_thumb_imaglist=new ArrayList<>();
    CreatePostJsonInputModel.Videodata videolist=new CreatePostJsonInputModel.Videodata();
    String flag="";

    public ShareDialog(final Context context, GetPostResponseModel.PostDetails youthList,String flag, final OnCallbackListener onCallbackListener) {
        imaglist.clear();
        vid_thumb_imaglist.clear();
        this.flag=flag;
        final Dialog d = new Dialog(context);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.share_dialog);
        sharePost=(ImageView)d.findViewById(R.id.sharePost);
        play_icon=(ImageView)d.findViewById(
                R.id.ic_play);
        img=(ImageView)d.findViewById(R.id.img);
        content_text=(TextView) d.findViewById(R.id.content);
        final Spinner shareOption=(Spinner)d.findViewById(R.id.shareOption);

        ArrayAdapter<String> a =new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, option);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shareOption.setAdapter(a);
        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                callapiPost_share(context, shareOption, onCallbackListener);
            }
        });

        try {
            categorie_id = String.valueOf(youthList.getCategorie_id());
            contentoriginal = youthList.getContent();
            date_created = String.valueOf(youthList.getDateCreated());
            owner_post_type_id=youthList.getPost_type_id();
            owner_post_id=youthList.getId();
            post_owner_id =youthList.getPostCreatorId();

            String imageUrl = null;
            for (int i = 0; i < youthList.getImage().size(); i++) {
                if (i == 0 && youthList.getImage() != null && youthList.getImage().isEmpty() == false) {
                    imageUrl = youthList.getImage().get(i).getUrl();
                }
                CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                imagedata.setUrl(youthList.getImage().get(i).getUrl());
                imagedata.setHeight(String.valueOf(youthList.getImage().get(i).getHeight()));
                imagedata.setWidth(String.valueOf(youthList.getImage().get(i).getWidth()));
                imaglist.add(imagedata);
            }
            for (int i = 0; i < youthList.getVideoThumbnails().size(); i++) {
                if (i == 0 && youthList.getVideoThumbnails() != null && youthList.getVideoThumbnails().isEmpty() == false) {
                    imageUrl = youthList.getVideoThumbnails().get(i).getUrl();
                }
                CreatePostJsonInputModel.ImageDataAllToSend vid_thumb_imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                vid_thumb_imagedata.setUrl(youthList.getVideoThumbnails().get(i).getUrl());
                vid_thumb_imagedata.setHeight(String.valueOf(youthList.getVideoThumbnails().get(i).getHeight()));
                vid_thumb_imagedata.setWidth(String.valueOf(youthList.getVideoThumbnails().get(i).getWidth()));
                vid_thumb_imaglist.add(vid_thumb_imagedata);
            }
            videolist.setUrl(youthList.getVideo().getUrl());
            videolist.setHeight(String.valueOf(youthList.getVideo().getHeight()));
            videolist.setWidth(String.valueOf(youthList.getVideo().getWidth()));

            if (contentoriginal.equalsIgnoreCase("") || contentoriginal.equalsIgnoreCase(null)) {
                content_text.setVisibility(View.GONE);
            } else {
                content_text.setText(contentoriginal);
            }
            if (imageUrl != null) {
                Utils.loadImageUsingGlideCenterCrop(context, img, imageUrl);
            }

            if(youthList.getVideo()==null){
                play_icon.setVisibility(View.GONE);
            }else {
                play_icon.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        d.show();
    }

    public ShareDialog(final Context context, String output, final OnCallbackListener onCallbackListener) {
        imaglist.clear();
        vid_thumb_imaglist.clear();
        final Dialog d = new Dialog(context);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.share_dialog);
        sharePost = (ImageView)d.findViewById(R.id.sharePost);
        play_icon = (ImageView)d.findViewById(R.id.ic_play);
        img = (ImageView)d.findViewById(R.id.img);
        content_text=(TextView) d.findViewById(R.id.content);
        play_icon.setVisibility(View.GONE);
        final Spinner shareOption=(Spinner)d.findViewById(R.id.shareOption);
        ArrayAdapter<String> a =new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, option);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shareOption.setAdapter(a);
        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                callapiPost_share(context, shareOption, onCallbackListener);
            }
        });

        try {
                JSONObject jsonObject = new JSONObject(output);
                JSONObject data = jsonObject.getJSONObject("data");
                categorie_id = data.getString("categorie_id");
                contentoriginal = data.getString("content");
                date_created = data.getString("date_created");
                post_owner_id = data.getString("post_creator_id");
                owner_post_id=data.getString("_id");
                owner_post_type_id="2";
                JSONArray image = data.getJSONArray("image");

                for (int i = 0; i < image.length(); i++) {
                    JSONObject object = image.getJSONObject(i);
                    CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                    imagedata.setUrl(object.getString("url"));
                    imagedata.setHeight(object.getString("height"));
                    imagedata.setWidth(object.getString("width"));
                    imaglist.add(imagedata);
                }


                if (contentoriginal.equalsIgnoreCase("") || contentoriginal.equalsIgnoreCase(null)) {
                    content_text.setVisibility(View.GONE);
                } else {
                    content_text.setText(contentoriginal);
                }
                Utils.loadImageUsingGlideCenterCrop(context, this.img, this.imaglist.get(0).getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }

        d.show();
    }

    public ShareDialog(
            final Context context,
            videoCreatePostJsonResponseModel.Data mainvideo,
            String flag,
            final OnCallbackListener onCallbackListener) {
        imaglist.clear();
        vid_thumb_imaglist.clear();
        this.flag=flag;
        final Dialog d = new Dialog(context);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.share_dialog);
        sharePost=(ImageView)d.findViewById(R.id.sharePost);
        play_icon=(ImageView)d.findViewById(
                R.id.ic_play);
        img=(ImageView)d.findViewById(R.id.img);
        content_text=(TextView) d.findViewById(R.id.content);
        final Spinner shareOption=(Spinner)d.findViewById(R.id.shareOption);

        ArrayAdapter<String> a =new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, option);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shareOption.setAdapter(a);
        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                callapiPost_share(context, shareOption, onCallbackListener);
            }
        });

        try {
            categorie_id = String.valueOf(mainvideo.getCategorie_id());
            contentoriginal = mainvideo.getContent();
            date_created = mainvideo.getDateCreated();
            post_owner_id =mainvideo.getPostCreatorId();

            for (int i = 0; i < mainvideo.getImage().size(); i++) {
                CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                imagedata.setUrl(mainvideo.getImage().get(i).getUrl());
                imagedata.setHeight(String.valueOf(mainvideo.getImage().get(i).getHeight()));
                imagedata.setWidth(String.valueOf(mainvideo.getImage().get(i).getWidth()));
                imaglist.add(imagedata);
            }
            for (int i = 0; i < mainvideo.getVideo_thumbnail().size(); i++) {
                CreatePostJsonInputModel.ImageDataAllToSend vid_thumb_imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                vid_thumb_imagedata.setUrl(mainvideo.getVideo_thumbnail().get(i).getUrl());
                vid_thumb_imagedata.setHeight(String.valueOf(mainvideo.getVideo_thumbnail().get(i).getHeight()));
                vid_thumb_imagedata.setWidth(String.valueOf(mainvideo.getVideo_thumbnail().get(i).getWidth()));
                vid_thumb_imaglist.add(vid_thumb_imagedata);
            }
            videolist.setUrl(mainvideo.getVideo().getUrl());
            videolist.setHeight(String.valueOf(mainvideo.getVideo().getHeight()));
            videolist.setWidth(String.valueOf(mainvideo.getVideo().getWidth()));

            if (contentoriginal.equalsIgnoreCase("") || contentoriginal.equalsIgnoreCase(null)) {
                content_text.setVisibility(View.GONE);
            } else {
                content_text.setText(contentoriginal);
            }

            Utils.loadImageUsingGlideCenterCrop(context, this.img, this.imaglist.get(0).getUrl());

            if (mainvideo.getVideo()==null){
                play_icon.setVisibility(View.GONE);
            } else {
                play_icon.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        d.show();
    }

    public ShareDialog(final Context context, SectionDataModel dataList, final OnCallbackListener onCallbackListener) {
        imaglist.clear();
        vid_thumb_imaglist.clear();
        final Dialog d = new Dialog(context);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.share_dialog);
        sharePost=(ImageView)d.findViewById(R.id.sharePost);
        play_icon=(ImageView)d.findViewById(R.id.ic_play);
        img=(ImageView)d.findViewById(R.id.img);
        content_text=(TextView) d.findViewById(R.id.content);
        final Spinner shareOption=(Spinner)d.findViewById(R.id.shareOption);

        ArrayAdapter<String> a =new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, option);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shareOption.setAdapter(a);
        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                callapiPost_share(context, shareOption, onCallbackListener);
            }
        });

        try {
            categorie_id = String.valueOf(dataList.getCategory_id());
            contentoriginal = dataList.getText_description();
            date_created = String.valueOf(dataList.getPost_created_date_time());
            post_owner_id =dataList.getPost_userid();
            owner_post_id =dataList.getPostid();
            owner_post_type_id=dataList.getPost_type_id();

            String imageUrl = null;
            for (int i = 0; i < dataList.getPost_image_createdby_creator_url().size(); i++) {
                if (i == 0 && dataList.getPost_image_createdby_creator_url() != null && dataList.getPost_image_createdby_creator_url().isEmpty() == false) {
                    imageUrl = dataList.getPost_image_createdby_creator_url().get(i);
                }
                CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                imagedata.setUrl(dataList.getPost_image_createdby_creator_url().get(i));
                imagedata.setHeight(String.valueOf(dataList.getPost_image_createdby_creator_height().get(i)));
                imagedata.setWidth(String.valueOf(dataList.getPost_image_createdby_creator_weidth().get(i)));
                imaglist.add(imagedata);
            }
            if(dataList.getPost_video_url()!=null) {
                videolist.setUrl(dataList.getPost_video_url());
                videolist.setHeight(String.valueOf(dataList.getPost_video_height()));
                videolist.setWidth(String.valueOf(dataList.getPost_video_width()));
            }
            for (int i = 0; i < dataList.getPost_video_thumb_createdby_creator().size(); i++) {
                if (i == 0 && dataList.getPost_video_thumb_createdby_creator() != null && dataList.getPost_video_thumb_createdby_creator().isEmpty() == false) {
                    imageUrl = dataList.getPost_video_thumb_createdby_creator().get(i);
                }
                CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                imagedata.setUrl(dataList.getPost_video_thumb_createdby_creator().get(i));
                imagedata.setHeight(String.valueOf(dataList.getPost_video_thumb_createdby_creator_height().get(i)));
                imagedata.setWidth(String.valueOf(dataList.getPost_video_thumb_createdby_creator_weidth().get(i)));
                vid_thumb_imaglist.add(imagedata);
            }

            if (contentoriginal.equalsIgnoreCase("") || contentoriginal.equalsIgnoreCase(null)) {
                content_text.setVisibility(View.GONE);
            } else {
                content_text.setText(contentoriginal);
            }
            if (imageUrl != null) {
                Utils.loadImageUsingGlideCenterCrop(context, this.img, imageUrl);
            }

            if(dataList.getPost_video_url()==null){
                play_icon.setVisibility(View.GONE);
            }else {
                play_icon.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        d.show();
    }

    private void callapiPost_share(final Context context, Spinner shareOption, final OnCallbackListener onCallbackListener) {
        json = new CreatePostJsonInputModel();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        json.setPostCreatorId(userid);
        json.setPostOwnerId(post_owner_id);
        json.setContent(contentoriginal);
        json.setOwner_post_type_id(owner_post_type_id);
        json.setOwnerpostId(owner_post_id);
        json.setOriginalpostDate(date_created);
        json.setType(1);
        if(shareOption.getSelectedItem().toString().equalsIgnoreCase("public")) {
            json.setView_to(1);
        }else if(shareOption.getSelectedItem().toString().equalsIgnoreCase("private")) {
            json.setView_to(5);
        }else if(shareOption.getSelectedItem().toString().equalsIgnoreCase("friends")) {
            json.setView_to(2);
        }else if(shareOption.getSelectedItem().toString().equalsIgnoreCase("BFF")) {
            json.setView_to(4);
        }else if(shareOption.getSelectedItem().toString().equalsIgnoreCase("Fans")) {
            json.setView_to(3);
        }else{
            json.setView_to(1);
        }
        //FIXME: for now share it all public
        json.setView_to(1);

        int categoryId = 0;
        try {
            categoryId = Integer.parseInt(this.categorie_id);
        } catch (Exception e) {
            // do nothing
        }
        json.setCategorie_id(categoryId);
        json.setImage(imaglist);
        json.setVideo(videolist);
        json.setVideo_thumbnail(vid_thumb_imaglist);
        json.setShareto(true);

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpost(json, token);
        String url = call.request().url().toString();
        Log.d("url", "url=" + url);
        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                if (response.code() == 200) {
                    Utils.showShortToast(context, "Posted successfully");
                    if (onCallbackListener != null) {
                        onCallbackListener.OnComplete();
                    }
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
