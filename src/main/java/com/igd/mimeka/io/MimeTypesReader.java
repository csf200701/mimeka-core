package com.igd.mimeka.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.igd.mimeka.mime.MimeType;
import com.igd.mimeka.mime.MimeTypes;

public class MimeTypesReader {
	
	private MimeTypes mimeTypes;
	
	public MimeTypesReader(MimeTypes mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	public void read(InputStream inputStream) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String str = null;
		StringBuilder sb = new StringBuilder();
		while((str = br.readLine()) != null) {
			sb.append(str);
		}
		
		read(sb.toString());
	}
	
	public void read(String json) throws Exception {
		Map<String, MimeType> fileMap = JsonReader.resolver(json, new TypeReference<Map<String, MimeType>>(){});
		final Map<String, List<String>> signMap = new HashMap<>();
		final Map<String, Integer> plMap = new HashMap<>();
		fileMap.forEach((k, m) -> {
			m.setType(k);
			List<String> signs = m.getSigns();
			for(String sign : signs) {
				if(signMap.containsKey(sign)) {
					signMap.get(sign).add(m.getType());
				} else {
					List<String> types = new ArrayList<>();
					signMap.put(sign, types);
					types.add(m.getType());
				}
				String[] sps = sign.split(",");
				String hex = sps[1].replaceAll(" ", "");
				plMap.put(sps[0] + "," + hex.length() / 2, 1);
			}
		});
		mimeTypes.setFileMap(fileMap);
		mimeTypes.setSignMap(signMap);
		mimeTypes.setPlMap(plMap);
	}

}
