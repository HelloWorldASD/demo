package com.wshang.soybean.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bimp {
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();
	public static List<String> drr = new ArrayList<String>();

	/**
	 * 商品描述图片保存
	 */
	public static int desc_max = 0;
	public static boolean desc_act_bool = true;
	public static List<Bitmap> desc_bmp = new ArrayList<Bitmap>();
	public static List<String> desc_drr = new ArrayList<String>();

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于200KB，失真度不明显
	public static Bitmap revitionImageSize(String path,int size) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true)
		{

			if (( options.outWidth >> i <= size)
					&& ( options.outHeight >> i <= size))

			{
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	/**
	 * 根据图片路径字段压缩图片
	 * 
	 * @param path 图片路径
	 * @param imgwidth
	 * @param picsize 图片大小 比如200k 200
	 * @return Ed:wanglongneng
	 */
	public static Bitmap revitionImage(String path, int imgwidth, int picsize)
			throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int height = options.outHeight;
		int width = options.outWidth;
		int reqHeight = 0;
		int reqWidth = imgwidth;// 300的宽
		reqHeight = ( reqWidth * height) / width;
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		Bitmap mybitmap = compressImage(
				Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false),
				picsize);
		bitmap.recycle();// lm add 20140621
		return mybitmap;
	}

	/**
	 * 根据图片路径字段压缩图片
	 * 
	 * @param path 图片路径
	 * @param imgwidth
	 * @param picsize 图片大小 比如200k 200
	 * @return Ed:wanglongneng
	 */
	public static Bitmap revitionImage(String path, int imgwidth,
			int imgheight, int picsize) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int height = options.outHeight;
		int width = options.outWidth;
		int reqHeight = imgheight;
		int reqWidth = imgwidth;// 300的宽
		// reqHeight = ( reqWidth * height) / width;
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		Bitmap mybitmap = compressImage(
				Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false),
				picsize);
		bitmap.recycle();// lm add 20140621
		return mybitmap;
	}

	/**
	 * 压缩图大小大小
	 * 
	 * @param path 图片路径
	 * @param imgwidth
	 * @param picsize 图片大小 比如200k 200
	 * @return ed:wanglongneng
	 */
	public static Bitmap revitionImageByBitMap(Bitmap path, int imgwidth,
			int picsize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		int height = path.getHeight();
		int width = path.getWidth();
		int reqHeight = 0;
		int reqWidth = imgwidth;// 300的宽
		reqHeight = ( reqWidth * height) / width;
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		Bitmap mybitmap = compressImage(
				Bitmap.createScaledBitmap(path, reqWidth, reqHeight, false),
				picsize);
		return mybitmap;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth)
		{
			if (width > height)
			{
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
			else
			{
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * 图片质量压缩
	 * 
	 * @param image
	 * @param picsize 图片大小 比如200K 200
	 * @return ED:WangLongneng
	 */
	public static Bitmap compressImage(Bitmap image, int picsize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > picsize)
		{ // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			options -= 10;// 每次都减少10
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}
