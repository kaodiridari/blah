package koa.diridari;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class Constants {
	
	//static final public Path chunksDir = Paths.get("C:\\Users\\user\\Documents\\java\\swing\\blah\\chunks");	
		
	static Properties globalProperties;
	
	public static Properties getProperties() throws Exception {
	    if (globalProperties == null) {
	        globalProperties = new Properties();
	        globalProperties.load(Files.newInputStream(Paths.get("C:\\Users\\user\\Documents\\java\\swing\\blah\\src\\main\\resources", "config.properties"), StandardOpenOption.READ));
	    }
	    return globalProperties;
	}
	
	/**
	 * Gets a single property. Throws an Exception if the demanded property does not exist.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getProperty(String key) throws Exception {
	   String val = (String)getProperties().get(key);
	   if (StringUtils.isEmpty(val)) {
	       throw new Exception("There is no property for the key: " + key);
	   }
	   return val;
	}	
}
