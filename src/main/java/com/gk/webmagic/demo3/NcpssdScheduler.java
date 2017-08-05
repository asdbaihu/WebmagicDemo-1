package com.gk.webmagic.demo3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.annotation.ThreadSafe;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

/**
 * 不进行url重复判定
 * @author gk
 *
 * 2017年3月13日
 */
@ThreadSafe
public class NcpssdScheduler extends QueueScheduler {
	
	private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

	@Override
	public void push(Request request, Task task) {
		logger.trace("get a candidate url {}", request.getUrl());
        logger.debug("push to queue {}", request.getUrl());
        pushWhenNoDuplicate(request, task);
	}
	
	@Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        queue.add(request);
    }

    @Override
    public synchronized Request poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
	
	

}
