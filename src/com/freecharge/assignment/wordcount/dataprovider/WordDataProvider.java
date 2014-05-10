package com.freecharge.assignment.wordcount.dataprovider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.freecharge.assignment.wordcount.dataprovider.utils.Constants;

/**
 * This is data provider class. Responsibility of this class is to read from
 * config file defined for this application in
 * {@code Constants#CONFIG_FILE_PATH}. This config file provides list of data
 * files, these files will be parse and words are stored in memory along with
 * their count.
 * 
 * 
 * @author lokhande
 * 
 */
public class WordDataProvider {

	/**
	 * InMemory data structure to store words with their count. For case
	 * sensitive searches this data structure will be used.
	 **/
	private static Map<String, Integer> wordCount = new HashMap<String, Integer>();
	/**
	 * This is another InMemory data structure to store word and their counts.
	 * But this will be used in case of case insensitive searches. 
	 * NOTE: Yes, I
	 * agree, this increase space complexity. But it improves time complexity
	 * for case insensitive search. Another option for this was to iterate over
	 * all keys and compare with query word. This will surely take O(n)
	 * complexity. Whereas with this extra space complexity, we are getting O(1)
	 * search.
	 */
	private static Map<String, Integer> wordCountIgnoreCase = new HashMap<String, Integer>();

	/**
	 * Here we are enabling addition and removal of files from config and
	 * loading these changes at runtime.
	 * 
	 * This variable will hold memory update timestamp.
	 */
	private static long lastModified = 0;

	/**
	 * This function reads config file and identifies data files specified.
	 * 
	 * Files will be read and loaded , if and only if they are modified after
	 * last read.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void readFiles() throws FileNotFoundException, IOException,
			URISyntaxException {
		// Reading config file location, location will be relative to webcontext
		// as we are not in servlet class where servlet context will be
		// available we are reading resource by taking class loader.
		URI configFileURI = Thread.currentThread().getContextClassLoader()
				.getResource(Constants.CONFIG_FILE_PATH).toURI();
		File configFile = new File(configFileURI);

		// check if config file is modified at backend or not. if it is
		// modified, its last modified time will be greater than what we have
		// stored.
		if (lastModified < configFile.lastModified()) {
			Properties properties = new Properties();
			properties.load(new FileReader(configFile));
			String filePaths = properties.getProperty(Constants.FILE_PATHS);

			if (filePaths == null || filePaths.trim().length() == 0) {
				throw new IllegalStateException(
						"Config file must specify atleast 1 data file.");
			}

			// Reinitialize inmemory.
			wordCount = new HashMap<String, Integer>();
			wordCountIgnoreCase = new HashMap<String, Integer>();
			String[] filePathList = filePaths.split(",");
			for (String filePath : filePathList) {

				File file = new File(filePath);
				updateWordCount(file);
			}
			lastModified = configFile.lastModified();
		}
	}

	/**
	 * This function actually reads file and update word count in memory.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	private static void updateWordCount(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		while (scanner.hasNext()) {
			String next = scanner.next().trim();
			// Case sensitive data store update
			int count = 1;
			if (wordCount.containsKey(next)) {
				count += wordCount.get(next);
			}
			wordCount.put(next, count);

			// Case insensitive data store update
			// all keys will be stored in lowercase.
			count = 1;
			if (wordCountIgnoreCase.containsKey(next)) {
				count += wordCountIgnoreCase.get(next);
			}
			wordCountIgnoreCase.put(next.toLowerCase(), count);
		}
		scanner.close();
	}

	/**
	 * Function which returns all words available with their counts.
	 * 
	 * @return Map of word and its count.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Map getAllWordCounts()
			throws FileNotFoundException, IOException, URISyntaxException {
		return getAllWordCounts(false);
	}

	/**
	 * Function to return all words available with their count, with additional
	 * support to choose case sensitivity.
	 * 
	 * @param ignoreCase
	 * @return Map of word and its count.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Map getAllWordCounts(boolean ignoreCase)
			throws FileNotFoundException, IOException, URISyntaxException {
		readFiles();
		if (ignoreCase) {
			return wordCountIgnoreCase;
		}
		return wordCount;
	}

	/**
	 * Get word count , CASE SENSITIVE.
	 * 
	 * @param word
	 * @return word count
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public int getWordCount(String word) throws FileNotFoundException,
			IOException, URISyntaxException {

		return getWordCount(word, false);

	}

	/**
	 * Returns word count, CASE SENSITIVITY depends on boolean value.
	 * @param word
	 * @param ignoreCase
	 * @return word count
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public int getWordCount(String word, boolean ignoreCase)
			throws FileNotFoundException, IOException, URISyntaxException {
		readFiles();

		if (word == null || word.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Please provide proper query word. Word should not be null or empty. For example: <host>:<port>/<path>/?query=<word>&ignoreCase=<true/false>");
		}

		if (wordCount == null || wordCountIgnoreCase == null) {
			throw new IllegalStateException("No data setup on server side");
		}

		if (ignoreCase) {
			return wordCountIgnoreCase.get(word.trim().toLowerCase());
		}

		if (wordCount.containsKey(word.trim())) {
			return wordCount.get(word.trim());
		}
		return 0;
	}

}
