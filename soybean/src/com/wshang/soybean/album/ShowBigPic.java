package com.wshang.soybean.album;

import com.wshang.soybean.R;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ShowBigPic extends Activity{
	ImageView  iv_show_big_pic;
	String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_big_pic);
		iv_show_big_pic=(ImageView)findViewById(R.id.iv_show_big_pic);
		path=getIntent().getStringExtra("path");
		iv_show_big_pic.setImageBitmap(BitmapFactory.decodeFile(path));
	}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			finish();
			break;
		}
    	return super.onTouchEvent(event);
    }
}
