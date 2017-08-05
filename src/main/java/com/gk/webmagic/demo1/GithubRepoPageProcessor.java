package com.gk.webmagic.demo1;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author gk
 *
 * 2017��3��2��
 */
public class GithubRepoPageProcessor implements PageProcessor {
	
	//����һ��ץȡ��վ��������ã��������룬ץȡ��������Դ����ȵ�
	private Site site = Site.me().setRetryTimes(5).setSleepTime(1000);

	public Site getSite() {
		return site;
	}
	
	//process�����Ƕ��������߼��ĺ������
	public void process(Page page) {
		//���ֶ���������γ�ȡҳ����Ϣ�� ����������
		page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
		System.out.println("-----------------------"+page.getUrl().toString()+"---------------------------");
		System.out.println("-----------------------"+page.getUrl().regex("https://github\\.com/(\\w+)/.*")+"---------------------------");
		page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
		
		if(page.getResultItems().get("name") == null) {
			//�������ҳ��
			page.setSkip(true);
		}
		
		page.putField("readme",page.getHtml().xpath("//div[@id='readme']/tidyText()"));
		
		page.addTargetRequests(page.getHtml().links().regex("https://github\\.com/\\w+/\\w+").all());
	}

	public static void main(String[] args) {
		Spider.create(new GithubRepoPageProcessor())
				.addUrl("https://github.com/gkaigk1987")//�������url��ʼ��ȡ
				.thread(5)//����5���߳���ȡ
				.run();//����
	}
	
}
