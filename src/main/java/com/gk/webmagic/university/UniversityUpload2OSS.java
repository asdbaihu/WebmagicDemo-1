package com.gk.webmagic.university;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.gk.webmagic.demo3.constant.OSSConstants;

public class UniversityUpload2OSS {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String directory = "F:/university";
	private static final String uploadDirectory = "universitySymbol";
	private LinkedBlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
	private ReentrantLock lock = new ReentrantLock();
	private boolean stopFlag = false;
	private OSSClient ossClient = null;
	private int idleNum = 0;

	public UniversityUpload2OSS() {
		this.ossClient = new OSSClient(OSSConstants.OSS_ENDPOINT, OSSConstants.OSS_ACCESS_ID,
				OSSConstants.OSS_ACCESS_KEY);
	}

	private void addExceptionFile(File file) {
		lock.lock();
		try {
			if (!fileQueue.contains(file)) {
				fileQueue.add(file);
			}
		} finally {
			lock.unlock();
		}
	}

	private boolean retryUpload2OSS(File file) {
		lock.lock();
		try {
			OSSClient ossClient = this.getOssClient();
			ossClient.putObject(OSSConstants.OSS_BUCKET_NAME, uploadDirectory + "/" + file.getName(), file);
			return true;
		} catch (ClientException e) {
			// 将出现上传异常的文件加入到LinkedBlokingQueue中
			addExceptionFile(file);
			logger.error("上传文件出现异常！文件名为{}", file.getName());
			e.printStackTrace();
		} catch (OSSException e) {
			// 将出现上传异常的文件加入到LinkedBlokingQueue中
			addExceptionFile(file);
			logger.error("上传文件出现异常！文件名为{}", file.getName());
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
			lock.unlock();
		}
		return false;
	}
	
	private void dealFileQueue(LinkedBlockingQueue<File> files) {
		if(files.size() > 0) {
			File file = files.peek();
			boolean flag = retryUpload2OSS(file);
			if(flag) {
				files.remove();
				logger.info("文件{}重新上传成功!",file.getName());
			}
		}
	}

	private void checkUpload() {
		lock.lock();
		try {
			int size = fileQueue.size();
			if(size > 0) {
				if(fileQueue.size() >= 5 || idleNum >= 5) {
					dealFileQueue(fileQueue);
					idleNum = 0;
				}else {
					idleNum++;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public void uploadMonitor() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("上传文件监控启动!");
				while (!stopFlag || fileQueue.size() > 0) {
					checkUpload();
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						//
					}
				}
				logger.info("上传文件监控停止!");
			}
		});
		t.setName("upload-file-monitor");
		t.start();
	}

	public void batchUpload(OSSClient ossClient, String directory, String uploadPath) {
		File dir = new File(directory);
		File exceptionFile = null;
		try {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					exceptionFile = file;
					String name = file.getName();
					ossClient.putObject(OSSConstants.OSS_BUCKET_NAME, uploadPath + "/" + name, file);
					logger.info("批量上传文件成功，当前上传的文件是{}",file.getName());
				}
			}
		} catch (ClientException e) {
			// 将出现上传异常的文件加入到LinkedBlokingQueue中
			if (null != exceptionFile) {
				addExceptionFile(exceptionFile);
			}
			logger.error("上传文件出现异常！文件名为{}", exceptionFile.getName());
			e.printStackTrace();
		} catch (OSSException e) {
			// 将出现上传异常的文件加入到LinkedBlokingQueue中
			if (null != exceptionFile) {
				addExceptionFile(exceptionFile);
			}
			logger.error("上传文件出现异常！文件名为{}", exceptionFile.getName());
			e.printStackTrace();
		} finally {
			if (ossClient != null) {
				ossClient.shutdown();
			}
		}
	}

	public boolean isStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}

	public OSSClient getOssClient() {
		return ossClient;
	}

	public void setOssClient(OSSClient ossClient) {
		this.ossClient = ossClient;
	}

	public static void main(String[] args) {
		UniversityUpload2OSS universityUpload2OSS = new UniversityUpload2OSS();
		universityUpload2OSS.uploadMonitor();
		universityUpload2OSS.batchUpload(universityUpload2OSS.getOssClient(), directory, uploadDirectory);
		universityUpload2OSS.setStopFlag(true);// 上传文件停止
	}

}
