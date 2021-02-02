package in.tagteen.tagteen.Fragments.youthtube;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import life.knowledge4.videotrimmer.utils.AbsoluteFilePath;


/**
 * Created by lovekushvishwakarma on 01/10/17.
 */

public class VideoGallery extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    private ImageView imageback, imageRecordVideo;
    private GridView gridVideoGallery;
    private AndroidPermissionMarshMallo permissionMarshMallo;

    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    public static final Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    public static final String MEDIA_DATA = MediaStore.Video.Media.DATA;
    public static TextView output;
    protected Context _context;
    private VideoRequestHandler videoRequestHandler;
    private Picasso picassoInstance;
    private ListView listView;
    private boolean select_flag = false;
    private ImageView btn_drop_down;
    private ArrayList<String> imageList = new ArrayList<String>();
    private ArrayList<String> CopyimageList = new ArrayList<String>();
    private ArrayList<String> folderNames = new ArrayList<String>();
    private Typeface font;
    private LinearLayout popup_panel;
    private GridView _gallery;
    private Cursor _cursor;
    private Uri _contentUri;
    private String fromScreen;

    private static final long MIN_VIDEO_DURATION = 10000;

    private AdapterView.OnItemClickListener _itemClickLis = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,
                                long id) {
            String[] proj = {MEDIA_DATA};
            _cursor = managedQuery(_contentUri, proj, null, null, null);
            _cursor.moveToFirst();
            _cursor.moveToPosition(position);
            File file = new File(imageList.get(position));
            // Get length of file in bytes
            long fileSizeInBytes = file.length();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            // if (fileSizeInMB<=200) {
            String galleryText = "VID";

            String duration[] = imageList.get(position).split("\\|");
            gotoTrim(duration[0]);
        }
    };

    private void gotoTrim(String path) {
        Intent intent = new Intent(VideoGallery.this, VideoTrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, path);
        intent.putExtra("Video_pick","0");
        if (this.fromScreen != null) {
            intent.putExtra(Constants.FROM_SCREEN, this.fromScreen);
            startActivityForResult(intent, Constants.TRIM_VIDEO_REQUEST_CODE);
        } else {
            startActivity(intent);
            finish();
        }
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static String UppercaseFirstLetters(String str) {
        boolean prevWasWhiteSp = true;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (prevWasWhiteSp) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                prevWasWhiteSp = false;
            } else {
                prevWasWhiteSp = Character.isWhitespace(chars[i]);
            }
        }
        return new String(chars);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_gallery_list);

        Intent intent = getIntent();
        this.fromScreen = intent.getStringExtra(Constants.FROM_SCREEN);

        _gallery = (GridView) findViewById(R.id.gridVideoGallery);
        imageback = (ImageView) findViewById(R.id.imageback);
        imageRecordVideo = (ImageView) findViewById(R.id.imagerecordVideo);

        permissionMarshMallo = new AndroidPermissionMarshMallo(this);

        imageback.setOnClickListener(this);
        imageRecordVideo.setOnClickListener(this);


        popup_panel = (LinearLayout) findViewById(R.id.popup_panel);
        popup_panel.setVisibility(View.INVISIBLE);
        _contentUri = MEDIA_EXTERNAL_CONTENT_URI;
        setGalleryAdapter();
        videoRequestHandler = new VideoRequestHandler();
        picassoInstance = new Picasso.Builder(VideoGallery.this).addRequestHandler(videoRequestHandler).build();

        // camera gallery drop down 19-Nov-2015 start
        btn_drop_down = (ImageView) findViewById(R.id.imageDropdown);
        listView = (ListView) findViewById(R.id.listView);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = getScreenHeight() / 2 + 100;

        listView.setLayoutParams(params);

        this.printNamesToLogCat(VideoGallery.this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterImages(folderNames.get(position));
                popup_panel.setVisibility(View.GONE);
            }
        });
        _gallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    if (popup_panel.getVisibility() == View.VISIBLE)
                        popup_panel.setVisibility(View.GONE);
                return false;
            }
        });

        btn_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDropDown();
            }
        });


        _gallery.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                } else {

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    public void FilterImages(String keyWord) {
        imageList = new ArrayList<String>();
        System.out.println("keyWord : " + keyWord);
        imageList.clear();
        if (keyWord.compareToIgnoreCase("All Videos") == 0)
            imageList = CopyimageList;
        else {
            for (String fileName : CopyimageList) {
                if (!fileName.equalsIgnoreCase("default")) {
                    if (new File(fileName).getParentFile().getName().compareToIgnoreCase(keyWord) == 0)
                        imageList.add(fileName);
                }
            }
        }

        select_flag = true;
        _gallery.setAdapter(new VideoGridAdapter(VideoGallery.this, imageList));
    }

    public void handleDropDown() {
        if (popup_panel.getVisibility() == View.GONE) {
            popup_panel.setVisibility(View.VISIBLE);
            HashSet hs = new HashSet();
            hs.addAll(folderNames);
            folderNames.clear();
            folderNames.addAll(hs);
            Collections.sort(folderNames, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
            listView.setAdapter(new FolderAdapter(folderNames, VideoGallery.this));
        } else
            popup_panel.setVisibility(View.GONE);
    }

    public class VideoRequestHandler extends RequestHandler {
        public String SCHEME_VIEDEO = "video";

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_VIEDEO.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            return new Result(bm, Picasso.LoadedFrom.DISK);
        }
    }

    private void setGalleryAdapter() {
        // TODO Auto-generated method stub
        _gallery.setAdapter(new VideoGridAdapter(_context, imageList));
        _gallery.setOnItemClickListener(_itemClickLis);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageback:
                finish();
                break;
            case R.id.imagerecordVideo:
                openVideoCapture();
                break;
        }
    }

    private void printNamesToLogCat(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION, MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED);
        ArrayList<File> userFiles = new ArrayList<>();
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();
            int count = 0;
            while (c.moveToNext()) {
                Log.d("VIDEO", c.getString(0));
                long millis = c.getLong(1);
                // exclude videos less than min duration
                if (millis < MIN_VIDEO_DURATION) {
                    continue;
                }
                String duraiton = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                Log.d("VIDEO duraiton", duraiton);
                File path = new File(c.getString(0) + "|" + duraiton);
                userFiles.add(path);
                count++;
            }

            Collections.sort(userFiles, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    int compareResult = 0;
                    Date d1 = new Date(file.lastModified());
                    Date d2 = new Date(t1.lastModified());
                    try {
                        compareResult = d1.compareTo(d2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        compareResult = d2.compareTo(d1);
                    }
                    return compareResult;
                }
            });

            Collections.reverse(userFiles);
            CopyimageList.add("default");
            for (File invidual : userFiles) {
                imageList.add(invidual.getAbsolutePath());
                CopyimageList.add(invidual.getAbsolutePath());
                if (invidual.getParentFile() != null) {
                    folderNames.add(UppercaseFirstLetters(invidual.getParentFile().getName()));
                }
            }
            folderNames.add(0, "All Videos");
            HashSet hs = new HashSet();
            hs.addAll(folderNames);
            folderNames.clear();
            folderNames.addAll(hs);
            Collections.sort(folderNames, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = getScreenHeight() / 2 + 100;
            if (folderNames.size() > 8)
                listView.setLayoutParams(params);
            else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                listView.setLayoutParams(params);
            }
            c.close();
        }
    }

    public class VideoGridAdapter extends BaseAdapter {
        ArrayList<String> imageList;
        Context context;

        public VideoGridAdapter(Context context, ArrayList<String> imageList) {
            this.imageList = imageList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return this.imageList.size();
        }

        @Override
        public String getItem(int i) {
            return this.imageList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.video_grid_view, null);
                holder.camera_image = (ImageView) view.findViewById(R.id.cam_item_image);
                holder.textTime = (TextView) view.findViewById(R.id.textTime);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.camera_image.clearColorFilter();
            holder.camera_image.setVisibility(View.VISIBLE);

            String path = imageList.get(position);
            String duration[] = path.split("\\|");
            picassoInstance.load(videoRequestHandler.SCHEME_VIEDEO + ":" + duration[0]).into(holder.camera_image);
            if (duration.length > 0) {
                try
                {
                    String pp0 = duration[0];
                    String pp1 = duration[1];
                    holder.textTime.setText(duration[1]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return view;
        }

        class ViewHolder {
            ImageView camera_image;
            TextView textTime;
        }
    }

    public class FolderAdapter extends BaseAdapter {
        ArrayList<String> data;
        Context context;
        LayoutInflater layoutInflater;

        public FolderAdapter(ArrayList<String> data, Context context) {
            super();
            this.data = data;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            return data.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.camera_roll_popup_layout, null);
            TextView folder_name = (TextView) convertView.findViewById(R.id.folder_name);
            folder_name.setTypeface(font);
            folder_name.setText(data.get(position));
            return convertView;
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_title_rationale));
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(VideoGallery.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.TRIM_VIDEO_REQUEST_CODE) {
                setResult(RESULT_OK, data);
                finish();
            } else if (requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    this.gotoTrim(AbsoluteFilePath.getPath(VideoGallery.this, selectedUri));
                } else {
                    Utils.showShortToast(this, "Failed to fetch video. Please try again");
                }
            }
        }
    }

    private void openVideoCapture() {
        int maxTime = 20 * 60; // max limit 20 mins
        Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoCapture.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxTime);
        startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
    }
}
