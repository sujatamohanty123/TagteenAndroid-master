package in.tagteen.tagteen.FilePicker.view;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import in.tagteen.tagteen.FilePicker.adapter.FilterAdapter;
import in.tagteen.tagteen.R;

import java.util.ArrayList;
import java.util.List;



public class FilterFragment extends DialogFragment {
    private Button btnAdd, btnClear;
    private EditText editAdd;
    private RecyclerView recyclerView;
    private FilterAdapter adapter;
    private List<String> itemfilter;

    public static FilterFragment newInstance(ArrayList<String> itemfilter) {
        Bundle args = new Bundle();
        args.putStringArrayList("filter", itemfilter);
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemfilter = getArguments().getStringArrayList("filter");

    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filepicker_fragmentfilter, container, false);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        editAdd = (EditText) view.findViewById(R.id.editAdd);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleMain);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new FilterAdapter(getActivity(), itemfilter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

/*        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editAdd.getText().toString().matches("")) {
                    itemfilter.add("." + editAdd.getText().toString());
                    adapter.notifyDataSetChanged();
                    ((FilePickerActivity) getActivity()).getDocument();
                    getDialog().dismiss();
                } else {
                    editAdd.setError("Empty Filter");
                }
            }
        });*/

   /*     btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemfilter.clear();
                ((FilePickerActivity) getActivity()).getDocument();
                getDialog().dismiss();
            }
        });*/
    }
}
