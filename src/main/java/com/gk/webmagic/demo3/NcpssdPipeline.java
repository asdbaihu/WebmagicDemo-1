package com.gk.webmagic.demo3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gk.webmagic.demo3.model.NcpssdBeanWithBLOBs;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class NcpssdPipeline implements Pipeline {
	
	private static Logger logger = LoggerFactory.getLogger(NcpssdPipeline.class);
	
	private NcpssdSpider spiderTask;
	
	public NcpssdPipeline() {
		
	}
	
	public NcpssdPipeline(NcpssdSpider task) {
		this.spiderTask = task;
	}
	
	public void process(ResultItems resultItems, Task task) {
		NcpssdBeanWithBLOBs ncpssdDetail = resultItems.get("ncpssdDetail");
		if(null != ncpssdDetail) {
			spiderTask.addNcpssdBean(ncpssdDetail);
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(getImageSuffix("/images/bookb5.png"));
		//http://103.247.176.188/cover/7831/10034196/1.jpg
//		System.out.println(isPic("http://103.247.176.188/cover/7831/10034196/1.jpg"));
	}
}
