package com.kdd.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * @author Bheemarao.M
 *
 */
public class PropertiesConfig implements GlobalVariables {
	private static Properties properties = new Properties();
	private static Properties runTimeproperties = new Properties();
	private static final Logger Log = Logger.getLogger(PropertiesConfig.class.getName());

	public static String getProperty(String key) {
		try {
			InputStream stream = new FileInputStream(new File(configPath));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			Log.error("File was Not Found: " + e.getMessage());
		} catch (IOException e) {
			Log.error("There was a IO Exception: ", e);
		}
		return properties.getProperty(key);
	}

	public static String getRunTimeProperty(String key) {
		try {
			InputStream stream = new FileInputStream(new File(configPathRuntime));
			runTimeproperties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			Log.error("File was Not Found: " + e.getMessage());
		} catch (IOException e) {
			Log.error("There was a IO Exception: ", e);
		}
		return runTimeproperties.getProperty(key);
	}

	public static void setRunTimeProperty(String key, String value) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(configPathRuntime);
			runTimeproperties.setProperty(key, value);
			runTimeproperties.store(fileOutputStream, "Sample way of creating Properties file from Java program");
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			Log.error("File was Not Found: " + e.getMessage());
		} catch (IOException e) {
			Log.error("There was a IO Exception: ", e);
		}
	}
}
