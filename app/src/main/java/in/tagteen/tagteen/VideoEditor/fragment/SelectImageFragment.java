package in.tagteen.tagteen.VideoEditor.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.VideoEditor.adapter.SelectImageAdapter;
import in.tagteen.tagteen.VideoEditor.adapter.SelectedImagesAdapter;
import in.tagteen.tagteen.VideoEditor.util.MultiUtils;

public class SelectImageFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView rv_select_image, rv_selected_images;
    private SelectImageAdapter selectImageAdapter;
    private SelectedImagesAdapter selectedImagesAdapter;
    private List<SelectImageInfo> selectedImages = new ArrayList<>();
    private TextView tv_selected_images;
    private LinearLayout ll_selected_images;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_image, null);
        activity = getActivity();

        rv_select_image = view.findViewById(R.id.rv_select_image);
        rv_selected_images = view.findViewById(R.id.rv_selected_images);
        tv_selected_images = view.findViewById(R.id.tv_selected_images);
        ll_selected_images = view.findViewById(R.id.ll_selected_images);

        rv_select_image.getItemAnimator().setChangeDuration(0);
        rv_select_image.getItemAnimator().setAddDuration(0);

        List<SelectImageInfo> imageDatas = MultiUtils.getImageDatas(activity);

        GridLayoutManager layoutManager = new GridLayoutManager(activity, 4);
        rv_select_image.setLayoutManager(layoutManager);
        selectImageAdapter = new SelectImageAdapter(imageDatas);
        rv_select_image.setAdapter(selectImageAdapter);

        selectedImagesAdapter = new SelectedImagesAdapter(selectedImages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_selected_images.setLayoutManager(linearLayoutManager);
        rv_selected_images.setAdapter(selectedImagesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemMoveCallbac(selectedImagesAdapter));
        itemTouchHelper.attachToRecyclerView(rv_selected_images);


        selectImageAdapter.setOnItemClickListener(new SelectImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SelectImageInfo item, int position) {

                if (item.isSelected()) {
                    item.setSelected(false);
                    selectedImages.remove(item);
                } else {
                    if (selectedImages.size() > 29) {
                        MultiUtils.showToast(activity, "最多只能选择30张图片");
                        return;
                    }
                    item.setSelected(true);
                    item.setSelectedPos(position);
                    selectedImages.add(item);
                }
                selectImageAdapter.notifyItemChanged(position);
                selectedImagesAdapter.notifyDataSetChanged();
                if (selectedImages.size() > 0) {
                    ll_selected_images.setVisibility(View.VISIBLE);
                    tv_selected_images.setText(selectedImages.size() + "");
                    rv_selected_images.scrollToPosition(selectedImages.size() - 1);
                } else {
                    ll_selected_images.setVisibility(View.GONE);
                }
            }
        });

        selectedImagesAdapter.setOnCancelSelectedListener(new SelectedImagesAdapter.OnCancelSelectedListener() {
            @Override
            public void onOnCancelSelected(SelectImageInfo item, int position) {
                selectedImages.remove(item);
                selectedImagesAdapter.notifyItemRemoved(position);

                int selectedPos = item.getSelectedPos();
                imageDatas.get(selectedPos).setSelected(false);
                selectImageAdapter.notifyItemChanged(selectedPos);

                if (selectedImages.size() > 0) {
                    ll_selected_images.setVisibility(View.VISIBLE);
                    tv_selected_images.setText(selectedImages.size() + "");
                } else {
                    ll_selected_images.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    public List<SelectImageInfo> getSelectedImages() {
        return selectedImages;
    }

}
