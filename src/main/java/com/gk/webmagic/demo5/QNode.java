package com.gk.webmagic.demo5;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class QNode {
	
	@JSONField(name="SelectT")
	private String SelectT;
	
	@JSONField(name="Select_Fields")
	private String Select_Fields;
	
	@JSONField(name="S_DBCodes")
	private String S_DBCodes;
	
	@JSONField(name="QGroup")
	private List<QGroup> QGroup;
	
	@JSONField(name="OrderBy")
	private String OrderBy;
	
	@JSONField(name="GroupBy")
	private String GroupBy;
	
	@JSONField(name="Additon")
	private String Additon;
	
	public static class QGroup{
		
		@JSONField(name="Key")
		private String Key;
		
		@JSONField(name="Logic")
		private int Logic;
		
		@JSONField(name="Items")
		private List<String> Items;
		
		@JSONField(name="ChildItems")
		private List<ChildItems> ChildItems;
		
		public static class ChildItems{
			
			@JSONField(name="Key")
			private String Key;
			
			@JSONField(name="Logic")
			private int Logic;
			
			@JSONField(name="ChildItems")
			private List<String> ChildItems;
			
			@JSONField(name="Items")
			private List<Items> Items;
			
			public static class Items {
				@JSONField(name="Key")
				private int Key;
				
				@JSONField(name="Title")
				private String Title;
				
				@JSONField(name="Logic")
				private int Logic;
				
				@JSONField(name="Name")
				private String Name;
				
				@JSONField(name="Operate")
				private String Operate;
				
				@JSONField(name="Value")
				private String Value;
				
				@JSONField(name="ExtendType")
				private int ExtendType;
				
				@JSONField(name="ExtendValue")
				private String ExtendValue;
				
				@JSONField(name="Value2")
				private String Value2;

				public int getKey() {
					return Key;
				}

				public void setKey(int key) {
					Key = key;
				}

				public String getTitle() {
					return Title;
				}

				public void setTitle(String title) {
					Title = title;
				}

				public int getLogic() {
					return Logic;
				}

				public void setLogic(int logic) {
					Logic = logic;
				}

				public String getName() {
					return Name;
				}

				public void setName(String name) {
					Name = name;
				}

				public String getOperate() {
					return Operate;
				}

				public void setOperate(String operate) {
					Operate = operate;
				}

				public String getValue() {
					return Value;
				}

				public void setValue(String value) {
					Value = value;
				}

				public int getExtendType() {
					return ExtendType;
				}

				public void setExtendType(int extendType) {
					ExtendType = extendType;
				}

				public String getExtendValue() {
					return ExtendValue;
				}

				public void setExtendValue(String extendValue) {
					ExtendValue = extendValue;
				}

				public String getValue2() {
					return Value2;
				}

				public void setValue2(String value2) {
					Value2 = value2;
				}
			}

			public String getKey() {
				return Key;
			}

			public void setKey(String key) {
				Key = key;
			}

			public int getLogic() {
				return Logic;
			}

			public void setLogic(int logic) {
				Logic = logic;
			}

			public List<Items> getItems() {
				return Items;
			}

			public void setItems(List<Items> items) {
				Items = items;
			}

			public List<String> getChildItems() {
				return ChildItems;
			}

			public void setChildItems(List<String> childItems) {
				ChildItems = childItems;
			}

		}

		public String getKey() {
			return Key;
		}

		public void setKey(String key) {
			Key = key;
		}

		public int getLogic() {
			return Logic;
		}

		public void setLogic(int logic) {
			Logic = logic;
		}

		public List<String> getItems() {
			return Items;
		}

		public void setItems(List<String> items) {
			Items = items;
		}

		public List<ChildItems> getChildItems() {
			return ChildItems;
		}

		public void setChildItems(List<ChildItems> childItems) {
			ChildItems = childItems;
		}
		
	}

	public String getSelectT() {
		return SelectT;
	}

	public void setSelectT(String selectT) {
		SelectT = selectT;
	}

	public String getSelect_Fields() {
		return Select_Fields;
	}

	public void setSelect_Fields(String select_Fields) {
		Select_Fields = select_Fields;
	}

	public String getS_DBCodes() {
		return S_DBCodes;
	}

	public void setS_DBCodes(String s_DBCodes) {
		S_DBCodes = s_DBCodes;
	}

	public List<QGroup> getQGroup() {
		return QGroup;
	}

	public void setQGroup(List<QGroup> qGroup) {
		QGroup = qGroup;
	}

	public String getOrderBy() {
		return OrderBy;
	}

	public void setOrderBy(String orderBy) {
		OrderBy = orderBy;
	}

	public String getGroupBy() {
		return GroupBy;
	}

	public void setGroupBy(String groupBy) {
		GroupBy = groupBy;
	}

	public String getAdditon() {
		return Additon;
	}

	public void setAdditon(String additon) {
		Additon = additon;
	}
	
	

}
