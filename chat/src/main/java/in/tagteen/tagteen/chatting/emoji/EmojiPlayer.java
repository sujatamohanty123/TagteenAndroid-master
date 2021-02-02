package in.tagteen.tagteen.chatting.emoji;

import android.content.Context;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;

/**
 * Created by tony00 on 3/3/2019.
 */
public class EmojiPlayer {

    private Context context;
    private MediaPlayer player;

    private EmojiPlayer(@NonNull Context context) {
        this.context = context;
    }

    public static EmojiPlayer createPlayer(@NonNull Context context){
        return new EmojiPlayer(context);
    }

    public void play(int emojiId) {
        if (player != null && player.isPlaying()) {
            player.stop();
        }

        player = MediaPlayer.create(context, Emoji.soundOf(emojiId));
        player.start();
    }
}
