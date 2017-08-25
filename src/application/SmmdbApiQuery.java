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
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import datatypes.SmmdbApiDataType;

public class SmmdbApiQuery {
	
	private String URL = "http://smmdb.ddns.net/api/getcourses?";

	public SmmdbApiQuery() {
		//Auto-generated constructor stub
	}
	
	//start api query
	public ArrayList<SmmdbApiDataType> startQuery() {
    	ArrayList<Integer> courseIDs = new ArrayList<>();
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
        	int id, owner, coursetype, gamestyle, difficulty, lastmodified, uploaded, downloads, stars, hasimage, ispackage, updatereq;
        	String nintendoid, title;
        	
        	//TODO add "courseTheme", "time", "autoScroll"
        	
        	//geht
        	try {
        		id = item.asObject().getInt("id", 0);
			} catch (Exception e) {
				id = 9;
			}
        	
        	//geht
        	try {
        		owner = item.asObject().getInt("owner", 0);
			} catch (Exception e) {
				owner = 9;
			}
        	
        	//test
        	try {
        		coursetype = item.asObject().getInt("coursetype", 0);
			} catch (Exception e) {
				coursetype = 9;
			}
        	
        	//geht
        	try {
        		gamestyle = item.asObject().getInt("gamestyle", 0);
			} catch (Exception e) {
				gamestyle = 9;
			}
        	
        	//geht
        	try {
        		difficulty = item.asObject().getInt("difficulty", 0);
			} catch (Exception e) {
				difficulty = 9;
			}
        	
        	//geht
        	try {
        		lastmodified = item.asObject().getInt("lastmodified", 0);
			} catch (Exception e) {
				lastmodified = 9;
			}
        	
        	//geht
        	try {
        		uploaded = item.asObject().getInt("uploaded", 0);
			} catch (Exception e) {
				uploaded = 9;
			}
        	
        	//gestrichen
        	try {
        		downloads = item.asObject().getInt("downloads", 0);
			} catch (Exception e) {
				downloads = 9;
			}
        	
        	//geht
        	try {
        		stars = item.asObject().getInt("stars", 0);
			} catch (Exception e) {
				stars = 9;
			}
        	
        	//gestrichen
        	try {
        		hasimage = item.asObject().getInt("hasimage", 0);
			} catch (Exception e) {
				hasimage = 9;
			}
        	
        	//gestrichen
        	try {
        		ispackage = item.asObject().getInt("ispackage", 0);
			} catch (Exception e) {
				ispackage = 9;
			}
        	
        	//gestrichen
        	try {
        		updatereq = item.asObject().getInt("updatereq", 0);
			} catch (Exception e) {
				updatereq = 9;
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

        	course.add(new SmmdbApiDataType(id, owner, coursetype, gamestyle, difficulty, lastmodified, uploaded, downloads,
        			 						stars, hasimage, ispackage, updatereq, nintendoid, title));
        }
        
		return course;
	}
	
}
