package in.tagteen.tagteen.apimodule_retrofit;

import in.tagteen.tagteen.Fragments.beans.CampusLiveInputJson;
import in.tagteen.tagteen.Fragments.beans.SearchInputJson;
import in.tagteen.tagteen.Fragments.beans.SearchShowRoomBean;
import in.tagteen.tagteen.Model.AcademicInfos;
import in.tagteen.tagteen.Model.AcceptIgnoreFriendJsonInputModel;
import in.tagteen.tagteen.Model.AddPostTeenFeedModel;
import in.tagteen.tagteen.Model.Add_Remove_Bff;
import in.tagteen.tagteen.Model.AllSelfyList;
import in.tagteen.tagteen.Model.BlockUserInput;
import in.tagteen.tagteen.Model.ChatHistoryResponseModel;
import in.tagteen.tagteen.Model.CommentLikeDislikeInputJson;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.Model.DeleteCommentModel;
import in.tagteen.tagteen.Model.EducationsModel;
import in.tagteen.tagteen.Model.FCMTokenUpdateModel;
import in.tagteen.tagteen.Model.FeedbackModel;
import in.tagteen.tagteen.Model.FeedbackResponseModel;
import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.Model.GetAllCommentList;
import in.tagteen.tagteen.Model.GetAllLReact;
import in.tagteen.tagteen.Model.GetAllLike_Cool_forpost;
import in.tagteen.tagteen.Model.GetAllLike_Cool_forpost1;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.GetReactionModel;
import in.tagteen.tagteen.Model.InsertComment_JsonInputModel;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.JsonAddBffModel;
import in.tagteen.tagteen.Model.JsonModelForBff;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.Model.MySelfyPostList;
import in.tagteen.tagteen.Model.NotificationModel;
import in.tagteen.tagteen.Model.NotificationResponseModel;
import in.tagteen.tagteen.Model.NotificationsData;
import in.tagteen.tagteen.Model.PhoneVerificationModel;
import in.tagteen.tagteen.Model.PhoneVerificationRequestModel;
import in.tagteen.tagteen.Model.PostLikesInputModel;
import in.tagteen.tagteen.Model.PostLikesModel;
import in.tagteen.tagteen.Model.PostResponseModel;
import in.tagteen.tagteen.Model.ReactionInputJson;
import in.tagteen.tagteen.Model.RewardsInfoModels;
import in.tagteen.tagteen.Model.RewardsPaymentOptionInputModel;
import in.tagteen.tagteen.Model.SearchModel;
import in.tagteen.tagteen.Model.ShoutOutModel;
import in.tagteen.tagteen.Model.SimpleActionModel;
import in.tagteen.tagteen.Model.UserPhotosModel;
import in.tagteen.tagteen.Model.UserURockInputJson;
import in.tagteen.tagteen.Model.UserURocksModel;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.Model.getChatJSONInputModel;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswers;
import in.tagteen.tagteen.Model.knowledge.KnowledgeCategories;
import in.tagteen.tagteen.Model.knowledge.KnowledgePostResponse;
import in.tagteen.tagteen.Model.knowledge.KnowledgeQuestions;
import in.tagteen.tagteen.Model.placements.ApplyPlacementResponse;
import in.tagteen.tagteen.Model.placements.Placements;
import in.tagteen.tagteen.Model.videoCreatePostJsonResponseModel;
import in.tagteen.tagteen.Model.webshows.Checksum;
import in.tagteen.tagteen.Model.webshows.Payment;
import in.tagteen.tagteen.Model.webshows.PaymentStatus;
import in.tagteen.tagteen.Model.webshows.TxnTokenResponse;
import in.tagteen.tagteen.apimodule_retrofit.response.register.RegisterPostModel;
import in.tagteen.tagteen.apimodule_retrofit.response.register.RegisterUserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Apimethods {

  @Headers("Content-Type: application/json")
  @POST("register/")
  Call<RegisterUserResponse> register(@Body RegisterPostModel registerPostModel);

  @GET("get_all_user_friend_list/{userid}")
  Call<GetAllUserFriendlist> getalluserfriendslist(@Path("userid") String userid,
      @Header("token") String token);

  @GET("search_user/{searchparam}")
  Call<SearchModel> getsearchlist(@Path("searchparam") String searchparam,
      @Header("token") String token);

  @POST("remove_bff/")
  Call<Add_Remove_Bff> setremovebff(@Body JsonModelForBff removedata,
      @Header("token") String token);

  @POST("add_bff/")
  Call<Add_Remove_Bff> setaddbff(@Body JsonAddBffModel adddata, @Header("token") String token);

  @POST("add_post/")
  Call<CreatePostJsonResponseModel> setpost(@Body CreatePostJsonInputModel adddata,
      @Header("token") String token);

  @POST("add_post/")
  Call<CreatePostJsonResponseModel> setpostTeenFeed(@Body AddPostTeenFeedModel adddata,
      @Header("token") String token);

  @GET("get_all_post/{userid}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> getPost(@Path("userid") String userid, @Path("page") int page,
      @Path("limit") int limit, @Path("type") int type, @Header("token") String token);

  @GET("get_user_all_teenfeed/{userid}/{page}/{limit}")
  Call<GetPostResponseModel> get_teenfeed_articlePost(@Path("userid") String userid,
      @Path("page") int page, @Path("limit") int limit, @Header("token") String token);

  @GET("get_all_user_post/{userid}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> get_user_Post(@Path("userid") String userid, @Path("page") int page,
      @Path("limit") int limit, @Path("type") int type, @Header("token") String token,
      @Header("login_user_id") String login_user_id);

  @GET("get_all_user_post_based_on_friend/{userid}/{friend_user_id}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> get_user_Post_friend_profile(@Path("userid") String userid,
      @Path("friend_user_id") String friend_user_id, @Path("page") int page,
      @Path("limit") int limit, @Path("type") int type);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/user/get_all_related_videos/{categorie_id}/{page}/{limit}/{userid}")
  Call<GetPostResponseModel> getRelatedVideoPost(@Path("categorie_id") int categorie_id,
      @Path("page") int page, @Path("limit") int limit, @Path("userid") String userid,
      @Header("token") String token);

  @GET("get_all_showroom_post/{userid}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> getallshowroomvid(@Path("userid") String userid,
      @Path("page") int page, @Path("limit") int limit, @Path("type") int type,  @Header("token") String token);

  @GET("get_all_showroom_post_user/{userid}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> getAllShowroomPostRanked(@Path("userid") String userid,
      @Path("page") int page, @Path("limit") int limit, @Path("type") int type,
      @Header("token") String token);
  @GET("get_all_showroom_post_user_celebrity/{userid}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> getAllShowroomPostCelebrity(@Path("userid") String userid,@Path("page") int page, @Path("limit") int limit, @Path("type") int type,
                                                      @Header("token") String token);

  @GET("https://pro-api.tagteen.in/write/api/v1.0/post/get_all_showroom_post_user_following/{userid}/{page}/{limit}/{type}")
  Call<GetPostResponseModel> getAllShowroomFollowingPosts(
          @Path("userid") String userid, @Path("page") int page, @Path("limit") int limit, @Path("type") int type,
          @Header("token") String token);


  @GET("get_all_showroom_post/{userid}/{page}/{limit}/{type}")
    //https://pro-api.tagteen.in/write/api/v1.0/user/get_all_showroom_post/5df5842b39f04005bfad1db9/1/10/3?lang_type_id=1
  Call<GetPostResponseModel> getShowroomVideosByLanguage(
      @Path("userid") String userid, @Path("page") int page, @Path("limit") int limit,
      @Path("type") int type, @Query("lang_type_id") int langId, @Header("token") String token);

  @POST("insert_like/")
  Call<Add_Remove_Bff> addlike(@Body LikeJsonInputModel likeinput, @Header("token") String token);

  @POST("get_all_chat_history/")
  Call<ChatHistoryResponseModel> chatHistory(@Body getChatJSONInputModel chatinput,
      @Header("token") String token);

  @POST("insert_comment/")
  Call<Add_Remove_Bff> insertComment(@Body InsertComment_JsonInputModel likeinput,
      @Header("token") String token);

  @GET("get_all_comment/{postid}/{page}/{limit}/")
  Call<GetAllCommentList> getComment(@Path("postid") String postid, @Path("page") int page,
      @Path("limit") int limit, @Header("token") String token);

  @GET("get_post/{postid}/{userid}")
  Call<PostResponseModel> getPostDetails(
          @Path("postid") String postid, @Path("userid") String userid, @Header("token") String token);

  @POST("insert_cool_reaction/")
  Call<Add_Remove_Bff> setCool(@Body LikeJsonInputModel likeinput, @Header("token") String token);

  @POST("un_cool_reaction/")
  Call<Add_Remove_Bff> setUncool(@Body LikeJsonInputModel likeinput, @Header("token") String token);

  @GET("get_post_like/{postid}/{page}/{limit}/")
  Call<GetAllLike_Cool_forpost> getAllLike(@Path("postid") String postid, @Path("page") int page,
      @Path("limit") int limit, @Header("token") String token);

  @GET("get_post_like_nerd_swag_dab_cool_action/{postid}/{page}/{limit}/")
  Call<GetAllLReact> getAllReact(@Path("postid") String postid, @Path("page") int page,
      @Path("limit") int limit, @Header("token") String token);

  @POST("get_post_cool_user/")
  Call<GetAllLike_Cool_forpost1> getAllCool(@Body ReactionInputJson react,
      @Header("token") String token);

  @POST("get_post_sweg_user/")
  Call<GetAllLike_Cool_forpost1> getAllSweg(@Body ReactionInputJson react,
      @Header("token") String token);

  @POST("get_post_nerd_user/")
  Call<GetAllLike_Cool_forpost1> getAllNerd(@Body ReactionInputJson react,
      @Header("token") String token);

  @POST("get_post_dab_user/")
  Call<GetAllLike_Cool_forpost1> getAllDab(@Body ReactionInputJson react,
      @Header("token") String token);

  @POST("insert_comment_like/")
  Call<Add_Remove_Bff> insertCommentlike(@Body CommentLikeDislikeInputJson likeinput,
      @Header("token") String token);

  @POST("comment_dis_like/")
  Call<Add_Remove_Bff> insertCommentdislike(@Body CommentLikeDislikeInputJson dislike,
      @Header("token") String token);

  @GET("get_user_fan_list/{userid}/{page}/{limit}")
  Call<GetFanList> getUserFanList(@Path("userid") String userid, @Path("page") int page,
      @Path("limit") int limit, @Header("token") String token);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/user/get_user_be_fan_list/{userid}/{page}/{limit}")
  Call<GetFanList> getMyFanList(@Path("userid") String userid, @Path("page") int page,
      @Path("limit") int limit, @Header("token") String token);

  @GET("search_user/{searchparam}")
  Call<FriendSeach> getFriendsearchlist(@Path("searchparam") String searchparam,
      @Header("token") String token);

  @POST("un_friend/")
  Call<Add_Remove_Bff> setUnfriend(@Body JsonModelForBff json, @Header("token") String token);

  @POST("accept_ignored_friend_request/")
  Call<Add_Remove_Bff> setAcceptIgonreRequest(@Body AcceptIgnoreFriendJsonInputModel json,
      @Header("token") String token);

  @POST("block_user/")
  Call<Add_Remove_Bff> blockUnBlockUser(@Body BlockUserInput json, @Header("token") String token);

  @POST("insert_cool_swed_nerd_dab/")
  Call<Add_Remove_Bff> insertCoolSwagDebNerd(@Body InsertCoolModel likeinput,
      @Header("token") String token);

  @POST("delete_post_cool_swed_nerd_dab_like/")
  Call<Add_Remove_Bff> deleteCoolSwagDebNerd(@Body InsertCoolModel likeinput,
      @Header("token") String token);

  @GET("get_all_user_photos/{userid}/{page}/{limit}")
  Call<UserPhotosModel> getUserPhotos_loginuser(@Path("userid") String userid,
      @Path("page") int page, @Path("limit") int limit, @Header("token") String token);

  @GET("get_all_user_post_based_on_friend_images/{userid}/{friend_user_id}/{page}/{limit}")
  Call<UserPhotosModel> getUserPhotos(@Path("userid") String userid,
      @Path("friend_user_id") String friend_user_id, @Path("page") int page,
      @Path("limit") int limit);

  @GET("get_all_user_videos/{userid}/{page}/{limit}")
  Call<GetPostResponseModel> getUserVideos(@Path("userid") String userid, @Path("page") int page,
      @Path("limit") int limit, @Header("token") String token);

  @GET("get_all_selfie_list/{userid}/{page}")
  Call<AllSelfyList> getSelfyList(@Path("userid") String userid, @Path("page") int page,
      @Header("token") String token);

  @GET("get_my_selfie_post/{userid}/1")
  Call<MySelfyPostList> getMySelfyPost(@Path("userid") String userid,
      @Header("token") String token);

  @POST("search_showroom/")
  Call<SearchShowRoomBean> searchShowrooms(@Body SearchInputJson json,
      @Header("token") String token);

  @POST("get_cool_swed_nerd_dab_count/")
  Call<GetReactionModel> get_reacts(@Body ReactionInputJson json, @Header("token") String token);

  @POST("delete_only_comment/{comment_id}")
  Call<SearchModel> delete_comment(@Path("comment_id") String comment_id,
      @Body DeleteCommentModel json);

  @POST("insert_feed_back/{userid}")
  Call<FeedbackResponseModel> insert_feedback(@Path("userid") String userid,
      @Body FeedbackModel json, @Header("Content-Type") String content_type);

  @POST("notification_on_off/{userid}")
  Call<NotificationResponseModel> notification_on_off(@Path("userid") String userid,
      @Body NotificationModel json, @Header("Content-Type") String content_type);

  @POST("get_user_gets_rocks_count/{userid}")
  Call<UserURocksModel> getUserURocksCount(@Body UserURockInputJson json,
      @Header("token") String token);

  @POST("update_phonepe_paytm_google_pay/{userid}")
  Call<SimpleActionModel> updateRewardsPaymentOption(
      @Body RewardsPaymentOptionInputModel inputJson);

  @POST("search_education_details")
  Call<AcademicInfos> searchEducationDetails(@Body SearchInputJson json);

  @POST("verifiy_mobile_number")
  Call<PhoneVerificationModel> verifyMobileNumber(@Body PhoneVerificationRequestModel json);

  @GET("get_education_names")
  Call<EducationsModel> getEduactionNames();

  @POST("get_campus_live_post/{search_type}/{page}/{limit}")
  Call<GetPostResponseModel> getCampusLivePosts(@Path("search_type") int searchType,
      @Path("page") int page, @Path("limit") int limit, @Body CampusLiveInputJson inputJson);

  @POST("insert_education_details")
  Call<SearchModel> insertEducationDetails(@Body AcademicInfos.AcademicInfo academicInfo);

  @GET("get_all_notification/{userid}")
  Call<NotificationsData> getAllNotifications(@Path("userid") String userid,
      @Header("token") String token);

  //tony00
  @POST("api_token")
  Call<SimpleActionModel> updateFCMToken(@Header("token") String token,
      @Body FCMTokenUpdateModel model);

  @GET("https://pro-api.tagteen.in/write/api/v1.0/post/get_showroom_video_info")
  Call<RewardsInfoModels> getShowroomVideoInfo();

  @POST("https://pro-api.tagteen.in/write/api/v1.0/post/get_post_video_count_by_date")
  Call<PostLikesModel> getPostVideoCountByDate(@Body PostLikesInputModel inputModel);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/user/get_verified_users/{limit}")
  Call<FriendSeach> getLatestVerifiedUsers(@Path("limit") int limit);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/post/get_top_videos")
  Call<GetPostResponseModel> getTopVideos(@Query("user_id") String userId,
      @Query("limit") int limit);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/post/get_top_videos")
  Call<GetPostResponseModel> getCategoryVideos(@Query("user_id") String userId,
      @Query("limit") int limit, @Query("categorie_id") int categoryId,
      @Query("select") int select);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/trending/find_all_trending_post")
  Call<GetPostResponseModel> getTrendingVideos(@Query("user_id") String userId);

  @GET("https://pro-api.tagteen.in/read/api/v1.0/user/get_ranked_users")
  Call<FriendSeach> getAllRockstars();

  @GET("https://pro-api.tagteen.in/read/api/v1.0/webshows/get_webshow_token")
  Call<SimpleActionModel> getWebshowToken();

  @GET("https://pro-api.tagteen.in/read/api/v1.0/webshows/get_webshows/{page}/{limit}")
  Call<WebshowModel> getWebshowVideos(@Path("page") int page, @Path("limit") int limit,
      @Query("webshows_type_id") int typeId, @Query("user_id") String userId);

  //// knowledge apis
  //@GET("http://3.6.38.217/api/register")
  //Call<Void> registerUser(@Query("id") String userId, @Query("mobile") String mobile,
  //    @Query("first_name") String firstName,
  //    @Query("last_name") String lastName, @Query("profile_url") String profileUrl,
  //    @Query("school_name") String schoolName,
  //    @Query("tagged_number") String taggedNo, @Query("created_date") String createdDate,
  //    @Query("email") String email);

  @GET("http://3.6.38.217/tagteen/api/questions")
  Call<KnowledgeQuestions> getKnowledgeQuestions(@Query("cat_id") String categoryId,
      @Query("page") int page);

  @GET("http://3.6.38.217/tagteen/api/answers")
  Call<KnowledgeAnswers> getAnswersForQuestion(@Query("user_id") String userId,
      @Query("quest_id") String questionId, @Query("page") int page);

  @GET("http://3.6.38.217/tagteen/api/questions")
  Call<KnowledgeQuestions> getMyQuestions(@Query("user_id") String userId, @Query("page") int page);

  @GET("http://3.6.38.217/tagteen/api/answers")
  Call<KnowledgeAnswers> getMyAnswers(@Query("user_id") String userId, @Query("page") int page);

  @GET("http://3.6.38.217/tagteen/api/categories")
  Call<KnowledgeCategories> getKnowledgeCategories();

  @GET("http://3.6.38.217/tagteen/api/addquestion")
  Call<KnowledgePostResponse> addQuestion(@Query("user_id") String userId,
      @Query("first_name") String firstName, @Query("last_name") String lastName,
      @Query("profile_pic") String profilePic, @Query("thumbnail") String thumbnail,
      @Query("media_link") String mediaLink, @Query("cat_id") int categoryId,
      @Query("title") String title);

  @GET("http://3.6.38.217/tagteen/api/addanswer")
  Call<KnowledgePostResponse> addAnswer(@Query("user_id") String userId,
      @Query("first_name") String firstName, @Query("last_name") String lastName,
      @Query("profile_pic") String profilePic, @Query("thumbnail") String thumbnail,
      @Query("media_link") String mediaLink, @Query("quest_id") String questionId);

  @GET("http://3.6.38.217/tagteen/api/viewquestion")
  Call<Void> updateQuestionViewCount(@Query("user_id") String userId,
      @Query("quest_id") String questionId);

  @GET("http://3.6.38.217/tagteen/api/viewanswer")
  Call<Void> updateAnswerViewCount(@Query("user_id") String userId,
      @Query("answer_id") String answerId);

  @GET("http://3.6.38.217/tagteen/api/delquestion")
  Call<Void> deleteQuestion(@Query("quest_id") String questionId);

  @GET("http://3.6.38.217/tagteen/api/delanswer")
  Call<Void> deleteAnswer(@Query("answer_id") String answerId);

  @GET("http://3.6.38.217/tagteen/api/placements")
  Call<Placements> getPlacements(@Query("user_id") String userId);

  @GET("http://3.6.38.217/tagteen/api/getshoutout")
  Call<ShoutOutModel> getShoutOutText();

  @GET("http://3.6.38.217/tagteen/api/apply")
  Call<ApplyPlacementResponse> applyPlacement(
      @Query("placement_id") String placementId, @Query("user_id") String userId,
      @Query("media_link") String mediaLink, @Query("thumbnail") String thumbnail);

  @POST("https://pro-api.tagteen.in/write/api/v1.0/payments/insert_payment")
  Call<PaymentStatus> insertWebshowPayment(@Body Payment payment);

  @GET("http://3.6.38.217/payment/generateChecksum.php")
  Call<Checksum> generateChecksum(@Query("userID") String userId, @Query("amount") String amount,
      @Query("webshowID") String webshowId);

  @GET("http://3.6.38.217/payment/initiateTransaction.php")
  Call<TxnTokenResponse> initiateOrder(@Query("userID") String userId,
      @Query("amount") String amount, @Query("webshowID") String webshowId);
}
