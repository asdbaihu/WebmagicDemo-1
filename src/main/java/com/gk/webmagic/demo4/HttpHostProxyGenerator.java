package com.gk.webmagic.demo4;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHostProxyGenerator {
	
	private static Logger logger = LoggerFactory.getLogger(HttpHostProxyGenerator.class);
	public static final String proxy_request_url = "http://s.zdaye.com/?api=201612281102424875&adr=%BD%AD%CB%D5%2C%D5%E3%BD%AD%2C%C9%CF%BA%A3&fitter=2&px=1";
	public static final String api = "201612281102424875";
	
	private List<HttpHost> hosts = new ArrayList<>();
	
	private ReentrantLock lock = new ReentrantLock();
	
	public HttpHostProxyGenerator() {
		generateHosts();
	}
	
	public List<HttpHost> getHosts() {
		return hosts;
	}
	
	@SuppressWarnings("unchecked")
	private void generateHosts() {
		lock.lock();
		InputStream is = null;
		try {
			is = new URL(proxy_request_url).openConnection().getInputStream();
			List<String> hostList = IOUtils.readLines(is);
			if(null != hostList && hostList.size() > 0) {
				hosts = hostList.stream().map(host -> {
					String[] hostStr = host.split(":");
					if(hostStr.length > 1) {
						Integer port = new Integer(hostStr[1]);
						HttpHost httpHost = new HttpHost(hostStr[0], port, "http");
						return httpHost;
					}
					return null;
				}).filter(h -> null != h).collect(Collectors.toList());
			}
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
			lock.unlock();
		}
	}
	
	/**
	 * 获取HttpHost
	 * @return
	 */
	public HttpHost getProxy() {
		lock.lock();
		try {
			int size = hosts.size();
			if(size > 0) {
				int i = new Random().nextInt(size);
				HttpHost host = hosts.get(i);
				logger.info("获取代理信息,IP:{},PORT:{}",host.getHostName(),host.getPort());
				return host;
			}
		} finally {
			lock.unlock();
		}
		logger.warn("未获取到代理信息！！");
		return null;
	}
}
