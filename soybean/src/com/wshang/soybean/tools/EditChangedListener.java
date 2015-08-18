package com.wshang.soybean.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;


public class EditChangedListener implements TextWatcher {  
    @Override  
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
    }  

    @Override  
    public void onTextChanged(CharSequence s, int start, int before, int count) {  
    	String content = s.toString();
    	char[]chars = content.toCharArray(); //把字符中转换为字符数组 

    	  System.out.println("\n\n汉字 ASCII\n----------------------");
    	  for(int i=0;i<chars.length;i++){//输出结果
    		  Log.e("ASCII", " "+chars[i]+" ="+(int)chars[i]);
    	  }
    }  

    @Override  
    public void afterTextChanged(Editable s)
    {  
    }
}; 
