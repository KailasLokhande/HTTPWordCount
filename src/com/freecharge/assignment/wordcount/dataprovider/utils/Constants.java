package com.freecharge.assignment.wordcount.dataprovider.utils;

/**
 * Utility component to hold all constants and config keys.
 * 
 * @author lokhande
 *
 */
public interface Constants {
	// Config file location and name. 
	String CONFIG_FILE_PATH="../../config/http-word-app.properties";
	
	// Config keys
	// Config value of file-paths key will be comma separated list of file paths to be read and loaded into memory.
	String FILE_PATHS = "file-paths";
	
	// TODO: if this application need any other values from config, define keys for them here.
}
