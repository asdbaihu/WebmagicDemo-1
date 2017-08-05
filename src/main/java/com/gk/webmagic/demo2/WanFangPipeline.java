package com.gk.webmagic.demo2;

import java.util.List;

import com.gk.webmagic.demo2.bean.WanFangJournalBean;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class WanFangPipeline implements Pipeline {
	
	private WanFangTask wanFangTask;
	
	public WanFangPipeline() {
		
	}
	
	public WanFangPipeline(WanFangTask wanFangTask) {
		this.wanFangTask = wanFangTask;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		List<WanFangJournalBean> list = resultItems.get("wfBean");
		if(null != list && list.size() > 0) {
			wanFangTask.addWanFangJournalInfo(list);
		}
	}

}
