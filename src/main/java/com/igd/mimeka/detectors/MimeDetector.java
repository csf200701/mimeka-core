package com.igd.mimeka.detectors;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.igd.mimeka.config.MimeConfig;
import com.igd.mimeka.mime.MimeType;

public class MimeDetector implements Detector{
	
	private final MimeConfig config;
	
	public MimeDetector(MimeConfig config) {
		this.config = config;
	}
	
	public String detect(byte[] data) {
		try {
			MimeType mimeType =  onDetect(data, null);
			if(mimeType != null) {
				return mimeType.getMime();
			}
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected Exception", e);
		}
		return null;
	}
	
	public String detect(byte[] data, String fileName) {
		try {
			MimeType mimeType =  onDetect(data, fileName);
			if(mimeType != null) {
				return mimeType.getMime();
			}
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected Exception", e);
		}
		return null;
	}

	@Override
	public MimeType onDetect(final byte[] data, String fileName) throws Exception {
		MimeType mimeType = null;
		Map<String, Integer> plMap = config.getMimeTypes().getPlMap();
		final List<String> typeList = new ArrayList<>();
		final Map<String, Integer> countTypeMap = new HashMap<>();
		plMap.keySet().stream().forEach(pl -> {
			String[] pls = pl.split(",");
			int start = Integer.valueOf(pls[0]);
			int len = Integer.valueOf(pls[1]);
			int sum = Integer.sum(start, len) -1;
			byte[] header = new byte[len];
			if(data.length > sum) {
				System.arraycopy(data, start, header, 0, len);
				String hex = new BigInteger(1, header).toString(16).toUpperCase();
//				System.out.println(start + " " + len + "," + hex);
				List<String> types = config.getMimeTypes().getSignMap().get(start + "," + hex);
				if(types != null) {
					types = types.stream().distinct().collect(Collectors.toList());
					typeList.addAll(types);	
					for(String type : types) {
						int value = len;
						value += countTypeMap.getOrDefault(type, 0);
						countTypeMap.put(type, value);
					}
				}
			}
		});
		Optional<Map.Entry<String,Integer>> max = countTypeMap.entrySet().stream().max(Map.Entry.comparingByValue());
		if(max.isPresent()) {
			Map.Entry<String,Integer> maxEntry = max.get();
			String type = maxEntry.getKey();
			if(fileName != null && !"".equals(fileName.trim())) {
				String ext = getExt(fileName);
				if(ext != null && !"".equals(ext.trim()) && !ext.equals(type)) {
					int extMax = countTypeMap.getOrDefault(ext, 0);
					if(extMax >= maxEntry.getValue()) {
						type = ext;
					}
				}
			}
			mimeType = config.getMimeTypes().getFileMap().get(type);
 		}
		return mimeType;
	}
	
	private String getExt(String fileName) {
		 if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
		        return fileName.substring(fileName.lastIndexOf(".")+1);
		 } else {
			 return "";
		 }

	}

}
