package in.tagteen.tagteen.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.youthtube.bean.YouthDataItem;

public class AppUtils {
	public static String loadJSONFromAsset(Context context, String filename) {
		String json;
		try {

			InputStream is = context.getAssets().open("json/" +filename+".json");

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);
			
			is.close();

			json = new String(buffer, "UTF-8");
			Log.v("AppUtils", "JSONArray=>"+json);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}


	public static ArrayList<YouthDataItem> getlistDatatemp(){
		ArrayList<YouthDataItem>list=new ArrayList<>();
		for(int i=0;i<10;i++){
			YouthDataItem bean1=new YouthDataItem("John Corter", "TAG000012", "When a door closes another door should open, but if it doesn't then go in through the window. - http://coolfunnyquotes.com ",
					"http://techslides.com/demos/sample-videos/small.webm","https://s-media-cache-ak0.pinimg.com/originals/db/e1/b4/dbe1b47e5a864e7fe7173345a12ac841.jpg", "userAvtar", 22,12, 5, 13, 1, false,false);


			list.add(bean1);

			YouthDataItem bean2=new YouthDataItem("John Corter", "TAG000013", "When a door closes another door should open, but if it doesn't then go in through the window. - http://coolfunnyquotes.com ",
					"http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4","http://jzvd-pic.nathen.cn/jzvd-pic/1bb2ebbe-140d-4e2e-abd2-9e7e564f71ac.png", "userAvtar", 22,12, 5, 13, 1, false,false);

			list.add(bean2);
		}

		return list;
	}

public static void myToast(Context context,String msg){
	Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
}

}
