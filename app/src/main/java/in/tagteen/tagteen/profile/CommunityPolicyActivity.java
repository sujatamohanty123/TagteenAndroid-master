package in.tagteen.tagteen.profile;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.R;

public class CommunityPolicyActivity extends AppCompatActivity {



     String value2= "<p>\nPlease read the following carefully, as we have a zero tolerance policy regarding misuse of Content (including your text, images, voices, stories, articles, messages, information, video, music, third party links shared by you, collectively, \"Content\"), and any violation may result in the immediate suspension or termination of your account. </p>" +
             "<p>\nAt tagteen we are committed to maintain a fun, welcoming, and safe community for all our users.  For that reason, we've developed a set of Community Policies, outlined below.</p>" +
             "<p>\nRespect everyone on tagteen, don’t spam people or post nudity</p>" +
             "<p>\n<b>1.No Nudity Or Posting Private Content</b></p>" +
             "<p>\nOverly sexualized Content, indecency and/or obscenity is strictly forbidden on tagteen. Please Remember, Where appropriate, we reserve the right to refer the aforementioned abusive content to local and/or federal law enforcement.</p>" +
             "<p>\nWe don’t allow exposure of any genitalia, whatsoever. However, we do allow photographs of paintings, sculptures, and other art that depicts nude figures. Explicit images or videos of sexual activity are prohibited. We also do not allow any sort of  inappropriate or revealing clothing to be posted on our platform. Sexually suggestive or provocative activity, which includes any sexual gestures, posture, dances, and/or general sexual behavior are strictly prohibited. For safety reasons, there are times when we may remove images that show nude or partially-nude children. Please refrain from the use of Content which invades another person’s privacy or which divulges their private or confidential information.</p>"+
             "<p>\n<b>2. No Bullying, Harassment, Or Hateful Language.</b></p>" +
             "<p>\nHaters Keep out.</p>" +
             "<p>\nWe do not tolerate bullying or harassment and hate speech of any kind purposefully targeting an user or group of individuals, which will be strongly dealt with by tagteen. Such behaviour will result in ban and suspension of user account and such content may be transferred to the law enforcement agencies if required.</p>" +
             "<p>\nWe remove content of violence or attack or discriminatory remarks on anyone based on their race, appearance, ethnicity, nationality, gender identity, sexual orientation, religion or faith and disabilities.This includes specific threats of physical harm as well as threats of theft and vandalism etc.</p>" +
             "<p>\nWe remove content (sharing photos or videos) that targets of physical bullying of a person to degrade or shame them, personal information meant to blackmail or harass someone, and repeated unwanted messages.</p>" +
             "<p>\n<b>3. No Criminal Activity.</b></p>" +
             "<p>\nTagteen is not a place to facilitate or organize any criminal activity, which has the potential to directly or indirectly cause physical or financial harm to people, businesses, animals and properties. We work with law enforcement when we believe there is a genuine risk of physical harm or direct threats to public safety.</p>"+
             "<p>\n<b>4. No Violent, Self Injury, or Graphic Content</b></p>"+
             "<p>\nWe do not allow Content that promotes or encourages physical violence towards yourself or others, and/or graphic content including child/animal abuse, self-mutilation, bodily harm is strictly prohibited. We remove disturbing and graphic images and videos when they are shared for sadistic pleasure or to glorify violence.</p>"+
             "<p>\n<b>5. No Spam</b></p>" +
             "<p>\nPlease respect our community and refrain from spamming anyone, under any circumstances. Failure to comply with this Guideline may result in the immediate termination of your account and permanent ban from using tagteen.</p>"+
             "<p>\n<b>6. No Violation of Copyrights</b></p>"+
             "<p>\nUsers are forbidden from posting any Content that violates someone else’s intellectual property rights, including copyright. We expect you to own all of the content and information you post on tagteen. We ask that you respect copyrights, trademarks, and other legal rights.</p>"+
             "<p><b>\n7. Reporting Abuse</b></p>"+
             "<p>\nTo keep the tagteen community safe and secure, we heavily rely on our vigilant users. If you see something on tagteen that you believe violates our terms and policies, please report it us. If you come across any such content or activity, please report the activity to us via email at: admin@tagteen.in with the offender’s details and a screenshot of the abusive content. On top of that, you can also report or block that user from the app. Our dedicated and prompt team of reviewers will assess the content and  will take appropriate action in dealing with the matter. The penalty for violating our Policies may vary, in our sole discretion, based on the severity of the violation.</p>";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_policy);

       /* setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Community Policy");
*/
        TextView textDesc = (TextView) findViewById(R.id.textDescCommPolicy);
        textDesc.setText(Html.fromHtml(value2));
        //textDesc.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        ImageView img=(ImageView)findViewById(R.id.commpolicyback);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CommunityPolicyActivity.this,AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //finish();

            }
        });

    }
}
