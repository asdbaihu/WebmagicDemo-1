package com.gk.webmagic.test;

import java.io.File;

import org.junit.Test;

import com.gk.webmagic.demo3.util.Util;

public class OSSClientTest {
	
	@Test
	public void test01() {
		Util.uploadFile2OSS(new File("E:/1.png"), "ncpssd", "2.png");
	}
	
	@Test
	public void test02() {
		boolean flag = Util.checkFileExist("ncpssd", "2.png");
		System.out.println(flag);
	}
	
	@Test
	public void test03() {
		Util.deleteFileFromOSS("ncpssd", "2.png");
	}

	@Test
	public void test04() {
		String dir = "F:/ncpssd20170314_bak";
//		Util.batchUpload(dir, "ncpssd");
	}
	
	@Test
	public void test05() {
		String path = "F:/uniDownload";
		String filename = "30_北京工业大学.jpg";
		Util.download(path, filename);
	}
}
