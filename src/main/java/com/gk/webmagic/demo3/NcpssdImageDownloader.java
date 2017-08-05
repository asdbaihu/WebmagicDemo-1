package com.gk.webmagic.demo3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo3.bean.ImageBean;
import com.gk.webmagic.demo3.util.Util;

public class NcpssdImageDownloader {
	
	private static Logger logger = LoggerFactory.getLogger(NcpssdImageDownloader.class);
	
	private ConcurrentLinkedQueue<ImageBean> imageBeans = new ConcurrentLinkedQueue<>();
	
	private ReentrantLock lock = new ReentrantLock();
	
	private static final int downloadNum = 5;
	
	private int idleNum = 0;
	
//	private int countNum = 0;
	
	private NcpssdSpider spider;
	
	public NcpssdImageDownloader() {
		
	}
	
	public NcpssdImageDownloader(NcpssdSpider spider) {
		this.spider = spider;
	}
	
	public NcpssdSpider getSpider() {
		return spider;
	}

	public void setSpider(NcpssdSpider spider) {
		this.spider = spider;
	}

	public void addImageBean(ImageBean bean) {
		try {
			lock.lock();
			if(!imageBeans.contains(bean)){
				imageBeans.add(bean);
				logger.info("新增图片下载信息{}",JSONObject.toJSONString(bean));
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void downloadImage(ConcurrentLinkedQueue<ImageBean> beans) {
		if(beans.size() > 0) {
			for (int i = 0; i < beans.size(); i++) {
				ImageBean imageBean = beans.peek();
				int flag = downloadImage(imageBean.getUrl(),imageBean.getName(),imageBean.getPath());
				if(flag == 1) {
					beans.remove();
					logger.info("图片重新下载成功，名称为{}!",imageBean.getName());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param imageUrl
	 * @param name
	 * @param path
	 */
	public int downloadImage(String imageUrl,String name,String path) {
		if(StringUtils.isNotEmpty(imageUrl) && Util.isPic(imageUrl)) {
			String suffix = Util.getImageSuffix(imageUrl);
			Path target = Paths.get(path);
			if(!Files.isReadable(target)) {
				try {
					Files.createDirectory(target);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("创建目录出错！");
				}
			}
			File file = new File(path + File.separator + name + suffix);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			RequestConfig requestConfig  = RequestConfig.custom()
						.setSocketTimeout(30 * 1000)
						.setConnectTimeout(30 * 1000).build();
			try {
				HttpGet httpGet = new HttpGet(imageUrl);
				httpGet.setConfig(requestConfig);
				CloseableHttpResponse response = httpClient.execute(httpGet);
//				if(name.equals("3") && countNum == 0) {
//					countNum++;
//					throw new ConnectTimeoutException(); 
//				}
				HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    int len = 0;
                    byte[] tmp = new byte[1024];
                    while ((len = in.read(tmp)) != -1) {
                    	fos.write(tmp,0,len);
                    }
                    fos.flush();
                } finally {
                    in.close();
                    fos.close();
                }
                logger.info("下载图片成功，图片名称为{}",name + suffix);
                return 1;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (HttpHostConnectException e) {
				//连接超时
				ImageBean imageBean = new ImageBean(name,imageUrl,path);
				addImageBean(imageBean);
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (ConnectTimeoutException e) {
				//连接超时
				ImageBean imageBean = new ImageBean(name,imageUrl,path);
				addImageBean(imageBean);
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (IOException e) {
				
			} catch (Exception e) {
				//不符合规则的url
				logger.info("不符合规则的图片链接为{}",imageUrl);
			} finally {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public void retryImageDownload() {
		lock.lock();
		try {
			int size = imageBeans.size();
			logger.info("当前需重新下载的图片链接数为{}",size);
			if(size > 0) {
				if(size >= downloadNum || idleNum >= 5) {
					downloadImage(imageBeans);
//					imageBeans = new ConcurrentLinkedQueue<>();
					idleNum = 0;
				}else {
					idleNum++;
				}
				logger.info("图片下载当前自旋{}次",idleNum);
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void cycleDownloadImage() {
		Thread imageDownloadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("图片下载监控启动！");
				while(!spider.isFinish() || imageBeans.size() > 0) {
					retryImageDownload();
					try {
						Thread.sleep(5 * 1000l);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
				logger.info("图片下载监控停止！");
			}
		});
		imageDownloadThread.setName("ncpssd image download thread");
		imageDownloadThread.start();
	}

}
