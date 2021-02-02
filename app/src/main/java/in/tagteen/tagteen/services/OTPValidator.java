package in.tagteen.tagteen.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class OTPValidator {

        private final int senderNameSplitLength = 2;
        private Pattern smsCodePattern  =   Pattern.compile("\\d+");

        public boolean validateSender(String receivedSenderName, String validSenderName){
            if(receivedSenderName.contains("-")){
                String[] splitReceivedSenderName = receivedSenderName.split("-");
                return splitReceivedSenderName.length == senderNameSplitLength && splitReceivedSenderName[1].equals(validSenderName);
            }
            else return false;
        }

        public int matchAndGetOTP(String code){
            Matcher matcher = smsCodePattern.matcher(code);
            if(matcher.find()){
                return Integer.parseInt(matcher.group());
            }
            else return 0;
        }



}
