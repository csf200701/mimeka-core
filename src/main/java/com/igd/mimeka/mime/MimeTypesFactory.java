package com.igd.mimeka.mime;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.igd.mimeka.io.MimeTypesReader;


public class MimeTypesFactory {
	
	public static MimeTypes create(String coreFilePath, ClassLoader classLoader) throws Exception {
		// If no specific classloader was requested, use our own class's one
		if (classLoader == null) {
		classLoader = MimeTypesReader.class.getClassLoader();
		}
		
		// This allows us to replicate class.getResource() when using
		//  the classloader directly
		String classPrefix = MimeTypesFactory.class.getPackage().getName().replace('.', '/') + "/";
		
		// Get the core URL, and all the extensions URLs
		URL coreURL = classLoader.getResource(classPrefix + coreFilePath);
		
		// Swap that into an Array, and process
		List<URL> urls = new ArrayList<>();
		urls.add(coreURL);
		
		return create(urls.toArray(new URL[0]));
	}
	
	public static MimeTypes create(URL... urls) throws Exception {
        InputStream[] streams = new InputStream[urls.length];
        for (int i = 0; i < streams.length; i++) {
            streams[i] = urls[i].openStream();
        }

        try {
            return create(streams);
        } finally {
            for (InputStream stream : streams) {
                stream.close();
            }
        }
    }
	
	public static MimeTypes create(InputStream... inputStreams) throws Exception  {
        MimeTypes mimeTypes = new MimeTypes();
        MimeTypesReader reader = new MimeTypesReader(mimeTypes);
        for (InputStream inputStream : inputStreams) {
            reader.read(inputStream);
        }
        return mimeTypes;
    }

}
