package in.tagteen.tagteen.profile;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.profile.adapter.ExpandableListAdapter;

public class FaqActivity extends AppCompatActivity {

    private static ExpandableListView expandableListView;
    private static ExpandableListAdapter adapter;
    private ImageView faqback1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        expandableListView = (ExpandableListView) findViewById(R.id.simple_expandable_listview);
        faqback1=(ImageView)findViewById(R.id.faqback);
        faqback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FaqActivity.this,AppSettings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);

        setItems();
        setListener();

    }

    // Setting headers and childs to expandable listview
    void setItems() {

        // Array list for header
        ArrayList<String> header = new ArrayList<String>();

        // Array list for child items
        List<String> child1 = new ArrayList<String>();
        List<String> child2 = new ArrayList<String>();
        List<String> child3 = new ArrayList<String>();
        List<String> child4 = new ArrayList<String>();
        List<String> child5 = new ArrayList<String>();
        List<String> child6 = new ArrayList<String>();
        List<String> child7 = new ArrayList<String>();
        List<String> child8 = new ArrayList<String>();
        //List<String> child9 = new ArrayList<String>();
        List<String> child10 = new ArrayList<String>();
        List<String> child11 = new ArrayList<String>();
        List<String> child12 = new ArrayList<String>();

        // Hash map for both header and child
        HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();

        // Adding headers to list

            header.add("TagTeen");
            header.add("Verification");
            header.add("Account");
            header.add("Send a tag offer");
            header.add("Friend Requests");
            header.add("Rewards");
            header.add("Showroom");
            header.add("TeenFeed");
            //header.add("Live stories");
            //header.add("Tag Messenger");
            header.add(" Tagteen Rockstar");
            header.add("Sharing on tagteen");


            child1.add(String.valueOf(Html.fromHtml(" “Tagteen” is an exclusive, invitation only Social Platform for Like-Minded young Indians between 13 and 23.")));
            child2.add( "a)How do I become a Verified/Permanent Member of tagteen?\n" +
                    "\nUnless you have downloaded and installed the app through a tag offer from a friend who knows you, you will enter the application as an Unverified Member. You have 7 days to verify your identity and prove that your age is between 13 - 23 years of age. There are several ways to verify your identity." +
                    "\nSearch and send a friend request to someone you know who is a permanent member in tagteen. Upon acceptance of that friend request you become a permanent member and a friend of that person." +
                    "If you are unable to find anyone in tagteen, You will get a phone call from one of our friendly members within 24 hours of your registration. They will ask you the relevant questions to prove your identity. If we are not happy we might ask you to send us a photo with instructions. Do not worry as it is as much our responsibility as it is yours to verify you and make you a part of the community.");
            child2.add(" b)What happens If I am unable to verify my identity?\n" +
                    "\nYou have 7 days to verify your identity, or your account with tagteen would lapse. You need to re-register and earn another 7 days to fulfill the verification requirements.\n");
            child2.add("c)What am I allowed to do as an Unverified Member?\n"+
                    "\nAs a Unverified member your privileges are limited. Your account is in “View Only” status, where you can only access the public features like “teenfeed” articles according to your hobbies/interests/passion and Showroom videos posted by the verified tagteen members. You will not be allowed to post, like, comment, share, chat with anyone until your account is verified. You can search for and send friend requests to your verified friends " );
        // Adding child data
            child3.add("a)How do I create a tagteen account?\n" +
                    "\nDownload the tagteen app from Google Play store. Once it is downloaded, either you open it from Play store or click the tag icon installed in your phone and open it. Fill in all personal information including contact details to get a Unique tag no." );
            child3.add("b)How do I change my profile information?\n" +
                    "\n To update your profile information, including your username and the email address associated with your account:" +
                    "\n Go to your profile, which is the icon placed in the right bottom corner of your app." +
                    "\n Tap the Edit Icon placed near your profile photo" +
                    " After you make all changes, do not forget to click the UPDATE button to save all your changes.");
            child3.add("I can neither log in nor have I received a verification code?\n" +
                    " We suggest that you exit the app and relaunch it. Or you can switch your network connection from Wi-Fi to data. If you are still unable to receive the verification code, you can send a screenshot of that page with your phone number to admin@tagteen.in for us to help.");
            child3.add("How do I deactivate and/or delete my account/n?\n" +
                    "\n If for any reason you do not want to use tagteen anymore, we would recommend you temporarily disable your account through Deactivation, where your profile, photos, fans, friends, videos, comments and likes will be hidden until you reactivate it by logging back in. To deactivate your account, :" +
                    "\n Go to your profile, which is the icon placed in the right bottom corner of your app." +
                    "\n Tap the settings icon near your profile photo" +
                    "\n Then go to Manage Account, one of the options there is deactivation. For security reasons, You need to re-enter your password before you deactivate." +
                    " We strongly DO NOT recommend deleting your account, as once it’s done it will permanently remove all your photos, videos, fans, friend’s and their details.  For security reasons, you need to re-enter your password before you delete your account.</p>");
   child4.add("a. How and Why do I send a tag?\n" +
                    "\nTagteen is more fun and exciting with all your friends on it. “tag+” is a feature where, you send a tag offer to your friends who are not on tagteen to come and join with you as a friend. " +
                    "\nYou will find a tag+ icon in the top right corner of your home screen." +
                    "\nOnly a verified member can send a tag offer to their friends. When you send a tag offer you are not only letting someone download the app and enter tagteen but you are approving and verifying their identity as well. Please make sure you only send a tag offer to someone of your age or between 13-23 years of age" +
                    "No outsiders (younger than 13 and older than 23) are allowed, so it’s your responsibility to make sure you are NOT sending the tag offer to an unknown or ineligible person. Once clicked a pop up box would open up, where you need to punch in the mobile number of your friend. Your friend will get a text with your name, app details and a link to the Play Store/TagteenApplication Store. Once downloaded they need to register using the same phone number and they will automatically be verified by us and will become your friend and a part of the unique tagteen community.");
        child5.add("a. How do I send a friend request?\n" +
                "\nSearch for any verified member on tagteen by clicking on the search icon on the top right side of your application. You can search by their name, email, phone number, school name and interest. Once you find them, you can go to their profile and click on “tag me”. A request will be sent to that person awaiting their action.\n");
        child6.add("At tagteen, we believe in promoting, rewarding, encouraging and supporting our users. We believe that everyone has talent and without financial freedom " +
                "and independence to follow it invariably goes waste. We want to help you financially and give you that launchpad and independence to follow your passion " +
                "and explore various opportunities in your career." +
                "\nTo earn money, you simply" +
                "\n Step 1: Post a video of your talent in showroom." +
                "\n Step 2: Provide your UPI details (G Pay, Phone Pe, Paytm)" +
                "\n Step 3: 1 U Rock = 1 rupee. Go to the rewards section of your app and keep track of your income. Money will be deposited weekly." +
                "\n Note: Only talent videos will be rewarded. Please don't upload any Musically or Tik tok videos.");
        child7.add("\na. What is Showroom?\n" +
                "\nDo you have a Talent??\n" +
                "\nCan you sing, dance, study, play, do magic, juggle or do anything you are talented. " +
                "\nThen let us give you “Showroom”. It’s a platform, where you can unleash your star potential. Simply Post a Video of your talent, let your friends, fans and the whole tagteen community like it and be famous.\n" +
                "\nIf you have the talent then we have the platform and support to make your dreams come true." +
                "\nWe only allow talent related videos in Showroom. Please do not post irrelevant videos as they would be removed by our team.");
        child8.add("\na. What is teenfeed?\n" +
                "\nAt tagteen, via teenfeed we empower and educate you with informative and entertaining articles. The articles are based around your interests and passion to help you be what you want to be. Teenfeed is not just limited to just accessing relevant articles, you can also write articles in the areas that might interest you and let others read them and get benefitted by them.");
        child8.add("\nb. What If I want to write articles for/on behalf of the company?\n" +
                "\nIf you love writing and have written articles in teenfeed, we would love to have you in our team and write on behalf of tagteen. Just drop us an email with your credentials, we will get back to you as soon as possible.\n");
        //child9.add("\nSelfieSnap is a Real time Selfie Share with your friends. SelfieSnap is a unique way of expressing yourself and sharing your status, as it only lets you capture or record your Live moments and does not allow you to upload anything from the past from your phone gallery. Now you can share with your friends your true Live status (what you are currently doing) without having to stream live. It also lets you take a Clippie (a 10sec Selfie Video) to let your friends know your current status through a video. Your Friends and Fans can like your status awarding you the “Selfie King” or “Selfie Queen” everyday. A SelfieSnap is available only for 24hrs to be accessed by your friends.");
        /*child10.add("\na. What’s new in the messenger?\n" +
                "\nTag messenger is an instant messaging feature in our application that has been developed and customised for teens, taking your interests, your privacy, your security and loads of fun into consideration. Our unique features like Selfie Reaction, Sound Mojis and Private Mode are going to provide you a private, fun-filled and engaging experience. ");
        child10.add("\nb. How do I use the Selfie-Reaction feature?\n" +
                "\n“Selfie reaction” is a personalised way of sharing your reactions with your friends during chat. We wanted to give you something more expressive, beyond texting and emojis and with Selfie Reaction you’re now to able to just choose your mood (e.g. happy, sad, laugh, crazy, sleepy etc.) from the given list, take a matching selfie with the mood and use it during chat. Once saved,the selfie reaction is stored in your chat screen for future use. You can edit and update your selfie reaction whenever you want and have fun with it. \n");
        child10.add("\nc. What are the features of the Private chat?\n" +
                "\nWe understand how important Privacy is for you. It’s in our DNA as well. That’s why we have created a completely safe, private and secured chat messenger which not only protects your chat data with us, but also enhances your privacy while chatting with your friends. The private features we have implemented for your safety are,\n" +
                "\nNo screenshots allowed throughout the Application including Chat messenger." +
                "\nEnd-to-End Encryption has been implemented in our chat messenger, which means only you and the person you are chatting with can read what is sent, and nobody in between not even tagteen has the access to it. In simple words, your messages, media files, documents etc. are secured with a unique lock and only the recipient and you have a special key to access them." +
                "\nPrivate Mode: Although we ensured that your data does not fall into wrong hands through E2E Encryption, we still believe that you should have the right to choose the person you want to share your data (messages, media etc) with during chat. We have created “Private Mode”, where all your messages, media files, documents etc. will disappear after a certain time set by you. Private mode gives you the peace of mind, extra security and protection, where you can confidently chat, where the misuse of your data by the person you are chatting with is minimised." +
                "\nNo Forward, No Copy is allowed when in Private Mode." );
        child10.add("\nd. What is the difference between Normal chat mode and Private chat mode?\n" +
                "\nForward and copy are allowed in Normal mode.");
      */  child10.add("\nThe purpose of tagteen is to give the teens a voice and a platform to express their opinion, showcase their talents and fulfill their dreams globally. Periodically we choose someone in our platform with immense talent and reward and support them to fullfill their dreams. You could be very studious or a singer, dancer. magician, writer, biker, photographer, sportsperson, model, actor, entrepreneurs, beautician, cook etc, you have an opportunity to become a tagteen rockstar. \n" +
                "\n When you become a rockstar, we will not only make you famous but also help and support you achieve your goals. We will be there with you every step of the way.\n" +
                "\n Just post videos to show off your talent and you could be the next tagteen rockstar. Don’t wait for someone else to do it for you, Post a video. Show the world what you’ve got and you could be the next tagteen rockstrar.");
        child11.add("Tagteen doesn’t allow me to share anything on other social media apps?\n " +
                "“What happens in tagteen, stays in tagteen”");

        // Adding header and childs to hash map
        hashMap.put(header.get(0), child1);
        hashMap.put(header.get(1), child2);
        hashMap.put(header.get(2), child3);
        hashMap.put(header.get(3), child4);
        hashMap.put(header.get(4), child5);
        hashMap.put(header.get(5), child6);
        hashMap.put(header.get(6), child7);
        hashMap.put(header.get(7), child8);
        //hashMap.put(header.get(8), child9);
        //hashMap.put(header.get(9), child10);
        hashMap.put(header.get(8), child10);
        hashMap.put(header.get(9), child11);

        adapter = new ExpandableListAdapter(FaqActivity.this, header, hashMap);

        // Setting adpater over expandablelistview
        expandableListView.setAdapter(adapter);
    }

    // Setting different listeners to expandablelistview
    void setListener() {

        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView listview, View view,
                                        int group_pos, long id) {

                /*Toast.makeText(FaqActivity.this,
                        "You clicked : " + adapter.getGroup(group_pos),
                        Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        expandableListView
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    // Default position
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)

                            // Collapse the expanded group
                            expandableListView.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }

                });

        // This listener will show toast on child click
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView listview, View view,
                                        int groupPos, int childPos, long id) {
        /*        Toast.makeText(
                        FaqActivity.this,
                        "You clicked : " + adapter.getChild(groupPos, childPos),
                        Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });
    }
}
