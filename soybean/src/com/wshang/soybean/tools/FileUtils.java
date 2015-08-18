package com.wshang.soybean.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * 
 * 项目名称：工具类
 * 
 * 类名称：FileUtils
 * 
 * 类描述： 文件工具类，可用于读写文件及对文件进行操作
 * 
 * 创建人：梁鹏
 * 
 * 创建时间：2014-3-11 下午2:16:01
 * 
 * 修改人：梁鹏
 * 
 * 修改时间：2014-3-11 下午2:16:01
 * 
 * 修改备注：
 * 
 * 修改人：cbj
 * 
 * 修改时间：2014-3-21
 * 
 * 修改备注：添加照片预览缓存应用文件即删除
 * 
 * @version
 * 
 */
public class FileUtils
{

	public final static String	FILE_EXTENSION_SEPARATOR	= ".";

	/**
	 * 读取文件
	 * 
	 * @param 文件路径
	 * @param 名称支持字符集
	 * @return 如果不存在的文件，返回文件的空值，否则返回内容
	 * @throws 如果发生错误
	 *             ，抛出BufferedReader
	 */
	public static StringBuilder readFile(String filePath, String charsetName)
	{
		File file = new File(filePath);
		StringBuilder fileContent = new StringBuilder("");
		if (file == null || !file.isFile())
		{
			return null;
		}

		BufferedReader reader = null;
		try
		{
			InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
			reader = new BufferedReader(is);
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				if (!fileContent.toString().equals(""))
				{
					fileContent.append("\r\n");
				}
				fileContent.append(line);
			}
			reader.close();
			return fileContent;
		}
		catch (IOException e)
		{
			throw new RuntimeException("IOException occurred. ", e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * 写入文件
	 * 
	 * @param 文件路径
	 * @param 内容
	 * @param 追加
	 *            -被追加，如果是真的，写文件的末尾，否则文件中明确的内容，写进去
	 * @return 返回true
	 * @throws RuntimeException
	 *             if an error occurs while operator FileWriter
	 */
	public static boolean writeFile(String filePath, String content, boolean append)
	{
		FileWriter fileWriter = null;
		try
		{
			makeDirs(filePath);
			fileWriter = new FileWriter(filePath, append);
			fileWriter.write(content);
			fileWriter.close();
			return true;
		}
		catch (IOException e)
		{
			throw new RuntimeException("IOException occurred. ", e);
		}
		finally
		{
			if (fileWriter != null)
			{
				try
				{
					fileWriter.close();
				}
				catch (IOException e)
				{
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * 写入文件
	 * 
	 * @param 文件路径
	 *            -
	 * @param 流
	 * @return
	 * @see {@link #writeFile(String, InputStream, boolean)}
	 */
	public static boolean writeFile(String filePath, InputStream stream)
	{
		return writeFile(filePath, stream, false);
	}

	/**
	 * 写入文件
	 * 
	 * @param 文件
	 *            -要打开用于写入的文件。
	 * @param 流
	 *            -输入流
	 * @param 追加
	 *            -如果属实，那么字节将被写入到文件的末尾而不是开头
	 * @return 返回true
	 * @throws RuntimeException
	 *             if an error occurs while operator FileOutputStream
	 */
	public static boolean writeFile(String filePath, InputStream stream, boolean append)
	{
		return writeFile(filePath != null ? new File(filePath) : null, stream, append);
	}

	/**
	 * 写入文件
	 * 
	 * @param 文件
	 *            -
	 * @param 流
	 *            -
	 * @return
	 * @see {@link #writeFile(File, InputStream, boolean)}
	 */
	public static boolean writeFile(File file, InputStream stream)
	{
		return writeFile(file, stream, false);
	}

	/**
	 * 写入文件
	 * 
	 * @param 文件
	 *            -要打开用于写入的文件。
	 * @param 流
	 *            -输入流
	 * @param 追加
	 *            -如果属实，那么字节将被写入到文件的末尾而不是开头
	 * @return 返回true
	 * @throws RuntimeException
	 *             if an error occurs while operator FileOutputStream
	 */
	public static boolean writeFile(File file, InputStream stream, boolean append)
	{
		OutputStream o = null;
		try
		{
			makeDirs(file.getAbsolutePath());
			o = new FileOutputStream(file, append);
			byte data[] = new byte[1024];
			int length = -1;
			while ((length = stream.read(data)) != -1)
			{
				o.write(data, 0, length);
			}
			o.flush();
			return true;
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException("FileNotFoundException occurred. ", e);
		}
		catch (IOException e)
		{
			throw new RuntimeException("IOException occurred. ", e);
		}
		finally
		{
			if (o != null)
			{
				try
				{
					o.close();
					stream.close();
				}
				catch (IOException e)
				{
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFilePath
	 *            原文件路径
	 * @param destFilePath
	 *            目的文件路径
	 * 
	 * @return
	 * @throws RuntimeException
	 *             if an error occurs while operator FileOutputStream
	 */
	public static boolean copyFile(String sourceFilePath, String destFilePath)
	{
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(sourceFilePath);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException("FileNotFoundException occurred. ", e);
		}
		return writeFile(destFilePath, inputStream);
	}

	/**
	 * 读取文件到字符串列表，列表的元素是一条线
	 * 
	 * @param 文件路径
	 *            -
	 * @param charsetName
	 *            -名称支持字符集
	 * @return 如果不存在的文件，返回文件的空值，否则返回内容
	 * @throws RuntimeException
	 *             if an error occurs while operator BufferedReader
	 */
	public static List<String> readFileToList(String filePath, String charsetName)
	{
		File file = new File(filePath);
		List<String> fileContent = new ArrayList<String>();
		if (file == null || !file.isFile())
		{
			return null;
		}

		BufferedReader reader = null;
		try
		{
			InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
			reader = new BufferedReader(is);
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				fileContent.add(line);
			}
			reader.close();
			return fileContent;
		}
		catch (IOException e)
		{
			throw new RuntimeException("IOException occurred. ", e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * 从路径获取文件名，不包括后缀
	 * 
	 * <pre>
	 *      getFileNameWithoutExtension(null)               =   null
	 *      getFileNameWithoutExtension("")                 =   ""
	 *      getFileNameWithoutExtension("   ")              =   "   "
	 *      getFileNameWithoutExtension("abc")              =   "abc"
	 *      getFileNameWithoutExtension("a.mp3")            =   "a"
	 *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
	 *      getFileNameWithoutExtension("c:\\")              =   ""
	 *      getFileNameWithoutExtension("c:\\a")             =   "a"
	 *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
	 *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
	 *      getFileNameWithoutExtension("/home/admin")      =   "admin"
	 *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
	 * </pre>
	 * 
	 * @param 文件路径
	 *            -
	 * @return 从路径文件名，不包括后缀
	 * @see
	 */
	public static String getFileNameWithoutExtension(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
		{
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (filePosi == -1)
		{
			return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
		}
		if (extenPosi == -1)
		{
			return filePath.substring(filePosi + 1);
		}
		return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath
				.substring(filePosi + 1));
	}

	/**
	 * 从路径获取文件名，包括后缀
	 * 
	 * <pre>
	 *      getFileName(null)               =   null
	 *      getFileName("")                 =   ""
	 *      getFileName("   ")              =   "   "
	 *      getFileName("a.mp3")            =   "a.mp3"
	 *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
	 *      getFileName("abc")              =   "abc"
	 *      getFileName("c:\\")              =   ""
	 *      getFileName("c:\\a")             =   "a"
	 *      getFileName("c:\\a.b")           =   "a.b"
	 *      getFileName("c:a.txt\\a")        =   "a"
	 *      getFileName("/home/admin")      =   "admin"
	 *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
	 * </pre>
	 * 
	 * @param 文件路径
	 *            -
	 * @return 从路径文件名，包括后缀
	 */
	public static String getFileName(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
		{
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
	}

	/**
	 * 从路径获取文件夹名称
	 * 
	 * <pre>
	 *      getFolderName(null)               =   null
	 *      getFolderName("")                 =   ""
	 *      getFolderName("   ")              =   ""
	 *      getFolderName("a.mp3")            =   ""
	 *      getFolderName("a.b.rmvb")         =   ""
	 *      getFolderName("abc")              =   ""
	 *      getFolderName("c:\\")              =   "c:"
	 *      getFolderName("c:\\a")             =   "c:"
	 *      getFolderName("c:\\a.b")           =   "c:"
	 *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
	 *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
	 *      getFolderName("/home/admin")      =   "/home"
	 *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
	 * </pre>
	 * 
	 * @param 文件路径
	 *            -
	 * @return
	 */
	public static String getFolderName(String filePath)
	{

		if (StringUtil.isEmpty(filePath))
		{
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
	}

	/**
	 * 获取文件的后缀从路径
	 * 
	 * <pre>
	 *      getFileExtension(null)               =   ""
	 *      getFileExtension("")                 =   ""
	 *      getFileExtension("   ")              =   "   "
	 *      getFileExtension("a.mp3")            =   "mp3"
	 *      getFileExtension("a.b.rmvb")         =   "rmvb"
	 *      getFileExtension("abc")              =   ""
	 *      getFileExtension("c:\\")              =   ""
	 *      getFileExtension("c:\\a")             =   ""
	 *      getFileExtension("c:\\a.b")           =   "b"
	 *      getFileExtension("c:a.txt\\a")        =   ""
	 *      getFileExtension("/home/admin")      =   ""
	 *      getFileExtension("/home/admin/a.txt/b")  =   ""
	 *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileExtension(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
		{
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (extenPosi == -1)
		{
			return "";
		}
		return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
	}

	/**
	 * 创建此文件的文件名结尾，包括创建该目录所需的完整目录路径的目录。 <br/>
	 * <ul>
	 * <strong>注意事项：</strong>
	 * <li>makeDirs("C:\\Users\\Tools") 只能创建的用户文件夹</li>
	 * <li>makeFolder("C:\\Users\\Tools\\") 可以创建Tools文件夹</li>
	 * </ul>
	 * 
	 * @param 文件路径
	 *            -
	 * @return 如果必要的目录已创建或目标目录已经存在，目录假的不能创建。
	 *         <ul>
	 *         <li>如果getFolderName（字符串）返回null，返回false</li>
	 *         <li>如果目标目录已经存在，则返回true</li>
	 *         </ul>
	 */
	public static boolean makeDirs(String filePath)
	{
		String folderName = getFolderName(filePath);
		if (StringUtil.isEmpty(folderName))
		{
			return false;
		}

		File folder = new File(folderName);
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	/**
	 * @param 文件路径
	 *            -
	 * @return
	 * @see #makeDirs(String)
	 */
	public static boolean makeFolders(String filePath)
	{
		return makeDirs(filePath);
	}

	/**
	 * 指示此文件代表了底层文件系统上的文件。
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
		{
			return false;
		}

		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	/**
	 * 此文件代表了底层文件系统上的目录。
	 * 
	 * @param directoryPath
	 * @return
	 */
	public static boolean isFolderExist(String directoryPath)
	{
		if (StringUtil.isEmpty(directoryPath))
		{
			return false;
		}

		File dire = new File(directoryPath);
		return (dire.exists() && dire.isDirectory());
	}

	/**
	 * 删除文件或目录
	 * <ul>
	 * <li>如果path为null或为空，则返回true</li>
	 * <li>如果不存在的路径，返回true</li>
	 * <li>如果路径存在，删除递归。返回true</li>
	 * <ul>
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path)
	{
		if (StringUtil.isEmpty(path))
		{
			return true;
		}

		File file = new File(path);
		if (!file.exists())
		{
			return true;
		}
		if (file.isFile())
		{
			return file.delete();
		}
		if (!file.isDirectory())
		{
			return false;
		}
		for (File f : file.listFiles())
		{
			if (f.isFile())
			{
				f.delete();
			}
			else if (f.isDirectory())
			{
				deleteFile(f.getAbsolutePath());
			}
		}
		return file.delete();
	}

	/**
	 * 获取文件大小
	 * <ul>
	 * <li>如果path为null或为空，则返回-1</li>
	 * <li>如果路径存在，它是一个文件，返回文件的大小，否则返回-1</li>
	 * <ul>
	 * 
	 * @param 路径
	 *            -
	 * @return 返回以字节为单位这个文件的长度。返回-1，如果文件不存在。
	 */
	public static long getFileSize(String path)
	{
		if (StringUtil.isEmpty(path))
		{
			return -1;
		}

		File file = new File(path);
		return (file.exists() && file.isFile() ? file.length() : -1);
	}

	/***
	 * @note 创建图片缓存等操作
	 * @add cbj
	 */
	public static String	SDPATH	= Environment.getExternalStorageDirectory() + "/formats/";

	public static void saveBitmap(Bitmap bm, String picName)
	{

		File file = null;
		makeRootDirectory(SDPATH);
		file = new File(SDPATH, picName);

		if (file.exists())
		{
			file.delete();
		}
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try
		{
			out.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param bm
	 * @param picName
	 */
	public static void saveStoreBitmap(Bitmap bm, String picName)
	{
		String sd=	Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera/";
		File file = null;
		makeRootDirectory(sd);
		file = new File(sd, picName);

		if (file.exists())
		{
			file.delete();
		}
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try
		{
			out.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void makeRootDirectory(String filePath)
	{
		File file = null;
		try
		{
			file = new File(filePath);
			if (!file.exists())
			{
				file.mkdir();
			}
		}
		catch (Exception e)
		{

		}
	}

	public static File createSDDir(String dirName) throws IOException
	{
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist_byName(String fileName)
	{
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}

	public static void delFile(String fileName)
	{
		File file = new File(SDPATH + fileName);
		if (file.isFile())
		{
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir()
	{
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles())
		{
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path)
	{
		try
		{
			File f = new File(path);
			if (!f.exists())
			{
				return false;
			}
		}
		catch (Exception e)
		{

			return false;
		}
		return true;
	}

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard()
	{
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
