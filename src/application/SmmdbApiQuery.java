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
        
        JsonObject mainObject = Json.parse(output).asObject().get("courses").asObject();
        JsonArray objectAssets = Json.parse(output).asObject().get("order").asArray();
        
        for (JsonValue asset : objectAssets) {
        	courseIDs.add(asset.asInt());
        }
        
        //if value is 9 or "notset" the api returned NULL as value
        for (int i = 0; i < courseIDs.size(); i++) {
        	int id, owner, coursetype, leveltype, difficulty, lastmodified, uploaded, downloads, stars, ispackage, updatereq;
        	String nintendoid, title;
        	JsonObject singleObject = mainObject.get(courseIDs.get(i).toString()).asObject();

        	try {
        		id = singleObject.getInt("id", 0);
			} catch (Exception e) {
				id = 9;
			}
        	try {
        		owner = singleObject.getInt("owner", 0);
			} catch (Exception e) {
				owner = 9;
			}
        	try {
        		coursetype = singleObject.getInt("coursetype", 0);
			} catch (Exception e) {
				coursetype = 9;
			}
        	try {
        		leveltype = singleObject.getInt("leveltype", 0);
			} catch (Exception e) {
				leveltype = 9;
			}
        	try {
        		difficulty = singleObject.getInt("difficulty", 0);
			} catch (Exception e) {
				difficulty = 9;
			}
        	try {
        		lastmodified = singleObject.getInt("lastmodified", 0);
			} catch (Exception e) {
				lastmodified = 9;
			}
        	try {
        		uploaded = singleObject.getInt("uploaded", 0);
			} catch (Exception e) {
				uploaded = 9;
			}
        	try {
        		downloads = singleObject.getInt("downloads", 0);
			} catch (Exception e) {
				downloads = 9;
			}
        	try {
        		stars = singleObject.getInt("stars", 0);
			} catch (Exception e) {
				stars = 9;
			}
        	try {
        		ispackage = singleObject.getInt("ispackage", 0);
			} catch (Exception e) {
				ispackage = 9;
			}
        	try {
        		updatereq = singleObject.getInt("updatereq", 0);
			} catch (Exception e) {
				updatereq = 9;
			}
        	try {
        		nintendoid = singleObject.getString("nintendoid", "");
			} catch (Exception e) {
				nintendoid = "notset";
			}
        	try {
        		title = singleObject.getString("title", "");;
			} catch (Exception e) {
				title = "notset";
			}

        	course.add(new SmmdbApiDataType(id, owner, coursetype, leveltype, difficulty, lastmodified, uploaded, downloads,
        			 						stars, ispackage, updatereq, nintendoid, title));
		}
        
		return course;
	}
	
}
