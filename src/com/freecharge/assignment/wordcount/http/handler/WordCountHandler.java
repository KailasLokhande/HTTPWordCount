package com.freecharge.assignment.wordcount.http.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.freecharge.assignment.wordcount.dataprovider.WordDataProvider;

/**
 * Handler class.
 * 
 * All request coming from USER will be served here.
 * 
 * @author lokhande
 *
 */

@Path("/")
public class WordCountHandler {

	private static WordDataProvider wordDataProvider = new WordDataProvider();
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Map<String,Object> getWordCount(@QueryParam("query") String queryWord, @QueryParam("ignoreCase") boolean ignoreCase) {
		Map<String, Object> output = new HashMap<String, Object>();
		try {
		int count = wordDataProvider.getWordCount(queryWord, ignoreCase);
		
		output.put("query", queryWord);
		if(ignoreCase)
		output.put("ignoreCase", ignoreCase);
		output.put("count", count);
		
		} catch (Exception exception) {
			output.put("error",true);
			output.put("errorMessage", exception.getMessage());
			// TODO: Log exception.
		}
		
		return output;
	}
}

