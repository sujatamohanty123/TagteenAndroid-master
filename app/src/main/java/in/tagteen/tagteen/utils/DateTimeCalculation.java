package in.tagteen.tagteen.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;



public class DateTimeCalculation {

    public static String DateToString(Date date_to_convert) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date_to_convert);
    }

    public static String getCurrentTime() {
        Calendar  calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        String  time = simpleDateFormat.format(calander.getTime());
        return time ;
    }
    public static String arrayToCommaSeparatedString(String[] array){

        StringBuilder sb = new StringBuilder();
        for (String n : array) {
            if (sb.length() > 0) sb.append(',');
            sb.append("'").append(n).append("'");
        }
        return sb.toString();
    }

    public static String getCurrentLocalTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(cal.getTime());
    }

    public static String timeFormatConversion(String time) {

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            String convertTime = new SimpleDateFormat("hh:mm a").format(dateObj);
            return convertTime;
        } catch (final ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public static String StringToDate(String date_to_convert) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(date_to_convert);
            return new SimpleDateFormat("MMM dd,yyyy").format(date);
        } catch (Exception e) {
            e.printStackTrace();
            Date currentDate = new Date();
            return date_to_convert;
        }
    }

    public static String currentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public static Date StringDateToDate(String date_to_convert) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy");
            return dateFormat.parse(date_to_convert);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date StringDateToDate1(String date_to_convert) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return dateFormat.parse(date_to_convert);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }


    public static Long compareDatesBasedOnTime(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }


    public static String weekDayName() {
        return new SimpleDateFormat("EEEE").format(System.currentTimeMillis());
    }


/*    public static Date getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(cal.getTime());
        Date date = null;
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }*/

    public static String getDateBasedOnCurrentDate(int difference) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getDefault());
        cal.add(Calendar.DATE, difference);
        return dateFormat.format(cal.getTime());
    }

    public static String getActualDateFrom(String servertime) {
        try {

            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDateFormat = inputFormat.parse(servertime);
            DateFormat timeFormat = new SimpleDateFormat("dd MMM hh:mm a");
            String convertedTime = timeFormat.format(parsedDateFormat);
            return convertedTime;
        } catch (Exception e) {
            e.printStackTrace();
            return servertime;
        }
    }


    public static String convertToLocalTime(String servertime) {
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDateFormat = inputFormat.parse(servertime);
            DateFormat timeFormat = new SimpleDateFormat("hh:mma");
            String convertedTime = timeFormat.format(parsedDateFormat);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String convertedDate = dateFormat.format(parsedDateFormat);

            long daysDifference = daysBetween(parsedDateFormat, new Date());

            JSONObject dateInfo = new JSONObject();
            dateInfo.put("TIME", convertedTime);
            dateInfo.put("DAYDIFFERENCE", daysDifference);
            dateInfo.put("DATE", convertedDate);

            return dateInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return servertime;
        }
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);
        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }


    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    public static String formatMonth(String month) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(month);
            DateFormat f2 = new SimpleDateFormat("dd MMM");
            return f2.format(date);
        } catch (Exception e) {
            return month;
        }
    }

    public static String convertLocalTime(String servertime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("hh:mm");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(servertime);
            DateFormat f2 = new SimpleDateFormat("h:mma");
            return f2.format(date);
        } catch (Exception e) {
            return servertime;
        }
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /*
    Static methods for Material EditText
     */
    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    public static boolean isLight(int color) {
        return Math.sqrt(
                Color.red(color) * Color.red(color) * .241 +
                        Color.green(color) * Color.green(color) * .691 +
                        Color.blue(color) * Color.blue(color) * .068) > 130;
    }

    public static String makePlaceholdersForSQLiteQuery(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    public static String convertGivenISODateStringToLocalTimeWithGivenFormat(String inputDateString, String format) {

        if (format == null) {
            format = "EEE hh:mm a";
        }

        SimpleDateFormat existingUTCFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat requiredFormat = new SimpleDateFormat(format);

        try {
            existingUTCFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date getDate = existingUTCFormat.parse(inputDateString);
            return requiredFormat.format(getDate);
        } catch (Exception e) {
            return "";
        }

    }

    public static void speakText(String text, TextToSpeech toSpeech, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text, toSpeech, context);
        } else {
            ttsUnder20(text, toSpeech, context);
        }
    }

    @SuppressWarnings("deprecation")
    private static void ttsUnder20(String text, TextToSpeech toSpeech, Context context) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        toSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void ttsGreater21(String text, TextToSpeech toSpeech, Context context) {
        String utteranceId = context.hashCode() + "";
        toSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public static String encodeToUTF8(String s) {
        try {
            s = URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String decodeToUTF8(String s) {
        try {
            s = URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static String getCurrentLocaDatelTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        return dateFormat.format(cal.getTime());
    }
}






