package in.tagteen.tagteen.widgets;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

final class RowItem {
    private final AsymmetricItem item;
    private final int index;

    RowItem(int index, AsymmetricItem item) {
        this.item = item;
        this.index = index;
    }

    AsymmetricItem getItem() {
        return item;
    }

    int getIndex() {
        return index;
    }
}