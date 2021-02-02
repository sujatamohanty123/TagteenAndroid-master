package in.tagteen.tagteen.chatting.emoji;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by lovekushvishwakarma on 12/09/17.
 */

class EmojiAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return Emoji.collections.length;
    }

    @Override
    public Integer getItem(int i) {
        return Emoji.collections[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ImageView imageEmoji = (ImageView) view;
        if (imageEmoji == null) {
            imageEmoji = new ImageView(viewGroup.getContext());
            int size = (int) MyUtils.toDp(viewGroup.getContext(), 30);
            imageEmoji.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        }
        imageEmoji.setImageResource(Emoji.iconOf(getItem(i)));
        return imageEmoji;
    }

}
