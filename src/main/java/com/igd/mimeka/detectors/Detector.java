package com.igd.mimeka.detectors;

import com.igd.mimeka.mime.MimeType;

public interface Detector {
	
	MimeType onDetect(byte[] data, String fileName) throws Exception;

}
