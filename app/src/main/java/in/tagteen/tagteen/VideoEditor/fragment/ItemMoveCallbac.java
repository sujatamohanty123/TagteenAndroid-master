package in.tagteen.tagteen.VideoEditor.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import in.tagteen.tagteen.VideoEditor.adapter.SelectedImagesAdapter;

public class ItemMoveCallbac extends ItemTouchHelper.Callback {
    private SelectedImagesAdapter selectedImagesAdapter;

    public ItemMoveCallbac(SelectedImagesAdapter selectedImagesAdapter) {
        this.selectedImagesAdapter = selectedImagesAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int moveFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swapFlag = ItemTouchHelper.RIGHT;
        return makeMovementFlags(moveFlag, swapFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        selectedImagesAdapter.onItemMove(viewHolder.getAdapterPosition(),viewHolder1.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }
}
