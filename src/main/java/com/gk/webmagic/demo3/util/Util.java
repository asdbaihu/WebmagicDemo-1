package com.gk.webmagic.demo3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.gk.webmagic.demo3.constant.OSSConstants;

public class Util {
	
	private static Logger logger = LoggerFactory.getLogger(Util.class);
	
	/**
	 * 获取文件流
	 * @param f
	 * @return
	 */
	public static InputStream getFileInputStream(String f) {
		File file = new File(f);
		return getFileInputStream(file);
	}
	
	/**
	 * 获取文件流
	 * @param f
	 * @return
	 */
	public static InputStream getFileInputStream(File file) {
		if(file.exists() && file.isFile()) {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				return is;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(null != is) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public static void uploadFile2OSS(InputStream is,String path,String fileName) {
		OSSClient ossClient = new OSSClient(OSSConstants.OSS_ENDPOINT, OSSConstants.OSS_ACCESS_ID, OSSConstants.OSS_ACCESS_KEY);
		try {
			ossClient.putObject(OSSConstants.OSS_BUCKET_NAME, path+File.separator+fileName, is);
		} catch (Exception e) {
			// ignore
		} finally {
			ossClient.shutdown();
		}
	}
	
	public static void uploadFile2OSS(File file,String path,String fileName) {
		OSSClient ossClient = new OSSClient(OSSConstants.OSS_ENDPOINT, OSSConstants.OSS_ACCESS_ID, OSSConstants.OSS_ACCESS_KEY);
		try {
			ossClient.putObject(OSSConstants.OSS_BUCKET_NAME, path+"/"+fileName, file);
		} catch (Exception e) {
			// ignore
		} finally {
			ossClient.shutdown();
		}
	}
	
	public static boolean checkFileExist(String path,String fileName) {
		OSSClient ossClient = new OSSClient(OSSConstants.OSS_ENDPOINT, OSSConstants.OSS_ACCESS_ID, OSSConstants.OSS_ACCESS_KEY);
		try {
			return ossClient.doesObjectExist(OSSConstants.OSS_BUCKET_NAME, path+"/"+fileName);
		} catch (Exception e) {
			// ignore
		} finally {
			ossClient.shutdown();
		}
		return false;
	}
	
	public static void deleteFileFromOSS(String path,String fileName) {
		OSSClient ossClient = new OSSClient(OSSConstants.OSS_ENDPOINT, OSSConstants.OSS_ACCESS_ID, OSSConstants.OSS_ACCESS_KEY);
		try {
			ossClient.deleteObject(OSSConstants.OSS_BUCKET_NAME, path+"/"+fileName);
		} catch (Exception e) {
			// ignore
		} finally {
			ossClient.shutdown();
		}
	}
	
	public static void download(String path,String fileName) {
		OSSClient ossClient = new OSSClient(OSSConstants.OSS_ENDPOINT, OSSConstants.OSS_ACCESS_ID, OSSConstants.OSS_ACCESS_KEY);
		try {
			ossClient.getObject(new GetObjectRequest(OSSConstants.OSS_BUCKET_NAME, "universitySymbol/"+fileName), new File(path+"/"+fileName));
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			ossClient.shutdown();
		}
	}
	
	public static void batchUpload(OSSClient ossClient,String directory,String uploadPath) {
		File dir = new File(directory);
		try {
			if(dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					String name = file.getName();
					ossClient.putObject(OSSConstants.OSS_BUCKET_NAME, uploadPath+"/"+name, file);
				}
			}
		} catch (Exception e) {
			//抛出异常，不在此处处理
			throw e;
		}  finally {
			ossClient.shutdown();
		}
	}
	
	/**
	 * 判断是否是图片
	 * @param imageUrl
	 * @return
	 */
	public static boolean isPic(String imageUrl) {
		String urlRegex = "\\S+\\.(jpg|bmp|gif|jpeg|png)$"; 
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(imageUrl);
		if(matcher.find()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取图片后缀名
	 * @param imageUrl
	 * @return
	 */
	public static String getImageSuffix(String imageUrl) {
		if(StringUtils.isNotEmpty(imageUrl)) {
			int index = imageUrl.lastIndexOf(".");
			return imageUrl.substring(index);
		}
		return "";
	}
	
}
