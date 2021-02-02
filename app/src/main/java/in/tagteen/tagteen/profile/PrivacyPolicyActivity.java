package in.tagteen.tagteen.profile;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import in.tagteen.tagteen.R;

public class PrivacyPolicyActivity extends AppCompatActivity {



    String value1 = "<p>\nAt tagteen we recognize that privacy is significant. This Privacy Policy (\"Policy\") applies to your use of the the application and its services on all platforms (the \"Services\").</p>" +
            "<p>\nWhen using tagteen messenger, nothing is stored in our server.</p>" +
            "<p>\nTagteen has created this Policy to explain how we collect, use and share your information when you use our Application and Services. By using our Service you understand, agree and consent that we are providing a platform for you to post content, including photos, videos, voices, comments, articles and other materials (\"Your Content\"), and to share Your Content publicly.  If You do not agree with this Policy, or with our Terms of Service, do not download, install and / or use the Application.</p>" +
            "<p>\nIf you have any questions about this Privacy Policy, please contact us at: admin@tagteen.in.</p>" +
            "<p><b>\nWhat Kind of Information Do We Collect?</b>" +
            "<p>\nAccount Information: As a part of the registration process, tagteen will allow you to use your mobile number or email  to register in the Application and create an account. When you create an account, we may collect the personal information you provide to us, such as your name, gender, date of birth, school name, school address pin code, hobbies, username, password, email address, picture, or phone number and so on. We do not access your address book or contact list on your phone currently</p>" +
            "<p><b>\nYour Content: </b></p>" +
            "<p>When you are choosing to participate in the use of the Application and the Services, such as registering as a user, messaging other tagteen users, posting status updates, uploading images/videos/articles we collect that information to provide you a service. In order to provide Services you hereby agree to provide tagteen a right to use such information for providing Services and also warrant that you have all requisite rights in your favour to share this information with tagteen. When using tagteen chat messenger none of your messages are stored in our server.</p>" +
            "<p>\n<b>Device Information:</b></p>" +
            "<p>\nWe collect information, which may include the Android ID, Log files, hardware model, operating system and version, mobile network information, browser type and language, IP address etc. about the device you use to access our Services and customise the service for your device</p>" +
            "<p>\n<b>Usage and Log Information:</b></p>" +
            "<p>\nWe collect information about your use of the Services, including access times, date and stamp information, articles and videos viewed and interactions (e.g. heart, like, U Rock cool, comment, share) and other information about your interactions with us, the Services and other users.We use these information to provide, understand, and improve our Services.</p>" +
            "<p><b>\nCookies and Other Tracking Technologies:</b></p>" +
            "<p>We and our service providers use cookies, web beacons, web storage and other technologies to collect information about your use of our Services and your device to improve our Services and your experience. We may use similar technologies to collect information when you interact with services offered by any third party through the Application.</p>" +
            "<p>\n<b>Other Information:</b></p>" +
            "<p>\n We may collect information you submit to communicate with us, for example, when you give us your opinions via feedback, update your account, interact with the Services, apply for a job at our company, communicate with us via third-party sites, We receive information about you and your activities on and off tagteen from third-party partners, such as information from a partner when we jointly offer services or from an advertiser about your experiences or interactions with them.</p>" +
            "<p><b>\nHow We Use and Share Collected Information?</b></p>" +
            "<p>\nWe do not sell Your Information that we collect with anyone for commercial, advertising and or similar marketing purposes without your consent.</p>" +
            "<p>\nWe use and share your information to personalise your experience, to provide, maintain, improve and develop the Service for your device, to provide customer service and support, to identify winners of promotional contests, to change or updates to features of tagteen, to deliver the products and services you request, to send you emails and push notifications, to promote safety and security, to comply with applicable laws or legal obligations, and for analytics and authentication.</p>" +
            "<p>\nWhen you share your content using our Services, you choose the audience who can see what you share. Any information you share with a public audience, can be seen or accessed by everyone in the platform.</p>" +
            "<p>\nWhen you use third-party apps, websites or other services that use, or are integrated with, our Services, they may receive information about you, what you post or share. In addition, when you download or use such third-party services, they can access your public profile information. Information collected by these apps, websites or integrated services is subject to their own terms and policies. We work with third party companies who help us provide and improve our Services for you.</p>" +
            "<p>\nWe want our advertising to be as relevant and useful for you as the other information you find on our Application or Services. We do not share information that personally identifies you like your name, email etc. with advertising or analytics partners and other partners unless you give us permission. These partners must adhere to strict confidentiality obligations in a way that is consistent with this Terms and  Policy and the agreements we enter into with them.</p>" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);


        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Privacy Policy")*/;

        TextView textDesc = (TextView) findViewById(R.id.textDescPrivacyPolicy);
        textDesc.setText(Html.fromHtml(value1));
        //textDesc.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        ImageView img=(ImageView)findViewById(R.id.policyback);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PrivacyPolicyActivity.this,AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
               // finish();
        }
        });


    }
}
