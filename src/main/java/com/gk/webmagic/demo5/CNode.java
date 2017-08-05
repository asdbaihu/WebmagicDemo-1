package com.gk.webmagic.demo5;

import com.alibaba.fastjson.annotation.JSONField;

public class CNode {

	@JSONField(name="PCode")
	private String PCode;
	
	@JSONField(name="SMode")
	private String SMode;
	
	@JSONField(name="OperateT")
	private String OperateT;

	public String getPCode() {
		return PCode;
	}

	public void setPCode(String pCode) {
		PCode = pCode;
	}

	public String getSMode() {
		return SMode;
	}

	public void setSMode(String sMode) {
		SMode = sMode;
	}

	public String getOperateT() {
		return OperateT;
	}

	public void setOperateT(String operateT) {
		OperateT = operateT;
	}
	
}
