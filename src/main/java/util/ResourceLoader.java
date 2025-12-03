package util;

import java.net.URL;

public class ResourceLoader {
    
    public static URL getResource(String path) {
        // Remove leading slash if present for classloader
        String classLoaderPath = path.startsWith("/") ? path.substring(1) : path;
        
        // Try classloader first (most reliable)
        URL resource = ResourceLoader.class.getClassLoader().getResource(classLoaderPath);
        if (resource != null) {
            return resource;
        }
        
        // Try with absolute path
        resource = ResourceLoader.class.getResource(path);
        if (resource != null) {
            return resource;
        }
        
        // Try with main/resources prefix if resources are nested (IntelliJ issue)
        if (path.startsWith("/")) {
            resource = ResourceLoader.class.getClassLoader().getResource("main/resources" + path);
            if (resource != null) {
                return resource;
            }
        }
        
        return null;
    }
}

