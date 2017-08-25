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
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import datatypes.SmmdbApiDataType;

public class SmmdbApiQuery {
	
	private String URL = "http://smmdb.ddns.net/api/getcourses?";

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
		} catch (IOException e) {
			System.out.println("error while making api request or reading response");
			e.printStackTrace();
		}
        
        System.out.println(URL);
        System.out.println("{ \"courses\": " + output + "}");
        
        String apiOutput = "{ \"courses\": " + output + "}";
        
        JsonArray items = Json.parse(apiOutput).asObject().get("courses").asArray();
        for (JsonValue item : items) {
        	int courseTheme, gameStyle, difficulty, lastmodified, uploaded, autoScroll, stars ,time;
        	String owner, id, nintendoid, title;
        	
        	//geht
        	try {
        		courseTheme = item.asObject().getInt("courseTheme", 9);
			} catch (Exception e) {
				courseTheme = 9;
			}
        	
        	//geht
        	try {
        		gameStyle = item.asObject().getInt("gameStyle", 9);
			} catch (Exception e) {
				gameStyle = 9;
			}
        	
        	//geht
        	try {
        		difficulty = item.asObject().getInt("difficulty", 9);
			} catch (Exception e) {
				difficulty = 9;
			}
        	
        	//geht
        	try {
        		lastmodified = item.asObject().getInt("lastmodified", 9);
			} catch (Exception e) {
				lastmodified = 9;
			}
        	
        	//geht
        	try {
        		uploaded = item.asObject().getInt("uploaded", 9);
			} catch (Exception e) {
				uploaded = 9;
			}
        	
        	//geht
        	try {
        		autoScroll = item.asObject().getInt("autoScroll", 9);
			} catch (Exception e) {
				autoScroll = 9;
			}
        	
        	//geht
        	try {
        		stars = item.asObject().getInt("stars", 9);
			} catch (Exception e) {
				stars = 9;
			}
        	
        	//geht
        	try {
        		time = item.asObject().getInt("time", 9);
			} catch (Exception e) {
				time = 9;
			}
        	
        	//geht
        	try {
        		owner = item.asObject().getString("owner", "");
			} catch (Exception e) {
				owner = "notset";
			}
        	
        	//geht
        	try {
        		id = item.asObject().getString("id", "");
			} catch (Exception e) {
				id = "notset";
			}
        	
        	//geht
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
