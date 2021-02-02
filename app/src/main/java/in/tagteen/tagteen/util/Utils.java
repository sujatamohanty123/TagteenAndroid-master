/*
 * Copyright (c) 2016 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.tagteen.tagteen.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.tagteen.tagteen.BuildConfig;
import in.tagteen.tagteen.Fragments.beans.ShowRooms;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnConfirmDialogListener;
import in.tagteen.tagteen.LoginActivity;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.Language;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.VideoPartDetail;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.profile.OtherUserProfileActivity;
import in.tagteen.tagteen.profile.UserProfileFragment;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Utils {
    private static int sKeyboardHeight;
    private static final String FILE_PROVIDER_AUTHORITY =
            BuildConfig.APPLICATION_ID + ".fileprovider";

    public static final DateFormat DATE_FORMAT_VISIBLE = new SimpleDateFormat("MMM dd, yyyy");
    public static final DateFormat DATE_MONTH_FORMAT_VISIBLE = new SimpleDateFormat("MMM dd");
    public static final DateFormat TIME_FORMAT_VISIBLE = new SimpleDateFormat("hh:mm a", Locale.UK);

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static final long DAY_IN_MILLIS = 86400000l;

    /**
     * Generate a value suitable for use in {@link View#setId(int)}. This value will not collide with
     * ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static int getSoftKeyboardHeight(Context context) {
        return 200;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    public static boolean isKeyboardShowing(Context context) {
        return context.getResources().getConfiguration().keyboardHidden
                == Configuration.KEYBOARDHIDDEN_NO;
    }

    public static void setKeyboardHeight(int keyboardHeight) {
        Utils.sKeyboardHeight = keyboardHeight;
    }

    public static int getKeyboardHeight() {
        return sKeyboardHeight;
    }

    public static void hideKeyboard(Activity context, View view) {
        if (context == null || view == null) {
            return;
        }
        InputMethodManager manager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeypad(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager manager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showAlertDialog(Context context, String msg, String title) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    public static void showConfirmationDialog(
            Context context, String msg, String confirmTitle, final OnConfirmDialogListener listener) {
        if (listener == null) {
            Utils.showShortToast(context, "No listener provided");
            return;
        }
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmationpopup);
        TextView lblMessage = (TextView) dialog.findViewById(R.id.message);
        TextView lblName = (TextView) dialog.findViewById(R.id.yourname);
        TextView lblConfirm = (TextView) dialog.findViewById(R.id.confirm);
        if (confirmTitle != null) {
            lblConfirm.setText(confirmTitle);
        }
        TextView lblCancel = (TextView) dialog.findViewById(R.id.dismiss);
        TextView lblOk = (TextView) dialog.findViewById(R.id.confirm_ok_btn);
        lblOk.setVisibility(View.GONE);

        if (msg != null) {
            lblMessage.setText(msg);
        }
        lblConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onConfirmation();
            }
        });
        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onCancelled();
            }
        });
        dialog.show();
    }

    public static String getDeviceId(Context context) {
        @SuppressLint("HardwareIds") String deviceId =
                Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public static String getNowInDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(new Date());
    }

    public static Date getDateFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.d("Utils", e.getMessage());
        }
        return date;
    }

    public static Date getDateTimeFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.d("Utils", e.getMessage());
        }
        return date;
    }

    public static boolean is24HrsCompleted(Date date) {
        if (date == null) {
            return true;
        }
        long timeDiff = new Date().getTime() - date.getTime();
        return timeDiff > DAY_IN_MILLIS;
    }

    public static boolean isSameDay(Date lhs, Date rhs) {
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs.getDate() != rhs.getDate()) {
            return false;
        }
        if (lhs.getMonth() != rhs.getMonth()) {
            return false;
        }
        if (lhs.getYear() != rhs.getYear()) {
            return false;
        }
        return true;
    }

    public static boolean isLatestPost(long postedTime) {
        postedTime = postedTime * 1000;
        long timeDiff = new Date().getTime() - postedTime;
        return timeDiff < (DAY_IN_MILLIS * 15);
    }

    public static String getRelativeTime(long feedTime) {
        String createdtime =
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(feedTime * 1000));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(createdtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            date = new Date();
        }
        long millis = date.getTime();
        long diff = System.currentTimeMillis() - millis;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffMonths = diffDays / 30;
        long diffYears = diffMonths / 12;

        String time = "";
        if (diffYears > 0) {
            if (diffYears == 1) {
                time = diffYears + " year ago";
            } else {
                time = diffYears + " years ago";
            }
        } else if (diffMonths > 0) {
            if (diffMonths == 1) {
                time = diffMonths + " month ago";
            } else {
                time = diffMonths + " months ago";
            }
        } else if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago";
            } else {
                time = diffDays + " days ago";
            }
        } else if (diffHours > 0) {
            if (diffHours == 1) {
                time = diffHours + " hour ago";
            } else {
                time = diffHours + " hrs ago";
            }
        } else if (diffMinutes > 0) {
            if (diffMinutes == 1) {
                time = diffMinutes + " min ago";
            } else {
                time = diffMinutes + " mins ago";
            }
        } else if (diffSeconds > 5) {
            time = diffSeconds + " secs ago";
        } else if (diffSeconds <= 5) {
            time = "1 sec ago";
        }
        return time;
    }

    public static String getPlacementPostedAgo(long feedTime) {
        String createdtime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(feedTime));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(createdtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            date = new Date();
        }
        long millis = date.getTime();
        long diff = System.currentTimeMillis() - millis;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffMonths = diffDays / 30;
        long diffYears = diffMonths / 12;

        String time = "";
        if (diffYears > 0) {
            if (diffYears == 1) {
                time = diffYears + " year ago";
            } else {
                time = diffYears + " years ago";
            }
        } else if (diffMonths > 0) {
            if (diffMonths == 1) {
                time = diffMonths + " month ago";
            } else {
                time = diffMonths + " months ago";
            }
        } else if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago";
            } else {
                time = diffDays + " days ago";
            }
        } else if (diffHours > 0) {
            if (diffHours == 1) {
                time = diffHours + " hour ago";
            } else {
                time = diffHours + " hrs ago";
            }
        } else if (diffMinutes > 0) {
            if (diffMinutes == 1) {
                time = diffMinutes + " min ago";
            } else {
                time = diffMinutes + " mins ago";
            }
        } else if (diffSeconds > 5) {
            time = diffSeconds + " secs ago";
        } else if (diffSeconds <= 5) {
            time = "1 sec ago";
        }
        return time;
    }

    public static String timeConversion(long timeInMills) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;
        long totalSeconds = timeInMills / 1000;
        int seconds = (int) (totalSeconds % SECONDS_IN_A_MINUTE);
        int totalMinutes = (int) (totalSeconds / SECONDS_IN_A_MINUTE);
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String ret = "";
        if (hours > 0) {
            if (hours < 10) {
                ret += "0";
            }
            ret += String.valueOf(hours);
            ret += ":";
        }
        if (minutes < 10) {
            ret += "0";
        }
        ret += String.valueOf(minutes);
        ret += ":";

        if (seconds < 10) {
            ret += "0";
        }
        ret += String.valueOf(seconds);
        return ret;
    }

    public static void showToast(Context context, String msg) {
        if (msg == null || context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String msg) {
        if (msg == null || context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String constructDownloadURL(String url) {
        if (url == null) {
            return null;
        }
        String downloadURL = url.replaceAll(" ", "+");
        downloadURL = downloadURL.replaceAll("#", "_");
        return downloadURL;
    }

    public static String getHtmlEscapedString(String str) {
        if (str == null) {
            return "";
        }

        return Html.fromHtml(str).toString();
    }

    public static int getPxFromDp(Context context, int size) {
        if (context == null) {
            return size;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }

    public static int getDpFromPx(Context context, int size) {
        if (context == null) {
            return size;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((size - 0.5f) / scale);
    }

    public static void loadProfilePic(Context mContext, ImageView imageView, String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Picasso.with(mContext)
                    .load(UrlUtils.getUpdatedImageUrl(url, "large")) // Your image source.
                    .transform(new RoundedTransformation(100, 0))
                    .fit()  // Fix centerCrop issue: http://stackoverflow.com/a/20824141/1936697
                    .centerCrop()
                    .placeholder(R.drawable.profile_pic_bg)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadProfilePicImage(Context mContext, ImageView imageView, String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Picasso.with(mContext)
                    .load(url) // Your image source.
                    .fit()  // Fix centerCrop issue: http://stackoverflow.com/a/20824141/1936697
                    .centerCrop()
                    .placeholder(R.drawable.profile_pic_bg)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadRoundedImage(Context mContext, ImageView imageView, String url,
                                        int radius) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            if (radius <= 0) {
                radius = 100;
            }
            Picasso.with(mContext)
                    .load(url) // image source.
                    .transform(new RoundedTransformation(radius, 0))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.profile_pic_bg)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadImageUsingGlideWithPlaceholder(
            Context context, ImageView imageView, String url, int placeholderResId) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(placeholderResId)
                    .error(placeholderResId)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.d("Utils", "Exception loading image with url: " + url);
        }
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView, String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.color.grey_400)
                    .error(R.color.grey_400)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.d("Utils", "Exception loading image with url: " + url);
        }
    }

    public static void loadVideoThumnailUsingGlide(Context context, ImageView imageView,
                                                   String videoUrl) {
        if (imageView == null || videoUrl == null || videoUrl.trim().length() == 0) {
            return;
        }
        Glide.with(context).load(videoUrl).into(imageView);
    }

    public static void loadImageUsingGlideCenterCrop(Context context, ImageView imageView,
                                                     String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.color.grey_400)
                    .error(R.color.grey_400)
                    .fitCenter()
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.d("Utils", "Exception loading image with url: " + url);
        }
    }

    public static void loadImageUsingGlideCircleTransform(Context context, ImageView imageView,
                                                          String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Glide.with(context)
                    .load(url)
                    .error(R.color.grey_400)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.placeholder)
                    .fitCenter()
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.d("Utils", "Exception loading image with url: " + url);
        }
    }

    public static void loadImageUsingGlide(Context context, ImageView imageView, int resId) {
        if (imageView == null || resId <= 0) {
            return;
        }
        try {
            Glide.with(context)
                    .load(resId)
                    .placeholder(R.color.grey_400)
                    .error(R.color.grey_400)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.d("Utils", "Exception loading image with resId: " + resId);
        }
    }

    public static void loadImageUsingGlideWithCache(Context context, ImageView imageView,
                                                    String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.color.grey_400)
                    .error(R.color.grey_400)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.d("Utils", "Exception loading image with url: " + url);
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        if (inImage == null) {
            return null;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path =
                MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void gotoProfile(Context mcContext, String userId) {
        if (isGuestLogin()) {
            Utils.moveToRegistration(mcContext);
        } else {
            String loggedInUserId =
                    SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
            Intent intent;
            if (userId.equalsIgnoreCase(loggedInUserId)) {
                intent = new Intent(mcContext, UserProfileFragment.class);
                intent.putExtra(Constants.LOAD_FRAGMENT, Constants.MY_PROFILE);
            } else {
                intent = new Intent(mcContext, OtherUserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, userId);
            }
            mcContext.startActivity(intent);
        }
    }

    public static boolean emailValidation(Context mcontext, String email) {
        boolean flag = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern) && email.length() > 0) {
            flag = true;
        } else {
            flag = false;
            Toast.makeText(mcontext, "Please enter valid email address", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    public static boolean matchPassword(Context mcontext, String pass1, String pass2) {
        boolean flag = false;
        if (pass1.length() > 0) {
            if (pass1.equalsIgnoreCase(pass2)) {
                flag = true;
            } else {
                Utils.showShortToast(mcontext, "password does not match...");
            }
        } else {
            Utils.showShortToast(mcontext, "please enter password...");
        }

        return flag;
    }

    public static String getDateLabel(String timeMilli) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar ysdy = Calendar.getInstance();
            ysdy.add(Calendar.DATE, -1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeMilli));

            String label = "";

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                label = "Today";
            } else if (calendar.get(Calendar.YEAR) == ysdy.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == ysdy.get(Calendar.DAY_OF_YEAR)) {
                label = "Yesterday";
            } else {
                label = Utils.DATE_FORMAT_VISIBLE.format(calendar.getTime());
            }
            return label;
        } catch (Exception e) {
            e.printStackTrace();
            return "---";
        }
    }

    public static String getAppFolderPath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/tagteen");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getPath();
    }

    private static final String SHARE_LINK_TEMP_FILE_NAME = "sharelinke.jpg";
    //private static final String SHARE_LINK_BASE_URL = "http://3.6.38.217/tagteen/api/share?";
    private static final String SHARE_LINK_BASE_URL = "http://appl.tagteen.in/?";

    public static void shareAppDownloadLink(Context context, View view) {
        Bitmap bitmap = Utils.getBitmapFromView(view);
        if (bitmap != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                //intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=in.tagteen.tagteen");
                String userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
                intent.putExtra(Intent.EXTRA_TEXT, SHARE_LINK_BASE_URL + "user=" + userId);

                File appDir = new File(Utils.getAppFolderPath());
                File file = new File(appDir, SHARE_LINK_TEMP_FILE_NAME);
                FileOutputStream outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                /*String url = MediaStore.Images.Media.insertImage(
                        context.getContentResolver(), bitmap, "title", "description");*/
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.toString()));
                context.startActivity(Intent.createChooser(intent, "Share download link"));
            } catch (Exception e) {
                Log.e("ShareLink", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void shareVideoAppDownloadLink(Context context, Uri videoLink, String postId) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            //intent.setPackage("com.whatsapp");
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Hey! Download tagteen - Only talent video app of India, and watch the full video. Have fun and earn with tagteen\uD83E\uDD18.Jay India\uD83C\uDDEE\uD83C\uDDF3"
                            + "\n "
                            + SHARE_LINK_BASE_URL + "post=" + postId);
                            //+ "https://play.google.com/store/apps/details?id=in.tagteen.tagteen");
            intent.putExtra(Intent.EXTRA_STREAM, videoLink);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Share Video"));
        } catch (Exception e) {
            Log.e("Utils", e.getMessage());
        }
    }

    public static void removeShareLinkTempImage() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File dirPath = new File(root + "/tagteen");
            if (dirPath == null) {
                return;
            }
            File file = new File(dirPath, SHARE_LINK_TEMP_FILE_NAME);
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    public static void removeShareLinkTempVideo() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File dirPath = new File(root + "/tagteen");
            if (dirPath == null) {
                return;
            }
            File file = new File(dirPath, "tagteenvideo.mp4");
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFrom24HrsFormatStr(String dateString) {
        if (dateString == null || dateString.trim().length() == 0) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.d("Utils", e.getMessage());
        }
        return date;
    }

    public static String getVideoCompressionPath() {
        String path =
                Environment.getExternalStorageDirectory().toString() + "/Tagteen/VideoCompression/";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getPath();
    }

    public static void clearVideoCompressionFolder() {
        String path =
                Environment.getExternalStorageDirectory().toString() + "/Tagteen/VideoCompression";
        File folder = new File(path);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        }
    }

    public static void makeTextViewResizable(final TextView textView, final int maxLine,
                                             final String expandText, final boolean viewMore) {
        if (textView.getTag() == null) {
            textView.setTag(textView.getText());
        }
        ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver observer = textView.getViewTreeObserver();
                observer.removeOnGlobalLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = textView.getLayout().getLineEnd(0);
                    text = textView.getText().subSequence(0, lineEndIndex - expandText.length() + 1)
                            + " "
                            + expandText;
                } else if (maxLine > 0 && textView.getLineCount() >= maxLine) {
                    lineEndIndex = textView.getLayout().getLineEnd(maxLine - 1);
                    text = textView.getText().subSequence(0, lineEndIndex - expandText.length() + 1)
                            + " "
                            + expandText;
                } else {
                    lineEndIndex = textView.getLayout().getLineEnd(textView.getLayout().getLineCount() - 1);
                    text = textView.getText().subSequence(0, lineEndIndex) + "";
                }
                textView.setText(text);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(addClickablePartTextViewResizable(
                        Html.fromHtml(textView.getText().toString()), textView, lineEndIndex, expandText,
                        viewMore),
                        TextView.BufferType.SPANNABLE);
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(
            Spanned strSpanned, final TextView textView, int maxLine, String spanableText,
            final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    textView.setLayoutParams(textView.getLayoutParams());
                    textView.setText(textView.getTag().toString(), TextView.BufferType.SPANNABLE);
                    textView.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(textView, -1, "Less", false);
                    } else {
                        makeTextViewResizable(textView, 3, "More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        if (imageView == null) {
            return null;
        }
        Bitmap bitmap = null;
        if (imageView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            bitmap = drawable.getBitmap();
        }
        return bitmap;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap =
                Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private static final int U_ROCK = 0x1F918;
    private static final int INDIAN_FLAG = 0x1F1F3;

    public static String getShareLinkText() {
        String ret = "I love tagteen ";
        ret += Utils.getEmojiByUnicode(U_ROCK);
        ret += ". Have fun, learn, earn... Only for Indians.";
        //ret += Utils.getEmojiByUnicode(INDIAN_FLAG);
        return ret;
    }

    public static String getEqualsOneRupeeString(Context context) {
        return " = " + context.getString(R.string.rupee_symbol) + " 1";
    }

    public static String getRewardsInfo(Context context) {
        //String ret = "1" + Utils.getEmojiByUnicode(U_ROCK);
        String ret = " upload talent videos, make ";
        ret += context.getString(R.string.rupee_symbol);
        ret += context.getString(R.string.rupee_symbol);
        ret += context.getString(R.string.rupee_symbol);
        return ret;
    }

    public static String getRewardsTermsAndConditions() {
        String ret = "1. Only talent videos where your face or your voice should be there.\n\n";
        ret +=
                "2. No tiktok or like or vigo or share chat or any videos from other app are allowed.\n\n";
        ret += "3. No status or lyrical videos are allowed.\n\n";
        ret += "4. Please edit your videos before uploading to gain more popularity.\n\n";
        ret += "5. Please upload 1 video maximum a day.\n\n";
        ret +=
                "6. Payments are weekly Sunday and Monday. Your first payment is after 7 days to the closest weekend.\n\n";
        ret +=
                "7. If its a knowledge/technical/gaming or any other appropriate video, please add properly made introduction and close to the video containing the topic and any other information you feel necessary.\n\n";
        ret +=
                "8. GAMEPLAYS ARE NOT ALLOWED. Only gaming reviews, tips and tricks with your voice explaining the video are allowed.\n\n";
        ret +=
                "9. If it's a screen recording the tagteen logo in the app installed should be visible at some point during the recording.\n";
        return ret;
    }

    public static String getRockstarPriceDetails() {
        return "1st Prize  <b>5000</b><br>2nd Prize <b>2000</b><br>3rd Prize <b>1000</b><br>7 Prizes of 100 each.";
    }

    public static long getFileSizeInMB(String path) {
        if (path == null || path.trim().length() == 0) {
            return 0;
        }
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }

        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;

        return fileSizeInMB;
    }

    public static void showShowroomFirstAccessAlert(Context context) {
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.dialog_showroom_first_access);

        TextView lblText4 = (TextView) d.findViewById(R.id.lblText4);
        String msg = "also on every talent video get";
        lblText4.setText(msg);
        TextView lblEqualsOneRupee = d.findViewById(R.id.lblEqualsOneRupee);
        lblEqualsOneRupee.setText(Utils.getEqualsOneRupeeString(context));

        TextView lblDone = (TextView) d.findViewById(R.id.lblDone);
        lblDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    private static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public static void showUnverifiedUserDialog(Context context) {
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        TextView ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
        LinearLayout buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);
        name.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        continueorder.setVisibility(View.GONE);
        dismiss.setVisibility(View.GONE);
        msg.setText(context.getString(R.string.unverified_msg));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public static int getRandomNumberForShowroom(int bound) {
        Random r = new Random();
        return r.nextInt(bound) + 1;
    }

    public static void checkPermissions(AppCompatActivity appCompatActivity) {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : Constants.REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(appCompatActivity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty()) {
            final String[] permissions =
                    missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(appCompatActivity, permissions,
                    Constants.REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[Constants.REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            appCompatActivity.onRequestPermissionsResult(
                    Constants.REQUEST_CODE_ASK_PERMISSIONS,
                    Constants.REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

    public static int getCategoryColor(Context mContext, String ctyName) {
        int textColor = ContextCompat.getColor(mContext, R.color.blue_800);
        if (ctyName.equalsIgnoreCase(Constants.ACTING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_acting);
        } else if (ctyName.equalsIgnoreCase(Constants.ANIMATION)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_animation);
        } else if (ctyName.equalsIgnoreCase(Constants.BEAUTY)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_beauty);
        } else if (ctyName.equalsIgnoreCase(Constants.CAR_BIKES)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_car_bikes);
        } else if (ctyName.equalsIgnoreCase(Constants.CAREERS)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_careers);
        } else if (ctyName.equalsIgnoreCase(Constants.COMPUTER_GADGETS)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_computers_gadgets);
        } else if (ctyName.equalsIgnoreCase(Constants.DANCING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_dancing);
        } else if (ctyName.equalsIgnoreCase(Constants.DJ)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_DJ);
        } else if (ctyName.equalsIgnoreCase(Constants.ENTREPRENEURS)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Enterpreneurs);
        } else if (ctyName.equalsIgnoreCase(Constants.FASHION)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Fashion);
        } else if (ctyName.equalsIgnoreCase(Constants.FOOD_COOKING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_food_cooking);
        } else if (ctyName.equalsIgnoreCase(Constants.FUNNY_STANDUP)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_funny_standup);
        } else if (ctyName.equalsIgnoreCase(Constants.GAMING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_gaming);
        } else if (ctyName.equalsIgnoreCase(Constants.HEALTH_FITNESS)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_health_fitness);
        } else if (ctyName.equalsIgnoreCase(Constants.HOME_LIFESTYLE)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_home_lifestyle);
        } else if (ctyName.equalsIgnoreCase(Constants.JOURNALISM)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_journalism);
        } else if (ctyName.equalsIgnoreCase(Constants.MAGIC)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_magic);
        } else if (ctyName.equalsIgnoreCase(Constants.MARKETING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Marketing);
        } else if (ctyName.equalsIgnoreCase(Constants.MILITARY)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Military);
        } else if (ctyName.equalsIgnoreCase(Constants.MOVIES)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Movies);
        } else if (ctyName.equalsIgnoreCase(Constants.MUSIC)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Music);
        } else if (ctyName.equalsIgnoreCase(Constants.PAINTING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Painting);
        } else if (ctyName.equalsIgnoreCase(Constants.PHOTOGRAPHY)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Photography);
        } else if (ctyName.equalsIgnoreCase(Constants.POLITICS)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Politics);
        } else if (ctyName.equalsIgnoreCase(Constants.SELF_IMPROVEMENT)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Self_Improvement);
        } else if (ctyName.equalsIgnoreCase(Constants.SHOPPING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Shopping);
        } else if (ctyName.equalsIgnoreCase(Constants.SPORTS)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Sports);
        } else if (ctyName.equalsIgnoreCase(Constants.STUDIES_EDUCATION)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Studies_Education);
        } else if (ctyName.equalsIgnoreCase(Constants.TRAVELLING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Travelling);
        } else if (ctyName.equalsIgnoreCase(Constants.WRITING)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_Writing);
        } else if (ctyName.equalsIgnoreCase(Constants.ALL) || ctyName.equalsIgnoreCase(
                Constants.FOR_YOU)) {
            textColor = ContextCompat.getColor(mContext, R.color.cty_all);
        }
        return textColor;
    }

    public static List<Language> getLanguages() {
        List<Language> languagesList = new ArrayList<>();
        languagesList.add(new Language(3, "English"));
        languagesList.add(new Language(5, "हिंदी")); // hindi
        languagesList.add(new Language(9, "മലയാളം")); // malayalam
        languagesList.add(new Language(17, "తెలుగు")); // telugu
        languagesList.add(new Language(13, "ଓଡ଼ିଆ")); // oriya
        languagesList.add(new Language(14, "ਪੰਜਾਬੀ")); // punjabi
        languagesList.add(new Language(2, "বাংলা")); // bengali
        languagesList.add(new Language(16, "தமிழ்")); // tamil

        languagesList.add(new Language(1, "অসমীয়া")); // assamese
        languagesList.add(new Language(4, "ગુજરાતી")); // gujarati
        languagesList.add(new Language(6, "ಕನ್ನಡ")); // kannada
        languagesList.add(new Language(7, "कॉशुर")); // kashmiri
        languagesList.add(new Language(8, "कोंकणी")); // konkani
        languagesList.add(new Language(10, "মৈতৈলোন্")); // manipuri
        languagesList.add(new Language(11, "मराठी")); // marathi
        languagesList.add(new Language(12, "नेपाली")); // nepali
        languagesList.add(new Language(15, "संस्कृत")); // sanskrit
        return languagesList;
    }

    public static List<Language> getVideoPostLanguages() {
        List<Language> languagesList = new ArrayList<>();
        languagesList.add(new Language(-1, "Select"));
        languagesList.add(new Language(3, "English"));
        languagesList.add(new Language(5, "हिंदी")); // hindi
        languagesList.add(new Language(9, "മലയാളം")); // malayalam
        languagesList.add(new Language(17, "తెలుగు")); // telugu
        languagesList.add(new Language(13, "ଓଡ଼ିଆ")); // oriya
        languagesList.add(new Language(14, "ਪੰਜਾਬੀ")); // punjabi
        languagesList.add(new Language(2, "বাংলা")); // bengali
        languagesList.add(new Language(16, "தமிழ்")); // tamil

        languagesList.add(new Language(1, "অসমীয়া")); // assamese
        languagesList.add(new Language(4, "ગુજરાતી")); // gujarati
        languagesList.add(new Language(6, "ಕನ್ನಡ")); // kannada
        languagesList.add(new Language(7, "कॉशुर")); // kashmiri
        languagesList.add(new Language(8, "कोंकणी")); // konkani
        languagesList.add(new Language(10, "মৈতৈলোন্")); // manipuri
        languagesList.add(new Language(11, "मराठी")); // marathi
        languagesList.add(new Language(12, "नेपाली")); // nepali
        languagesList.add(new Language(15, "संस्कृत")); // sanskrit
        return languagesList;
    }

    public static void updateQuestionViewCount(Context context, String questionId) {
        String userId =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Void> call = apiMethods.updateQuestionViewCount(userId, questionId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public static void updateAnswerViewCount(Context context, String answerId) {
        String userId =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Void> call = apiMethods.updateAnswerViewCount(userId, answerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public static void updateShowroomViewCount(Context context, String postId) {
        if (context == null || postId == null) {
            return;
        }
        AsyncWorker mWorker = new AsyncWorker(context);
        JSONObject jsonObject = new JSONObject();
        String userId =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        try {
            jsonObject.put("post_id", postId);
            jsonObject.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = ServerConnector.REQUEST_UPDATE_YOUTHTUBE_VIEWCOUNT;
        mWorker.execute(url, jsonObject.toString(), RequestConstants.POST_REQUEST,
                RequestConstants.HEADER_YES, RequestConstants.UPDATE_VIDEO_VIEW_COUNT);
    }

    public static void deleteQuestion(Context context, String questionId,
                                      OnCallbackListener onCallbackListener) {
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Void> call = apiMethods.deleteQuestion(questionId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (onCallbackListener != null) {
                    onCallbackListener.OnComplete();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (onCallbackListener != null) {
                    onCallbackListener.OnComplete();
                }
            }
        });
    }

    public static void deleteAnswer(Context context, String answerId,
                                    OnCallbackListener onCallbackListener) {
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        Call<Void> call = apiMethods.deleteAnswer(answerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (onCallbackListener != null) {
                    onCallbackListener.OnComplete();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (onCallbackListener != null) {
                    onCallbackListener.OnComplete();
                }
            }
        });
    }

    public static boolean isGuestLogin() {
        String userId =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        if (userId != null && Constants.GUEST_USER_ID.equalsIgnoreCase(userId)) {
            return true;
        }
        return false;
    }

    public static void moveToRegistration(Context context) {
        //SharedPreferenceSingleton.getInstance().clearPreference();
        //ChatSessionManager.getInstance().clear();

        Intent intent = new Intent(context, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        //appCompatActivity.finish();
    }

    public static void moveToVideoDetails(Context context, SectionDataModel data) {
        if (data == null) {
            return;
        }

        Intent intent = new Intent(context, VideoPartDetail.class);
        intent.putExtra("post_creator_id", data.getOwner_post_creator_id());
        intent.putExtra("post_vid_id", data.getOwner_post_id());
        intent.putExtra("category_id", data.getCategory_id());
        intent.putExtra(Constants.SHOWROOM_POST_DATA, data);
        context.startActivity(intent);
    }

    public static void moveToVideoDetails(Context context, GetPostResponseModel.PostDetails data) {
        if (data == null) {
            return;
        }

        Intent intent = new Intent(context, VideoPartDetail.class);
        intent.putExtra("post_creator_id", data.getOwner_post_creator_id());
        intent.putExtra("post_vid_id", data.getOwner_post_id());
        intent.putExtra("category_id", data.getCategorie_id());
        intent.putExtra(Constants.SHOWROOM_POST_DATA, data);
        context.startActivity(intent);
    }

    public static void moveToVideoDetails(Context context, ShowRooms data) {
        if (data == null) {
            return;
        }

        Intent intent = new Intent(context, VideoPartDetail.class);
        intent.putExtra("post_creator_id", data.getPostCreatorId());
        intent.putExtra("post_vid_id", data.getId());
        intent.putExtra("category_id", data.getCategorieId());
        intent.putExtra(Constants.SHOWROOM_POST_DATA, data);
        context.startActivity(intent);
    }

    public static boolean isMobileNumbervalid(String mobileNumber) {
        boolean flag = false;
        String regexStr = "^[5-9][0-9]{9}$";

        if (mobileNumber.matches(regexStr) &&
                !mobileNumber.equals("9999999999") &&
                !mobileNumber.equals("8888888888") &&
                !mobileNumber.equals("7777777777") &&
                mobileNumber.trim().length() != 0) {
            try {
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            flag = false;
        }

        return flag;
    }

    public static boolean isemailvalid(String emailid) {
        try {
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(emailid);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Uri getImageUri(Context mContext, File imageFile) {
        Uri imageUri;
        // if android version 24+ then should be get uri using FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(mContext, FILE_PROVIDER_AUTHORITY, imageFile);
        } else {
            //imageUri = Uri.parse(imageFile.getPath());
            imageUri = Uri.fromFile(imageFile);
        }
        return imageUri;
    }

    public static boolean checkIfFileExists(String filename) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File dirPath = new File(root + "/tagteen");
            if (dirPath == null) {
                return false;
            }
            File file = new File(dirPath, filename);
            if (file != null && file.exists()) {
                return true;
            }
        } catch (Exception e) {
            // do nothing
            return false;
        }
        return false;
    }
}