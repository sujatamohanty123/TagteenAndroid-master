package in.tagteen.tagteen.Model;

import android.app.Activity;

/**
 * Created by user on 22-03-2017.
 */

public class TutorialItemModel {

        private String paymentType;
        private String receiptId;


        public TutorialItemModel() {
        }



        public TutorialItemModel(Activity activity) {

            this.paymentType = paymentType;
            this.receiptId   = receiptId;

        }


        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }



    }


