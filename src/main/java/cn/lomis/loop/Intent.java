package cn.lomis.loop;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class Intent {
	private Component parent;
	private Class<?> frameClass;
	
	private Map<String, Object> dataMap = new HashMap<>();
	
	public Intent() {
	}
	
	public Intent(Component componet, Class<?> clz) {
		this.parent = componet;
		this.frameClass = clz;
	}
	

	public void putData(String key, Object data) {
		dataMap.put(key, data);
	}

	public void setFrameClass(Class<?> frameClass) {
		this.frameClass = frameClass;
	}
	
	public Class<?> getFrameClass() {
		return frameClass;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public Component getParent() {
		return parent;
	}
	
}
