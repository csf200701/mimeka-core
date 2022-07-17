package com.igd.mimeka;

import com.igd.mimeka.config.MimeConfig;
import com.igd.mimeka.detectors.MimeDetector;

public class MimeKa {
	private MimeDetector detector;
	
	public MimeKa()  {
		this(MimeConfig.getDefaultConfig());
	}
	
	public MimeKa(MimeConfig config) {
		this.detector = new MimeDetector(config);
	}
	
	public String detect(byte[] data) {
		detect(data, null);
		return detect(data, null);
	}
	
	public String detect(byte[] data, String fileName) {
		return detector.detect(data, fileName);
	}

}
