/**
 * cemu_UI
 * 
 * Copyright 2017  <@Seil0>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

package com.cemu_UI.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.datatypes.SmmdbApiDataType;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

public class SmmdbApiQuery {
	
	private String URL = "https://smmdb.ddns.net/api/getcourses?format=json";
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
			LOGGER.info(output);
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
