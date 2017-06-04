/**
 * smmdbapi query
 * start query and return all courses as ArrayList
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
	
	private String url = "http://smmdb.ddns.net/api/getcourses?";

	public SmmdbApiQuery() {
		//Auto-generated constructor stub
	}
	
	//TODO needs to be tested
	public ArrayList<SmmdbApiDataType> startQuery() {
    	ArrayList<Integer> courseIDs = new ArrayList<>();
    	ArrayList<SmmdbApiDataType> course = new ArrayList<>();
    	String output = "";
    	

        try {
        	URL apiUrl = new URL(url);
        	BufferedReader ina = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			output = ina.readLine();
			ina.close();
		} catch (IOException e) {
			//Auto-generated catch block
			e.printStackTrace();
		}
        
        JsonObject mainObject = Json.parse(output).asObject().get("courses").asObject();
        System.out.println(mainObject);
        
        
        JsonArray objectAssets = Json.parse(output).asObject().get("order").asArray();
        for (JsonValue asset : objectAssets) {
        	courseIDs.add(asset.asInt());
        }
        
        //FIXME if parameter = null query will stop
        for (int i = 0; i < courseIDs.size(); i++) {
        	System.out.println(i);
        	 JsonObject singleObject = mainObject.get(courseIDs.get(i).toString()).asObject();
        	 int id = singleObject.getInt("id", 0);
        	 int owner = singleObject.getInt("owner", 0);
        	 int coursetype = singleObject.getInt("coursetype", 0);
        	 int leveltype = singleObject.getInt("leveltype", 0);
        	 int difficulty = singleObject.getInt("difficulty", 0);
        	 int lastmodified = singleObject.getInt("lastmodified", 0);
        	 int uploaded = singleObject.getInt("uploaded", 0);
        	 int downloads = singleObject.getInt("downloads", 0);
        	 int stars = singleObject.getInt("stars", 0);
        	 int ispackage = singleObject.getInt("ispackage", 0);
        	 int updatereq = singleObject.getInt("updatereq", 0);
//        	 String nintendoid = singleObject.getString("nintendoid", "");
        	 String title = singleObject.getString("title", "");
        	
        	 course.add(new SmmdbApiDataType(id, owner, coursetype, leveltype, difficulty, lastmodified, uploaded, downloads,
        			 						stars, ispackage, updatereq, title));
		}
        
		return course;
	}
	
}
