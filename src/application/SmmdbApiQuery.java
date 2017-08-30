/**
 * smmdbapi query
 * api query, return all courses as ArrayList
 */
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import datatypes.SmmdbApiDataType;

public class SmmdbApiQuery {
	
	private String URL = "http://smmdb.ddns.net/api/getcourses?";
	private static final Logger LOGGER = LogManager.getLogger(SmmdbApiQuery.class.getName());

	public SmmdbApiQuery() {
		//Auto-generated constructor stub
	}
	
	/**
	 * start smmdb api query
	 * @return a ArryList with all courses found at smmdb
	 */
	public ArrayList<SmmdbApiDataType> startQuery() {
    	ArrayList<SmmdbApiDataType> course = new ArrayList<>();
    	String output = "";
    	

        try {
        	URL apiUrl = new URL(URL);
        	BufferedReader ina = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			output = ina.readLine();
			ina.close();
			LOGGER.info("response from " + URL + " was valid");
		} catch (IOException e) {
			LOGGER.error("error while making api request or reading response");
			LOGGER.error("response from " + URL + " was: " + output, e);
		}
        
        String apiOutput = "{ \"courses\": " + output + "}";  
        JsonArray items = Json.parse(apiOutput).asObject().get("courses").asArray();
        
        for (JsonValue item : items) {
        	int courseTheme, gameStyle, difficulty, lastmodified, uploaded, autoScroll, stars ,time;
        	String owner, id, nintendoid, title;

        	try {
        		courseTheme = item.asObject().getInt("courseTheme", 9);
			} catch (Exception e) {
				courseTheme = 9;
			}

        	try {
        		gameStyle = item.asObject().getInt("gameStyle", 9);
			} catch (Exception e) {
				gameStyle = 9;
			}

        	try {
        		difficulty = item.asObject().getInt("difficulty", 9);
			} catch (Exception e) {
				difficulty = 9;
			}

        	try {
        		lastmodified = item.asObject().getInt("lastmodified", 9);
			} catch (Exception e) {
				lastmodified = 9;
			}

        	try {
        		uploaded = item.asObject().getInt("uploaded", 9);
			} catch (Exception e) {
				uploaded = 9;
			}

        	try {
        		autoScroll = item.asObject().getInt("autoScroll", 9);
			} catch (Exception e) {
				autoScroll = 9;
			}

        	try {
        		stars = item.asObject().getInt("stars", 9);
			} catch (Exception e) {
				stars = 9;
			}

        	try {
        		time = item.asObject().getInt("time", 9);
			} catch (Exception e) {
				time = 9;
			}

        	try {
        		owner = item.asObject().getString("owner", "");
			} catch (Exception e) {
				owner = "notset";
			}

        	try {
        		id = item.asObject().getString("id", "");
			} catch (Exception e) {
				id = "notset";
			}

        	try {
        		nintendoid = item.asObject().getString("nintendoid", "");
			} catch (Exception e) {
				nintendoid = "notset";
			}
        	
        	try {
        		title = item.asObject().getString("title", "");;
			} catch (Exception e) {
				title = "notset";
			}

        	course.add(new SmmdbApiDataType(courseTheme, gameStyle, difficulty, lastmodified, uploaded, autoScroll,
        			 						stars, time, owner, id, nintendoid, title));
        }
        
		return course;
	}
	
}
