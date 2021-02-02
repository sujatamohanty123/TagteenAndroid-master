package io.agora.openlive.utils;

import androidx.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class
TimestampUtils {
  SimpleDateFormat dateFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
  public static final String DATE_DEFAULT_FORMAT = "yyyy-mm-dd'T'HH:mm:ss.SSS'Z'";
  public static final String DATE_DATETIME_FORMAT = "yyyy-mm-dd hh:mm:ss";
  public static final String HOUR_FORMAT = "dd MMM hh:mm aa";
  private static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
  private static SimpleDateFormat sFormatter =
      new SimpleDateFormat(DATE_DEFAULT_FORMAT, Locale.ENGLISH);

  /**
   * Return an ISO 8601 combined date and time string for current date/time
   *
   * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
   */
  public static String getISO8601StringForCurrentDate() {
    Date now = new Date();
    return getISO8601StringForDate(now);
  }

  /**
   * Return an ISO 8601 combined date and time string for specified date/time
   *
   * @param date Date
   * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
   */
  private static String getISO8601StringForDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return dateFormat.format(date);
  }

  /**
   * Private constructor: class cannot be instantiated
   */
  private TimestampUtils() {
  }

  public static String convertDateToString(Date date) {
    return sFormatter.format(date);
  }

  public static String convertDateToString(Date date, String toformat) {
    sFormatter.applyPattern(toformat);
    return convertDateToString(date);
  }

  @Nullable public static Date convertStringToDate(String date) {
    try {
      return sFormatter.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Nullable public static Date convertStringToDate(String date, String format) {
    sFormatter.applyPattern(format);
    return convertStringToDate(date);
  }

  public static String formatStringDate(String dateRaw, String formatResult) {
    return convertDateToString(convertStringToDate(dateRaw), formatResult);
  }

  public static String formatStringDate(String dateRaw, String formatRaw, String formatResult) {
    return convertDateToString(convertStringToDate(dateRaw, formatRaw), formatResult);
  }

  public static String getCurrentDateTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    return mdformat.format(calendar.getTime());
  }
}
