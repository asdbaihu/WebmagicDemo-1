package com.gk.webmagic.demo5;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;

public class MyPage extends Page {
	
	private Selectable url;
	
	private List<Request> targetRequests = new ArrayList<Request>();

	@Override
	public void addTargetRequests(List<String> requests) {
		synchronized (targetRequests) {
            for (String s : requests) {
                if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                    continue;
                }
                s = UrlUtils.canonicalizeUrl(s, url.toString());
                Request request = new Request(s);
                request.setMethod(HttpConstant.Method.POST);
                targetRequests.add(request);
            }
        }
	}
	
	

}
