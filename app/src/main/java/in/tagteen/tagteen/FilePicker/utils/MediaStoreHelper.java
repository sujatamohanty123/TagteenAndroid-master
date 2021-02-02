package in.tagteen.tagteen.FilePicker.utils;

import androidx.fragment.app.FragmentActivity;


import in.tagteen.tagteen.FilePicker.cursors.DocScannerTask;
import in.tagteen.tagteen.FilePicker.cursors.loadercallback.FileResultCallback;
import in.tagteen.tagteen.FilePicker.item.Document;

import java.util.List;

public class MediaStoreHelper {

  public static void getDocs(FragmentActivity activity, List<String> itemfilter, FileResultCallback<Document> fileResultCallback)
  {
    new DocScannerTask(activity,fileResultCallback,itemfilter).execute();
  }
}