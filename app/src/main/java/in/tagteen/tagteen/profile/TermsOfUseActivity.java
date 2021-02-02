package in.tagteen.tagteen.profile;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.R;

public class TermsOfUseActivity extends AppCompatActivity {


    TextView mTextview;


    String value5 = "<p><b>IMPORTANT – PLEASE READ THESE TERMS CAREFULLY BEFORE USING THE TAGTEEN MOBILE APPLICATION. BY DOWNLOADING, INSTALLING, USING AND CONTINUOUSLY USING THE SERVICE, YOU ACKNOWLEDGE THAT YOU HAVE READ AND UNDERSTOOD THIS AGREEMENT AND THAT YOU AGREE TO BE BOUND BY ITS TERMS. IF YOU DO NOT AGREE TO OUR TERMS, PLEASE DO NOT USE THE SERVICE.</b></p>" +
            "<p>\nBy accepting tagteen Terms of Use, there is a binding legal agreement between <b>Tagteen Media Labs Pvt Ltd</b> (hereinafter referred to as “tagteen” the “Company,” “we,” “us,” or “our”) and <b>You</b> (“you,” “your,” or “yourself”). These terms govern your use of the Company’s website at www.tagteen.in (the “Site”), mobile software applications (the “Mobile TagteenApplication” or sometimes referred as \"Apps\"), which is also collectively referred to as our ‘Service”.</p>" +
            "<p>\n“tagteen” is an invitation only, online social media platform, which offers you instant messaging, media and article uploading and sharing within the community.</p>" +
            "<p><b>Age criteria : 13-23 years old ONLY!</b></p>" +
            "<p>\nYou are not allowed to use this application if you are not between 13 and 23 years of age. Please delete this application immediately, if you are not eligible.</p>" +
            "<p>\nIf you have an existing profile, once you turn 23 your profile will be removed or transferred to another platform with your consent and permission.</p>" +
            "<p>\nYou agree that no joint venture, partnership, employment, or agency relationship exists between you and the Company as a result of these Terms or use of the Service.</p>" +
            "<p><b>\nYour Account</b></p>" +
            "<p>\nAccount Creation: In order to access the full benefits of the Service, you will have to create a verified tagteen account in your mobile device. Tagteen has the authority to cancel your account if your account does not satisfy our verification policies. When creating your account, you must provide accurate and complete information.</p>" +
            "<p>\nYou must be at least 13 years of age to use the Services or submit any information to tagteen. If you are between 13 and 18 years of age, you may only use the Services under the supervision of a parent or legal guardian who agrees to be bound by these Terms. If you are above 23 or younger than 13 then you will not be allowed to use this application.</p>" +
            "<p>\nWe need to verify and confirm your identity until you become a permanent user in tagteen. It is your responsibility to prove that you are who you say you are and you are within the age criteria to be eligible as a permanent user in tagteen. Your account will lapse in 7days after your registration if  we are unable to verify your identity.</p>" +
            "<p>\nYou may never use another\\'s account without permission. By using the Application, you agree to safeguard the confidentiality of your account, password credentials and for limiting access to your mobile device to prevent unauthorized use of your account on the Application. You are completely and solely responsible for the security and activity that occurs on your account, If you suspect or know of any unauthorized use of your log-in credentials or any other breach of security with respect to your Account, you must notify us immediately at: admin@tagteen.in. Although the Company will not be liable for any loss or damage arising from any unauthorized use of your password and/or credentials prior to you notifying the Company of such unauthorized use or loss thereof, you may be liable for the losses of tagteen, or others, due to such unauthorized use. You understand that you are responsible for all data charges you incur by using the Services.</p>" +
            "<p><b>\nYour content</b></p>" +
            "<p>\n1.The Application or Services may allow you to create, upload, share and/ or store content, (including your text, images, voices, stories, articles, messages, information, video, music, third party links, collectively, \"Content\"). You agree to abide by our  Terms and Community Policies and not to post, store, create or otherwise publish or send through the application your content that violates it. Tagteen has the right to delete or remove your content that if it’s not in line with our terms of use and Community Policies.</p>" +
            "<p>\n2.By submitting or posting your Content through the Application or the Services, you agree to and grant tagteen a non-exclusive, royalty-free, irrevocable, worldwide, fully sub-licensable, transferable and perpetual license to use, copy, reformat, modify, publish, translate, create derivative works, display, sell and distribute your Content in any media anytime to other companies, organizations or individuals including tagteen’s marketing and promotional activities.</p>" +
            "<p>\n3.Tagteen does not claim any ownership over the Content, you retain full ownership of the Content and proprietary rights associated with the Content. Tagteen does not guarantee the validity, accuracy or legal status or confidentiality with respect to any Content associated with you and you will be solely responsible to assume all risks arising out of uploading, posting, transfer or disclosure of the Content.</p>" +
            "<p>\n4.You understand that your Content may be displayed publicly. Company does not control, take responsibility for or assume liability for your Content or any loss or damage related to your Content</p>" +
            "<p>\n5.You will be solely responsible for all Content that you upload, transmit, share or display through the Application or Services. You may only post Content that is non-confidential, you have all proprietary rights to post to the Services, is accurate and not misleading or harmful in any manner; and does not and will not violate these Terms or any applicable law, rule or regulation. Subject to applicable laws, upon deletion of the tagteen account, tagteen will delete all your Content from its database and servers within a maximum of 90 days, except to the extent required to be retained by applicable law. Tagteen does not take any responsibility of deletion of your content ( your messages, status updates, profile picture updates and other files shared by you), which might still be stored locally in your contact’s/friend’s device.</p>" +
            "<p><b>\nThird party content</b></p>" +
            "<p>\nCertain content or services made available through the Application or the Services may have been owned, operated, created or shared by other users, publishers, and other third parties including advertisements (\"Third Party Content/ Services\"). You agree and assume all risks and liabilities attached by accessing those Content/Services and tagteen is not responsible or liable for those Third Party Services.  When you access the Third Party Content, you should understand that you are no longer governed by tagteen Terms and Policies and that the terms and policies of those third party sites or services will then apply. </p>" +
            "<p><b>\nIntellectual Property</b></p>" +
            "<p>\nWe respect the intellectual property rights of others, and we expect that you will do the same. That’s why you are expressly forbidden from posting any Content that violates someone else’s intellectual property rights, including, without limitation, copyright, trademarks and other proprietary rights. You acknowledge and agree that the Application, the Services, including without limitation any content available on the Application, all trademarks, service marks and trade names and other intellectual property rights associated therewith, and text, graphics, information, logos, icons, images, sound files, software, other files are the exclusive property of tagteen or its licensors and is protected by applicable laws.</p>" +
            "<p>\nAny use except as specifically permitted under these Terms, including the reproduction, modification, distribution, republication, display or performance, of the content on the Application or the Services is strictly prohibited. We reserve the right to remove User Content alleged to be infringing without prior notice to you and at our sole discretion</p>" +
            "<p>\nSubject to these Terms, you are granted a limited, non-exclusive, non-transferable and revocable license to access and use the Services and Service Materials for your personal, non-commercial use. You agree and understand that you will use the Application and/or Services solely for lawful purposes only; and you will not, nor allow third parties on your behalf to reproduce, duplicate, copy, sell, create derivative works or otherwise exploit for any commercial purpose the Application (including its content) and/ or Services;</p>" +
           "<p>\n<b>Limitation of liability</b></p>" +
            "<p>\nTo the fullest extent permitted by applicable laws, under no circumstance will tagteen or it’s any company party ( its affiliates, respective directors, employees or agents, contractors, suppliers, licensors) be liable to you or any third party for any special, incidental, indirect, punitive or consequential damages whatsoever, including, but not limited to, damages for loss of profits or loss of data, loss of confidential information, business interruption, loss of privacy, including warranty, contract, tort (including negligence) or otherwise and any other loss whatsoever arising out of or in any way arising from or related to the use of Application, or the inability to use all or part of the Application at any time, or the service materials, including without limitation any damages caused by or resulting from reliance from any information obtained through the services (including personal injury, property damage, death), or any damage resulting from third party content used in connection with the Application even if tagteen and such company party has been advised of the possibility of such damages. Notwithstanding anything to the contrary, our maximum aggregate liability to you for any causes whatsoever, and regardless of the form of action, will at all times be limited to the amount paid by you, if any, for using any Services giving rise to the claim or Rupees one thousand only (Rs. 1000), whichever is lesser.</p>" +
            "<p>\n<b>Warranty Disclaimer</b></p>" +
            "<p>\nYOU EXPRESSLY ACKNOWLEDGE AND AGREE THAT, TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, YOUR USE OF THE APPLICATION OR SERVICES IS AT YOUR SOLE RISK AND DISCRETION. YOU SHALL BE LIABLE FOR ANY CONSEQUENCES WHATSOEVER RESULTING FROM ANYTHING TRANSMITTED OR CAUSED TO BE TRANSMITTED BY YOU, TO OR THROUGH THE APPLICATION. THE COMPANY AND THE COMPANY PARTIES (ITS AFFILIATES, AND THEIR RESPECTIVE OFFICERS, DIRECTORS, EMPLOYEES, AGENTS, SUPPLIERS AND LICENSORS) MAKE NO WARRANTY AGAINST AND WILL NOT BE LIABLE OR RESPONSIBLE FOR ANY OF THESE GIVEN BELOW INCLUDING BUT NOT LIMITED TO THE,</p>" +
            "<p>\n1. ACCURACY, SECURITY, RELIABILITY, COMPLETENESS, APPROPRIATENESS, TIMELINESS OF THE CONTENT AVAILABLE ON THE SERVICE</p>" +
            "<p>\n2.ERRORS, MISTAKES OR OMISSIONS THEREIN, OR FOR ANY DELAYS OR INTERRUPTIONS IN TRANSMISSION OF THE DATA OR STORAGE OF INFORMATION</p>" +
            "<p>\n3.LOSSES OR DAMAGES INCLUDING ANY PERSONAL INJURY ARISING FROM OR IN ANY WAY RELATED TO YOUR ACCESS OR USE OF THE APPLICATION AND/ OR SERVICES</p>" +
            "<p>\n4. ANY CONTENT POSTED, TRANSMITTED, OR OTHERWISE MADE AVAILABLE THROUGH THE APPLICATION AND/ OR SERVICES</p>" +
            "<p>\n5.BUGS, VIRUSES, WHICH MAY BE TRANSMITTED TO OR THROUGH OUR APPLICATION OR SERVICES</p>" +
            "<p>\n6.INTRUSION, DISTORTION, LOSS OR FORGERY OF DATA, ETC DUE TO ACT OF ANY THIRD PARTY, FAILURE OF ANY SOFTWARE AND/OR HARDWARE OR TELECOMMUNICATION SERVICE PROVIDER(S) USED BY US OR ANY OTHER ACT BEYOND OUR REASONABLE CONTROL.</p>" +
            "<p>\n<b>Indemnification</b></p>" +
            "<p>\nTo the maximum extent permitted by applicable law, You agree to defend, indemnify and hold harmless the company and company parties(affiliates, respective officers, directors, contractors, employees and agents), from and against any claims, loss, damages, costs, liabilities and expenses (including, but not limited to, attorney's' fees) arising out of or related to (a) your access to and use (or misuse) of the Services; (b) any User Content or Submission you provide; (c) your violation of these Terms; (d) your violation of any rights of another or of any law; or (e) your negligence and misconduct</p>" +
            "<p>\n<b>Third party disputes</b></p>" +
            "<p>\nTo the fullest extent permitted by law, any dispute you have with any third party arising out of your use of the Service, is directly between you and such third party, and you irrevocably release the Company and it’s Parties ( affiliates, respective officers, directors, contractors, employees and agents) from any and all claims and damages of every kind and nature, known and unknown, arising out of or in any way connected with such disputes.</p>" +
            "<p>\n<b>Entire Agreement</b></p>" +
            "<p>\nYou agree that these Terms constitutes the entire agreement between you and the Company with respect to the subject matter hereof, and supersedes all previous or contemporaneous agreements, whether written or oral, between the parties with respect to the subject matter herein.  </p>" +
            "<p>\n<b>Amendments</b></p>" +
            "<p>\nWe’ll notify you before we make changes to these terms and give you the opportunity to review on the revised terms before continuing to use our Services. Your continued use of the tagteen Services, following notice of the changes to our terms, policies or guidelines, constitutes your acceptance of our amended terms, policies or guidelines.</p>" +
            "<p>\n<b>Waiver</b></p>" +
            "<p>\nIf you breach these terms, and in the event of a failure of the Company to act on or enforce on the breach will not be construed as a waiver of that provision or any other provision in these Terms. </p>" +
            "<p>\n<b>Severability</b></p>" +
            "<p>\nIf any provision of these Terms shall be deemed unlawful, void or for any reason unenforceable, then that provision shall be deemed severable from these Terms and the remainder of these terms shall be valid and enforceable.</p>" +
            "<p>\n<b>Right to Remove</b></p>" +
            "<p>\nYou understand and agree that tagteen reserves the full and absolute right to takedown, delete or remove any content available on the Application in its sole discretion, without any notice to you, for any reason whatsoever including on account of your violation of these Terms, in line with the applicable laws.</p>" +
            "<p>\nCommunications/Contact Us</p>" +
            "<p>\nIf you have any questions or comments about the Services, they may be directed to tagteen at admin@tagteen.in.</p>" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);


      /*  setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Terms of Use");*/

        mTextview=(TextView)findViewById(R.id.termstextall);
        mTextview.setText(Html.fromHtml(value5));

        ImageView img=(ImageView)findViewById(R.id.termsback);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TermsOfUseActivity.this,AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
               // finish();
            }
        });


    }
}
