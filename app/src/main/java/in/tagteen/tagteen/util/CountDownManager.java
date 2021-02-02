package in.tagteen.tagteen.util;





import android.os.CountDownTimer;

import java.util.List;

import in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.TagteenInterface.TimerEventListener;


public class CountDownManager {

     ChatThreadRecyclerViewAdapter.ViewHolder holder;
     TimerEventListener eventListener;
    public CountDownManager(){

    }

public  void getTimerCountDown(final ChatThreadRecyclerViewAdapter.ViewHolder holder, ChatMessage chatMessage, final int i, TimerEventListener timerEventListener){


    long timers = chatMessage.getPrivateMessageTime();
    eventListener =timerEventListener;
    timers = timers*1000;
    this.holder=holder;

   holder.timer = new CountDownTimer(timers, 1000) { public void onTick(long millisUntilFinished) {
            holder.privateChatTimer.setText("" + millisUntilFinished/1000 + " Sec");}
             public void onFinish() {
            holder.privateChatTimer.setText("00");
                 eventListener.onFinish(holder);
        }

    }.start();
}

}





