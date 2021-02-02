package in.tagteen.tagteen.LocalCasha;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;

import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.TagteenInterface.DownLoadInterface;

public class SaveMediaCash  extends AsyncTask<String, String, String> {
    private Context context;
    private ProgressDialog pDialog;
    String image_url;
    URL ImageUrl;
    String myFileUrl1;
    Bitmap bmImg = null;
    private SQLiteDatabase db;
    private DataBaseHelper dbHelper;
    DownLoadInterface downLoadInterface;
    ChatMessage chatMessage;
    String Id;

    File imageFile;

    public SaveMediaCash(Context context, DownLoadInterface downLoadInterface ,SQLiteDatabase db) {

        this.context = context;
        this.downLoadInterface = downLoadInterface;
        this.db = db;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... args) {

        InputStream is = null;

        try {

            ImageUrl = new URL(args[0]);
            Id = args[1];


            HttpURLConnection conn = (HttpURLConnection) ImageUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            bmImg = BitmapFactory.decodeStream(is, null, options);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            String path = ImageUrl.getPath();
            String idStr = path.substring(path.lastIndexOf('/') + 1);
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath()
                    + "/tagteen/");
            dir.mkdirs();
            String fileName = idStr;
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bmImg.compress(CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();

             imageFile = file;
            MediaScannerConnection.scanFile(context,
                    new String[] { imageFile.getPath() },
                    new String[] { "image/jpeg" }, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }

        return null;
    }




    @Override
    protected void onPostExecute(String args) {
        updateDownLoadStatus(Id, DatabaseContracts.IS_TRUE,imageFile.getPath());
        downLoadInterface.mediaDownloadDone(Id , imageFile.getPath());

    }

    @Override
    protected void onProgressUpdate(String... values){
        downLoadInterface.mediaDownLoadProgress(Integer.parseInt(values[0]));
    }

    private boolean updateDownLoadStatus(String Id, String status,String path) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS, status);
        contentValues.put(DatabaseContracts.ChatContractDataBase.LOCATPATH, path);
        db.update(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase._ID + "= ? ", new String[]{Id});
        return true;
    }
}

