package com.wshang.soybean.album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wshang.soybean.R;
//import com.wshang.soybean.news.CreateInfoArticle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/****************************************** 
 * 类描述： 相册管理类
 * 类名称：PhotoAlbumActivity
 * @author: lp
 *****************************************/
public class PhotoAlbumActivity extends Activity{
	private GridView aibumGV;
	private List<PhotoAibum> aibumList;

	// 设置获取图片的字段信�?
	private static final String[] STORE_IMAGES = {
			MediaStore.Images.Media.DISPLAY_NAME, // 显示的名�?
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.LONGITUDE, // 经度
			MediaStore.Images.Media._ID, // id
			MediaStore.Images.Media.BUCKET_ID, // dir id 目录
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字
		   
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoalbum);
		aibumGV = (GridView) findViewById(R.id.album_gridview);
		aibumList = getPhotoAlbum();
		aibumGV.setAdapter(new PhotoAibumAdapter(aibumList, this));
		aibumGV.setOnItemClickListener(aibumClickListener);
	}

	/**
	 * 相册点击事件
	 */
	OnItemClickListener aibumClickListener =  new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
//			Intent intent = new Intent(PhotoAlbumActivity.this,
//					PhotoActivity.class);
//			intent.putExtra("aibum", aibumList.get(position));
//			startActivityForResult(intent, CreateInfoArticle.photoRequestCode);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		 super.onActivityResult(requestCode, resultCode, data);  
	     if (resultCode != Activity.RESULT_OK) 
	    	 return ;
	     switch (requestCode)
	     {
//			case CreateInfoArticle.photoRequestCode:
//				setResult(RESULT_OK, data);
//				PhotoAlbumActivity.this.finish();
//				break;
		 }
	}
	
	/**
	 * 方法描述：按相册获取图片信息
	 * 
	 * @author: why
	 * @time: 2013-10-18 下午1:35:24
	 */
	private List<PhotoAibum> getPhotoAlbum() {
		List<PhotoAibum> aibumList = new ArrayList<PhotoAibum>();
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		Map<String, PhotoAibum> countMap = new HashMap<String, PhotoAibum>();
		PhotoAibum pa = null;
		while (cursor.moveToNext()) {
			String path=cursor.getString(1);
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			Log.e("info", "id==="+id+"==dir_id=="+dir_id+"==dir=="+dir+"==path="+path);
			if (!countMap.containsKey(dir_id)) {
				pa = new PhotoAibum();
				pa.setName(dir);
				pa.setBitmap(Integer.parseInt(id));
				pa.setCount("1");
				pa.getBitList().add(new PhotoItem(Integer.valueOf(id),path));
				countMap.put(dir_id, pa);
			} else {
				pa = countMap.get(dir_id);
				pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
				pa.getBitList().add(new PhotoItem(Integer.valueOf(id),path));
			}
		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it) {
			aibumList.add(countMap.get(key));
		}
		return aibumList;
	}
}
