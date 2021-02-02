package in.tagteen.tagteen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategorySpinnerAdapter;
import in.tagteen.tagteen.Model.knowledge.KnowledgeCategories;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.DataCache;

public class AddKnowledgePostActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private VideoView videoView;
    private Spinner spinCategory;
    private Spinner videoLanguage;
    private EditText txtDescription;
    private ImageView imgBack, imgUpload;
    private LinearLayout layoutCategory;

    private CategorySpinnerAdapter spinnerAdapter;
    private Map<String, String> categoryNameVsIdMap;

    private String filePath;
    private String postType;
    private static final int CATEGORY_SELECT = -1;

    private ArrayList<String> languagesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knowledge_post_activity);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            this.filePath = extraIntent.getStringExtra(Constants.PATH_VIDEO_RECORDED);
            this.postType = extraIntent.getStringExtra(Constants.KNOWLEDGE_POST_TYPE);
        }

        this.progressBar = findViewById(R.id.progressBar);
        this.progressBar.setMax(100);
        this.imgBack = findViewById(R.id.imgBack);
        this.imgUpload = findViewById(R.id.imgUpload);
        this.videoView = findViewById(R.id.videoView);
        this.spinCategory = findViewById(R.id.spinnerCategory);
        this.txtDescription = findViewById(R.id.txtDescription);
        this.layoutCategory = findViewById(R.id.layoutCategory);
        this.videoLanguage = findViewById(R.id.spinLanguage);

        if (Constants.KNOWLEDGE_QUESTION_TYPE.equals(this.postType)) {
            this.loadCategories();
        } else {
            this.layoutCategory.setVisibility(View.GONE);
        }

        this.initLanguages();

        MediaController myMediaController = new MediaController(this);
        this.videoView.setMediaController(myMediaController);

        this.videoView.setVideoURI(Uri.parse(this.filePath));
        this.videoView.start();
    }

    private void bindEvents() {
        this.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadKnowledgePost();
            }
        });
    }

    private void loadCategories() {
        List<KnowledgeCategories.Category> categories = DataCache.getKnowledgeCategories();
        if (categories == null) {
            return;
        }

        ArrayList<CategoryBean> categoryList = new ArrayList<>();
        categoryList.add(new CategoryBean(-1, "Select"));
        for (KnowledgeCategories.Category category : categories) {
            if (!category.getName().equalsIgnoreCase("All")) {
                categoryList.add(new CategoryBean(
                        Integer.valueOf(category.getId()), category.getName()));
            }
        }
        this.spinnerAdapter = new CategorySpinnerAdapter(this, R.layout.only_textvew, categoryList);
        this.spinCategory.setAdapter(this.spinnerAdapter);
    }

    private void uploadKnowledgePost() {
        CategoryBean categoryBean = (CategoryBean) this.spinCategory.getSelectedItem();
        int categoryId = CATEGORY_SELECT;
        if (categoryBean != null) {
            categoryId = categoryBean.getId();
        }
        if (categoryId == CATEGORY_SELECT && Constants.KNOWLEDGE_QUESTION_TYPE.equals(this.postType)) {
            Utils.showAlertDialog(this,"Please choose a category", "Alert");
            return;
        }

        String description = this.txtDescription.getText().toString();
        if (Constants.KNOWLEDGE_QUESTION_TYPE.equals(this.postType)) {
            if (description.trim().length() == 0) {
                this.txtDescription.setError("Please give some description.");
                this.txtDescription.requestFocus();
                return;
            }
        }

        FileDataSender.FILE_PATH = this.filePath;
        FileDataSender.HAS_FILE_TO_UPLOAD = true;
        FileDataSender.DESCRIPTION = description;
        FileDataSender.CATEGORY_ID = categoryId;

        finish();
    }

    private void initLanguages() {
        this.languagesList = new ArrayList<String>();
        this.languagesList.add("Assamese");
        this.languagesList.add("Bengali");
        this.languagesList.add("English");
        this.languagesList.add("Gujarati");
        this.languagesList.add("Hindi");
        this.languagesList.add("Kannada");
        this.languagesList.add("Kashmiri");
        this.languagesList.add("Konkani");
        this.languagesList.add("Malayalam");
        this.languagesList.add("Manipuri");
        this.languagesList.add("Marathi");
        this.languagesList.add("Oriya");
        this.languagesList.add("Punjabi");
        this.languagesList.add("Sanskrit");
        this.languagesList.add("Tamil");
        this.languagesList.add("Telugu");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.languagesList);
        this.videoLanguage.setAdapter(adapter);
    }
}
