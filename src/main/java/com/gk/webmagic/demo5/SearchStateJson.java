package com.gk.webmagic.demo5;

import com.alibaba.fastjson.annotation.JSONField;

public class SearchStateJson {
	
	@JSONField(name="StateID")
	private String StateID;
	
	@JSONField(name="Platfrom")
	private String Platfrom;
	
	@JSONField(name="QueryTime")
	private String QueryTime;
	
	@JSONField(name="Platfrom")
	private String Account;
	
	@JSONField(name="ClientToken")
	private String ClientToken;
	
	@JSONField(name="Language")
	private String Language;
	
	@JSONField(name="CNode")
	private CNode CNode;
	
	@JSONField(name="QNode")
	private QNode QNode;

	public String getStateID() {
		return StateID;
	}

	public void setStateID(String stateID) {
		StateID = stateID;
	}

	public String getPlatfrom() {
		return Platfrom;
	}

	public void setPlatfrom(String platfrom) {
		Platfrom = platfrom;
	}

	public String getQueryTime() {
		return QueryTime;
	}

	public void setQueryTime(String queryTime) {
		QueryTime = queryTime;
	}

	public String getAccount() {
		return Account;
	}

	public void setAccount(String account) {
		Account = account;
	}

	public String getClientToken() {
		return ClientToken;
	}

	public void setClientToken(String clientToken) {
		ClientToken = clientToken;
	}

	public String getLanguage() {
		return Language;
	}

	public void setLanguage(String language) {
		Language = language;
	}

	public CNode getCNode() {
		return CNode;
	}

	public void setCNode(CNode cNode) {
		CNode = cNode;
	}

	public QNode getQNode() {
		return QNode;
	}

	public void setQNode(QNode qNode) {
		QNode = qNode;
	}
	
}
