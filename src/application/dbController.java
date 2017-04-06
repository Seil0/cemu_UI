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
 * 
 */
package application;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class dbController {
	
	public dbController(MainWindowController m) {
		mainWindowController = m;
	}
	
	private MainWindowController mainWindowController;
	private ArrayList<String> entries = new ArrayList<>();
	private String DB_PATH;
	private String DB_PATH_games;
	private Connection connection = null;
	private Connection connectionGames = null;
	
	public void main(){
		System.out.println("<==========starting loading sql==========>");
		loadRomDatabase();
		loadGamesDatabase();
		createRomDatabase();
		loadRoms();
		checkRemoveEntry();
		System.out.println("<==========finished loading sql==========>"); 
	}
	
	private void loadRomDatabase(){
		if (System.getProperty("os.name").equals("Linux")) {
			DB_PATH = System.getProperty("user.home") + "/localRoms.db";
		}else{
			DB_PATH = System.getProperty("user.home") + "\\Documents\\cemu_UI" + "\\" + "localRoms.db";
		}
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			connection.setAutoCommit(false);	//AutoCommit to false -> manual commit is active
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			System.err.println(e.getMessage());
		}
		System.out.println("rom database loaded successfull");
	}
	
	/**
	 * this method is used to load the games database with additional informations about a game
	 * it is used if a new game is added (automatic or manual)
	 */
	private void loadGamesDatabase(){
		if (System.getProperty("os.name").equals("Linux")) {
			DB_PATH_games = System.getProperty("user.home") + "/games.db";
		}else{
			DB_PATH_games = System.getProperty("user.home") + "\\Documents\\cemu_UI" + "\\" + "games.db";
		}
		try {
			// create a database connection
			connectionGames = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH_games);
			connectionGames.setAutoCommit(false);	//AutoCommit to false -> manual commit is active
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			System.err.println(e.getMessage());
		}
		System.out.println("games database loaded successfull");
	}
	
	//creating database, if db has 0 entries search for all .rpx files in the roms directory and add them
	void createRomDatabase() { 
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("create table if not exists local_roms (title, coverPath, romPath, titleID, productCode, region, lastPlayed, timePlayed)");
			stmt.close();
			connection.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms"); 
			while (rs.next()) { 
				entries.add(rs.getString(2));
			}
			stmt.close();
			rs.close();
		}catch (SQLException ea){
			System.err.println("Ups! an error occured!"); 
			ea.printStackTrace();
		}
		if(entries.size() == 0){
			loadRomDirectory(mainWindowController.getRomPath());
		}
	}

	void addRom(String title, String coverPath, String romPath, String titleID, String productCode, String region, String lastPlayed, String timePlayed) throws SQLException{
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("insert into local_roms values ('"+title+"','"+coverPath+"','"+romPath+"','"+titleID+"','"+productCode+"','"+region+"','"+lastPlayed+"','"+timePlayed+"')");
		connection.commit();
		stmt.close();
		System.out.println("added \""+title+"\" to databsae");
	}
	
	void removeRom(String titleID) throws SQLException{
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("delete from local_roms where titleID = '"+titleID+"'");
		connection.commit();
		stmt.close();
		System.out.println("removed \""+titleID+"\" from databsae");
	}
	
	//load all rom's on startup to the UI
	void loadRoms(){
		System.out.println("loading all rom's on startup to mwc ..."); 
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms"); 
			while (rs.next()) {
				mainWindowController.addGame(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
			stmt.close();
			rs.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//load one single rom after manual adding one
	void loadSingleRom(String titleID){
		System.out.println("loading a single rom (ID: "+titleID+") to mwc ..."); 
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms where titleID = '"+titleID+"'"); 
			while (rs.next()) {
				mainWindowController.addGame(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
			stmt.close();
			rs.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//get all files with .rpx TODO add other formats
	void loadRomDirectory(String directory){
		File dir = new File(directory);
		String[] extensions = new String[] { "rpx", "jsp" };
		File pictureCache;
		String coverPath;
		
		if(System.getProperty("os.name").equals("Linux")){
			pictureCache = mainWindowController.pictureCacheLinux;
		}else{
			pictureCache = mainWindowController.pictureCacheWin;
		} 
		
		try {
			Statement stmt = connectionGames.createStatement(); 
			System.out.println("Getting all .rpx files in " + dir.getCanonicalPath()+" including those in subdirectories \n");
			List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			for (File file : files) {
				File appFile = new File(file.getParent()+"\\app.xml");
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(appFile);
				String title_ID = document.getElementsByTagName("title_id").item(0).getTextContent();
				title_ID = title_ID.substring(0, 8) + "-" + title_ID.substring(8, title_ID.length());
				System.out.println("Name: "+file.getName()+"; Title ID: "+title_ID);
				ResultSet rs = stmt.executeQuery("SELECT * FROM games WHERE TitleID = '"+title_ID+"';");
				while (rs.next()) {
					System.out.print(rs.getString(2));
					if (checkEntry(rs.getString(2))) {
						System.out.println(": game already in database");
					}else{
						System.out.println(": add game");
						System.out.println("adding cover to cache ...");
						
						BufferedImage originalImage = ImageIO.read(new URL(rs.getString(6)));//change path to where file is located
					    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 400, 600);
					    ImageIO.write(resizeImagePNG, "png", new File(pictureCache+"\\"+rs.getString(3)+".png")); //change path where you want it saved
					    coverPath = pictureCache+"\\"+rs.getString(3)+".png";
						
						addRom(rs.getString(2), coverPath, file.getCanonicalPath(), rs.getString(1), rs.getString(3), rs.getString(5),"","0");
					}
				}
				System.out.println("");
			}
		} catch (IOException | SQLException | ParserConfigurationException | SAXException e) {
			System.out.println("Ups something went wrong!");
			e.printStackTrace();
		}
	}
	
	private boolean checkEntry(String title) throws SQLException{
		Statement stmt = connection.createStatement();
		boolean check = false;
		ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms WHERE title = '"+title+"';");
		while (rs.next()) {
			check = true;
		}
		return check;
	}
	
	private void checkRemoveEntry() {
		// TODO needs to be implemented!
		System.out.println("check if entry removed not done yet!");
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
	    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
	    Graphics2D g = resizedImage.createGraphics();
	    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
	    g.dispose();

	    return resizedImage;
	}
	
	void setLastPlayed(String titleID){
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE local_roms SET lastPlayed=date('now') WHERE titleID = '"+titleID+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	String getLastPlayed(String titleID){
		String lastPlayed = null;
		try{
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT lastPlayed FROM local_roms WHERE titleID = '"+titleID+"';" );
			lastPlayed = rs.getString(1);
			stmt.close();
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return lastPlayed;
	}
	
	void setTimePlayed(String timePlayed, String titleID){
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE local_roms SET timePlayed='"+timePlayed+"' WHERE titleID = '"+titleID+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	String getTimePlayed(String titleID){
		String timePlayed = null;
		try{
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT timePlayed FROM local_roms WHERE titleID = '"+titleID+"';" );
			timePlayed = rs.getString(1);
			stmt.close();
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return timePlayed;
	}
	

}
