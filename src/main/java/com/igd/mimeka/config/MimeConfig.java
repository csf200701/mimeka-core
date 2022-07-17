package com.igd.mimeka.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igd.mimeka.exceptions.MimekaException;
import com.igd.mimeka.mime.MimeTypes;
import com.igd.mimeka.mime.MimeTypesFactory;
import com.igd.mimeka.mime.ServiceLoader;

public class MimeConfig {
	
	private final ServiceLoader serviceLoader;
	private final MimeTypes mimeTypes;
	
	public MimeTypes getMimeTypes() {
		return mimeTypes;
	}
	
	public static MimeConfig getDefaultConfig() {
        try {
            return new MimeConfig();
        } catch (MimekaException e) {
            throw new RuntimeException("Unable to read default configuration", e);
        } catch (Exception e) {
        	throw new RuntimeException("Unable to access default configuration", e);
		} 
    }
	
	public MimeConfig() throws MimekaException, Exception {
		String config = System.getProperty("mimeka.config");
        if (config == null || config.trim().equals("")) {
            config = System.getenv("MIMEKA_CONFIG");
        }
        
        this.serviceLoader = new ServiceLoader();
        
        if (config == null || config.trim().equals("")) {
        	this.mimeTypes = MimeTypesFactory.create("mimeka-mimetypes.json", serviceLoader.getLoader());
        } else {
        	 try (InputStream stream = getConfigInputStream(config, serviceLoader)) {
        		 this.mimeTypes = MimeTypesFactory.create(stream);
        	 } 
        }
	}
	
	
	
	private static InputStream getConfigInputStream(String config, ServiceLoader serviceLoader) throws MimekaException, IOException {
        InputStream stream = null;
        try {
            stream = new URL(config).openStream();
        } catch (IOException ignore) {
        }
        if (stream == null) {
            stream = serviceLoader.getResourceAsStream(config);
        }
        if (stream == null) {
            Path file = Paths.get(config);
            if (Files.isRegularFile(file)) {
                stream = Files.newInputStream(file);
            }
        }
        if (stream == null) {
            throw new MimekaException("Specified Mimeka configuration not found: " + config);
        }
        return stream;
    }

}
