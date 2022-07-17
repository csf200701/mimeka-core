package com.igd.mimeka.mime;

import java.util.ArrayList;
import java.util.List;

public class MimeType {
	
	private String type;
	private String mime;
	private List<String> signs;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public List<String> getSigns() {
		return signs;
	}
	public void setSigns(ArrayList<String> signs) {
		this.signs = signs;
	}
	
}
