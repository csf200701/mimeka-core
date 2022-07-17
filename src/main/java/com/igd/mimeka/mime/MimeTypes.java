package com.igd.mimeka.mime;

import java.util.List;
import java.util.Map;

public class MimeTypes {
	
	private Map<String, MimeType> fileMap;
	private Map<String, List<String>> signMap;
	private Map<String, Integer> plMap;

	public Map<String, MimeType> getFileMap() {
		return fileMap;
	}

	public void setFileMap(Map<String, MimeType> fileMap) {
		this.fileMap = fileMap;
	}

	public Map<String, List<String>> getSignMap() {
		return signMap;
	}

	public void setSignMap(Map<String, List<String>> signMap) {
		this.signMap = signMap;
	}

	public Map<String, Integer> getPlMap() {
		return plMap;
	}

	public void setPlMap(Map<String, Integer> plMap) {
		this.plMap = plMap;
	}
	
}
