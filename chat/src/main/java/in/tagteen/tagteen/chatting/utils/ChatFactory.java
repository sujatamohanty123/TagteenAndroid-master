package in.tagteen.tagteen.chatting.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import in.tagteen.tagteen.chatting.ActivitySplash;
import in.tagteen.tagteen.chatting.widget.CircleTransform;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatFactory {

    private static final String TAG = "ChatFactory.TAG";

    private ChatFactory() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot instantiate the class ChatFactory");
    }

    public static void loadCircleImage(Context context, String url, ImageView view) {
        if (TextUtils.isEmpty(url) || view == null)
            return;
        Picasso.with(context).load(url).resize(100, 100)
                .transform(new CircleTransform()).into(view);
    }

    public static void loadImage(Context context, String url, ImageView view) {
        if (TextUtils.isEmpty(url) || view == null)
            return;
        Picasso.with(context).load(url).into(view);
    }

    public static void showSnackMessage(View viewToShow, CharSequence message) {
        Snackbar.make(viewToShow, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackMessage(Context context, CharSequence message) {
        showSnackMessage(((AppCompatActivity) context).findViewById(android.R.id.content), message);
    }

    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getFormattedItemDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date msgDate = dateFormat.parse(dateFormat.format(new Date(date)));
            Date now = dateFormat.parse(dateFormat.format(new Date()));

            if (msgDate.equals(now)) {
                return "Today";
            }

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(new Date().getTime());
            c.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = dateFormat.parse(dateFormat.format(c.getTime()));
            if (msgDate.equals(yesterday)) {
                return "Yesterday";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(new Date(date));
    }

    public static String getFormattedChatListDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date msgDate = dateFormat.parse(dateFormat.format(new Date(date)));
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            if (msgDate.equals(today)) {
                SimpleDateFormat dF = new SimpleDateFormat("hh:mm a");
                return dF.format(new Date(date));
            }

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(new Date().getTime());
            c.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = dateFormat.parse(dateFormat.format(c.getTime()));
            if (msgDate.equals(yesterday)) {
                return "Yesterday";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(new Date(date));
    }

    public static String getFormattedDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return dateFormat.format(new Date(date));
    }

    public static String getFormattedTime(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateFormat;

        if (c.before(new Date())) {
            dateFormat = new SimpleDateFormat("dd MMM hh:mm a");
        } else {
            dateFormat = new SimpleDateFormat("hh:mm a");
        }
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(c.getTime());
    }

    public static String getFormattedDate(Date date) {
        return getFormattedDate(date.getTime());
    }

    public static long getFormattedDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        try {
            return dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void showShortToast(Context context, String msg) {
        if (msg == null || msg.trim().length() == 0) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Returns auto generated random message id
     *
     * @return Message id
     */
    @NonNull
    public static String getAutoGeneratedMsgId() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            return new String(secureRandom.generateSeed(10));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "0352628700285";
    }

    public static CharSequence getFormattedTypingStatus(@NonNull String friendName,
                                                        boolean isTyping) {
        int pos = 0;
        SpannableStringBuilder sb = new SpannableStringBuilder(friendName);
        sb.setSpan(new AbsoluteSizeSpan(18), 0,
                sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = sb.length();
        sb.append("\n").append(isTyping? "typing..." : "last seen " +
                ChatFactory.getFormattedDate(System.currentTimeMillis()));
        sb.setSpan(new AbsoluteSizeSpan(15), pos,
                sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public static float toDp(Context context, int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics());
    }

    public static boolean isActivityAlive(@NonNull Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager!=null){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
                for (ActivityManager.AppTask task : appTasks) {
                    String activity = task.getTaskInfo().baseIntent.getComponent().getClassName();
                    Log.d("onBackPressed1: ", activity);
                    if(activity.equals(ActivitySplash.class.getCanonicalName())) return true;
                }
            } else {
                List<ActivityManager.RecentTaskInfo> recentTaskInfo = activityManager.getRecentTasks(Integer.MAX_VALUE, 0);
                for (ActivityManager.RecentTaskInfo task : recentTaskInfo) {
                    String activity = task.baseIntent.getComponent().getClassName();
                    Log.d("onBackPressed2: ", activity);
                    if(activity.equals(ActivitySplash.class.getCanonicalName())) return true;
                }
            }
        }
        return false;
    }
}
