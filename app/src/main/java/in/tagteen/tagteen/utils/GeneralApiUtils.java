package in.tagteen.tagteen.utils;

public class GeneralApiUtils {

  public static String checkUpdateStringIfEmpty(String value) {
    return isStringEmpty(value) ? "NAN" : value;
  }

  public static boolean isStringEmpty(String str) {
    return str == null || str.trim().equals("");
  }
}
