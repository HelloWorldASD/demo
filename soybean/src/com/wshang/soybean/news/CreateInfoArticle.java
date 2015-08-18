package com.wshang.soybean.news;
//package com.wshang.soybean.news;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//import org.json.JSONException;
//import org.json.JSONObject;
//import com.lidroid.xutils.HttpUtils;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.exception.HttpException;
//import com.lidroid.xutils.http.RequestParams;
//import com.lidroid.xutils.http.ResponseInfo;
//import com.lidroid.xutils.http.callback.RequestCallBack;
//import com.lidroid.xutils.http.client.HttpRequest;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import com.wshang.soybean.R;
//import com.wshang.soybean.album.PhotoAlbumActivity;
//import com.wshang.soybean.main.MyApplication;
//import com.wshang.tools.Bimp;
//import com.wshang.tools.CommonUtil;
//import com.wshang.tools.HuiConstants;
//import com.wshang.tools.LoadingProgress;
//import com.wshang.tools.ToastUtil;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.text.Editable;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.format.DateFormat;
//import android.text.style.ImageSpan;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//public class CreateInfoArticle extends Activity implements View.OnClickListener
//{
//	private static final int TAKE_PICTURE = 0;//拍照
//	
////	@ViewInject(R.id.txtRightTitle)
//	private TextView txtRightTitle;
////	@ViewInject(R.id.llyExpression)
//    private LinearLayout llyExpression;
////	@ViewInject(R.id.edtContent)
//	private EditText edtContent;
////	@ViewInject(R.id.gvPhotos)
//	private GridView gvPhotos;
//	private List<JSONObject> photoData;
//	private CreateInfoPhotoAdapter photoAdapter;
//	
//	private CreateInfoSourceAdapter sourceAdapter;
//	private List<JSONObject> sourceData;
//	private List<String> titles = null;
//	private List<String> urls = null;
//	
//	@ViewInject(R.id.lvSource)
//	private ListView lvSource;
//	
//	private final int cameraRequestCode = 1000;
//	private final int sourceRequestCode = 1001;
//	public final static int photoRequestCode = 1002;
//	private Dialog loading = null;
//
//
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.create_info_article);
////		ViewUtils.inject(this);
//		initView();
//		initData();
//	}
//	
//	private void initView()
//	{
//		txtRightTitle = (TextView) findViewById(R.id.txtRightTitle);
//		llyExpression = (LinearLayout) findViewById(R.id.llyExpression);
//		edtContent = (EditText) findViewById(R.id.edtContent);
//		gvPhotos = (GridView) findViewById(R.id.gvPhotos);
//	}
//	
//	private void initData()
//	{
//		txtRightTitle.setVisibility(View.VISIBLE);
//		txtRightTitle.setText("发布");
//		loading = new LoadingProgress(this, R.style.LoadDialog, "");
//		
//		photoData = new ArrayList<JSONObject>();
//		photoAdapter = new CreateInfoPhotoAdapter(this, photoData);
//		gvPhotos.setAdapter(photoAdapter);
//		
//		sourceData = new ArrayList<JSONObject>();
//		sourceAdapter = new CreateInfoSourceAdapter(this, sourceData);
//		
//		lvSource.setAdapter(sourceAdapter);
//	}
//
//	@Override
//	public void onClick(View v)
//	{
//		switch (v.getId())
//		{
//			case R.id.txtRightTitle:
//				create();
//				break;
//			case R.id.ivCamera://相机
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
//                startActivityForResult(intent, cameraRequestCode);
//				break;
//			case R.id.ivExpression://表情
//				if (llyExpression.getVisibility() == View.GONE)
//					llyExpression.setVisibility(View.VISIBLE);
//				else 
//					llyExpression.setVisibility(View.GONE);
//				break;
//			case R.id.ivPhoto://画册
//				Intent ittPhoto = new Intent(CreateInfoArticle.this, PhotoAlbumActivity.class);
//				startActivityForResult(ittPhoto, photoRequestCode);
//				break;
//			case R.id.ivSource://来源
//				Intent ittSource = new Intent(CreateInfoArticle.this, ContentSource.class);
//				startActivityForResult(ittSource, sourceRequestCode);
//				break;
//			case R.id.ivSmiley_0:
//			case R.id.ivSmiley_1:
//			case R.id.ivSmiley_2:
//			case R.id.ivSmiley_3:
//			case R.id.ivSmiley_4:
//			case R.id.ivSmiley_5:
//			case R.id.ivSmiley_6:
//			case R.id.ivSmiley_7:
//			case R.id.ivSmiley_8:
//			case R.id.ivSmiley_9:
//			case R.id.ivSmiley_10:
//			case R.id.ivSmiley_11:
//			case R.id.ivSmiley_12:
//			case R.id.ivSmiley_13:
//			case R.id.ivSmiley_14:
//			case R.id.ivSmiley_15:
//			case R.id.ivSmiley_16:
//			case R.id.ivSmiley_17:
//			case R.id.ivSmiley_18:
//			case R.id.ivSmiley_19:
//			case R.id.ivSmiley_20:
//			case R.id.ivSmiley_21:
//			case R.id.ivSmiley_22:
//			case R.id.ivSmiley_23:
//			case R.id.ivSmiley_24:
//			case R.id.ivSmiley_25:
//			case R.id.ivSmiley_26:
//			case R.id.ivSmiley_27:
//			case R.id.ivSmiley_28:
//				ImageView ivSimle = (ImageView) v;
//				String picName = (String) ivSimle.getContentDescription();
//				Field field = null;
//				try {
//					field = R.drawable.class.getDeclaredField(picName);
//				} catch (NoSuchFieldException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				int drawId = 0;
//				try {
//					drawId = field.getInt(null);
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Drawable drawable = CreateInfoArticle.this.getResources().getDrawable(drawId);
//				double zoom = 0.66;
//				int widht = (int) (drawable.getIntrinsicWidth() * zoom);
//				int height = (int) (drawable.getIntrinsicHeight() * zoom);
//				drawable.setBounds(0, 0, widht, height);
//				// 根据Bitmap对象创建ImageSpan对象
//				ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//				// 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
//				String source = "[" + picName + "]";
//				int length = 0;
//				try {
//					length = (edtContent.getText().toString() + source).getBytes("GBK").length;
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if (length > 300)
//					return;
//				SpannableString spannableString = new SpannableString(source);
//				// 用ImageSpan对象替换face
//				spannableString.setSpan(span, 0, source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//				// 将随机获得的图像插入到EditText控件的光标处
//				int index = edtContent.getSelectionStart();
//				// 插入图片
//				Editable eb = edtContent.getEditableText();
//				eb.insert(index, spannableString);
//				break;
//			case R.id.ivSmileyDelete:
//				Editable eb2 = edtContent.getEditableText();
//				String contentString  = edtContent.getText().toString();
//				if (contentString.substring(contentString.length()-1).equals("]")) 
//				{
//					String last11 = contentString.substring(contentString.length()-11);
//					int indexOfStat = contentString.length()-11+last11.lastIndexOf("[");
//					eb2.replace(indexOfStat, contentString.length(), "");
//				}
//				else 
//				{
//					eb2.replace((contentString.length()-1), contentString.length(), "");
//				}
//				break;
//			default:
//				break;
//		}
//	}
//	
//	private void create()
//	{
//		loading.show();
//		HttpUtils http = new HttpUtils();
//		RequestParams params = new RequestParams();
//		
//		params.addBodyParameter("uid", MyApplication.getMember().getMemberMap().get("user_id"));
//		params.addBodyParameter("token",  MyApplication.getMember().getMemberMap().get("token"));
//		params.addBodyParameter("content", edtContent.getText().toString());
//		if (titles != null)
//		{
//			for (int i = 0; i < titles.size(); i++) 
//			{
//				params.addBodyParameter("title"  + String.valueOf(i + 1), titles.get(i));
//				params.addBodyParameter("url" + String.valueOf(i + 1), urls.get(i));
//	        }
//		}
//		
//		if (photoData != null)
//		{
//			File file = null;
//			int count = photoData.size();
//			for (int i = 0; i < count; i++)
//			{
//				JSONObject item = photoData.get(i);
//				try {
//					file = new File(item.getString("path"));
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				params.addBodyParameter("file" + String.valueOf(i + 1) , file);
//	        }
//		}
//		
//
//		http.send(HttpRequest.HttpMethod.POST,
//				HuiConstants.PREFIX + "/post/create",
//				params,
//		    new RequestCallBack<String>(){
//		        @Override
//		        public void onLoading(long total, long current, boolean isUploading) {
//		        	
//		        }
//
//		        @Override
//		        public void onSuccess(ResponseInfo<String> responseInfo) {
//		        	try 
//                    {
//		        		loading.dismiss();
//		        		JSONObject result = new JSONObject(responseInfo.result);
//		        		
//		        		boolean success = result.getBoolean("success");
//		        		if (success == false)
//		        		{
//		        			ToastUtil.makeFailToastThr(CreateInfoArticle.this, result.getString("msg"));
//		        			int code = result.getInt("code");
//		        			if (code == -1)
//		        			{
//		        				CommonUtil.openLogin(CreateInfoArticle.this, true);
//		        			}
//		        			return ;
//		        		}
//		        		else
//		        			CreateInfoArticle.this.finish();
//					} 
//                    catch (JSONException e)
//					{
//						
//					}
//		        }
//
//		        @Override
//		        public void onStart() {
//		        }
//
//		        @Override
//		        public void onFailure(HttpException error, String msg) {
//		        	loading.dismiss();
//		        	ToastUtil.makeFailToastThr(CreateInfoArticle.this, CreateInfoArticle.this.getResources().getString(R.string.network_fail));
//		        }
//		});
//	}
//	
//	private void getCamera(Intent data)
//	{
//		String sdStatus = Environment.getExternalStorageState();  
//        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
//            return;  
//        }  
//        
//        String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";   
//        
//        Bundle bundle = data.getExtras();  
//        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
//        FileOutputStream b = null;           
//        File file = new File(Environment.getExternalStorageDirectory() 
//        		+ "/sdcard/image/");  
//        file.mkdirs();// 创建文件夹  
//        String fileName = Environment.getExternalStorageDirectory() + "/sdcard/image/" +name;  
//
//        try {  
//            b = new FileOutputStream(fileName);  
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件  
//        } catch (FileNotFoundException e) {  
//            e.printStackTrace();  
//        } finally {  
//            try {  
//                b.flush();  
//                b.close();  
//                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri uri = Uri.fromFile(file);
//                intent.setData(uri);
//                sendBroadcast(intent);
//            } catch (IOException e) {  
//                e.printStackTrace();  
//            }  
//        } 
//        
//        try  
//        {  
//        	bitmap = Bimp.compressImage(bitmap, 100);
//        	JSONObject item = new JSONObject();
//        	item.put("photo", bitmap);
//        	item.put("path", fileName);
//        	List<JSONObject> addDatas = new ArrayList<JSONObject>();
//        	addDatas.add(item);
//        	photoAdapter.setData(addDatas);
//        	photoAdapter.notifyDataSetChanged();
//        }catch(Exception e)  
//        {  
//            Log.e("error", e.getMessage());  
//        }
//	}
//	
//	private void getSourceData(Intent data)
//	{
//		titles = data.getStringArrayListExtra("titles");
//		urls = data.getStringArrayListExtra("urls");
//		sourceData.clear();
//		if (titles != null)
//		{
//			JSONObject item = null;
//			for (int i = 0; i < titles.size(); i++)
//			{
//				item = new JSONObject();
//				try {
//					item.put("title", titles.get(i));
//					item.put("url", urls.get(i));
//					sourceData.add(item);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			sourceAdapter.notifyDataSetChanged();
//		}
//	}
//	
//	private void getPhotoData(Intent data)
//	{
//		ArrayList<String> pathList = data.getStringArrayListExtra("paths");
//		
//		if (pathList != null)
//		{
//			JSONObject item = null;
//			for (int i = 0; i < pathList.size(); i++)
//			{
//				item = new JSONObject();
//				String path = null;
//				try {
//					path = pathList.get(i);
//					Bitmap photo = Bimp.revitionImageSize(path, 100);
//					item.put("photo", photo);
//					item.put("path", pathList.get(i));
//					photoData.add(item);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			photoAdapter.notifyDataSetChanged();
//		}
//	}
//	
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
//	{
//		 super.onActivityResult(requestCode, resultCode, data);  
//	     if (resultCode != Activity.RESULT_OK) 
//	    	 return ;
//	     switch (requestCode)
//	     {
//			case cameraRequestCode:
//				getCamera(data);
//				break;
//			case sourceRequestCode:
//				getSourceData(data);
//				break;
//			case photoRequestCode:
//				getPhotoData(data);
//				break;
//			default:
//				break;
//		 }
//	}
//}
