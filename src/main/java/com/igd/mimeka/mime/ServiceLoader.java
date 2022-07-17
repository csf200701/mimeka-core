package com.igd.mimeka.mime;

import java.io.InputStream;

public class ServiceLoader {
	
	private final ClassLoader loader;
	private  static volatile ClassLoader contextClassLoader;
	
	public ServiceLoader() {
		this(getContextClassLoader());
	}
	
	public ServiceLoader(ClassLoader loader) {
		this.loader = loader;
	}
		
	static ClassLoader getContextClassLoader() {
        ClassLoader loader = contextClassLoader;
        if (loader == null) {
            loader = ServiceLoader.class.getClassLoader();
        }
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }
	
	public static void setContextClassLoader(ClassLoader loader) {
        contextClassLoader = loader;
    }
	
	public ClassLoader getLoader() {
        return loader;
    }
	
	public InputStream getResourceAsStream(String name) {
        if (loader != null) {
            return loader.getResourceAsStream(name);
        } else {
            return null;
        }
    }

}
