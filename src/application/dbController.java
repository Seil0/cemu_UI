/**
 * cemu_UI
 * 
 * Copyright 2017  <@Seil0>
 * 
 * "THE CHOCOLATE-WARE LICENSE" (Revision 1):
 * As long as you retain this notice this software is licensed under the GNU GENERAL PUBLIC LICENSE Version 3,
 * with the following additions:
 * If we meet some day, and you think this stuff is worth it,
 * you can buy me a chocolate in return. - @Seil0
 * (license based in Beer-ware, see https://fedoraproject.org/wiki/Licensing/Beerware )   
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final Logger LOGGER = LogManager.getLogger(dbController.class.getName());
	
	public void main(){
		LOGGER.info("<==========starting loading sql==========>");
		loadRomDatabase();
		loadGamesDatabase();
		createRomDatabase();
		loadAllRoms();
		checkRemoveEntry();
		LOGGER.info("<==========finished loading sql==========>");
	}
	
	private void loadRomDatabase(){
		if (System.getProperty("os.name").equals("Linux")) {
			DB_PATH = System.getProperty("user.home") + "/cemu_UI/localRoms.db";
		}else{
			DB_PATH = System.getProperty("user.home") + "\\Documents\\cemu_UI" + "\\" + "localRoms.db";
		}
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			connection.setAutoCommit(false);	//AutoCommit to false -> manual commit is active
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			LOGGER.error("error while loading the ROM database", e);
		}
		LOGGER.info("ROM database loaded successfull");
	}
	
	/**
	 * this method is used to load the games database with additional informations about a game
	 * it is used if a new game is added (automatic or manual)
	 */
	private void loadGamesDatabase(){
		if (System.getProperty("os.name").equals("Linux")) {
			DB_PATH_games = System.getProperty("user.home") + "/cemu_UI/games.db";
		}else{
			DB_PATH_games = System.getProperty("user.home") + "\\Documents\\cemu_UI" + "\\" + "games.db";
		}
		try {
			// create a database connection
			connectionGames = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH_games);
			connectionGames.setAutoCommit(false);	//AutoCommit to false -> manual commit is active
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			LOGGER.error("error while loading the games database", e);
		}
		LOGGER.info("games database loaded successfull");
	}
	
	//creating database, if database has 0 entries search for all .rpx files in the roms directory and add them
	void createRomDatabase() { 
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("create table if not exists local_roms (title, coverPath, romPath, titleID, productCode, region, lastPlayed, timePlayed)");
			stmt.close();
			connection.commit();
		} catch (SQLException e) {
			LOGGER.error("error while creating ROM database", e);
		}
		
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms"); 
			while (rs.next()) { 
				entries.add(rs.getString(2));
			}
			stmt.close();
			rs.close();
		}catch (SQLException e){
			LOGGER.error("error while loading ROMs from ROM database, local_roms table", e);
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
		LOGGER.info("added \""+title+"\" to ROM database");
	}
	
	void removeRom(String titleID) throws SQLException{
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("delete from local_roms where titleID = '"+titleID+"'");
		connection.commit();
		stmt.close();
		LOGGER.info("removed \""+titleID+"\" from ROM database");
	}
	
	//load all ROMs on startup to the mainWindowController
	void loadAllRoms(){
		LOGGER.info("loading all rom's on startup into the mainWindowController ...");
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms"); 
			while (rs.next()) {
				mainWindowController.addGame(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
			stmt.close();
			rs.close();
		}catch (Exception e){
			LOGGER.error("error while loading all ROMs into the mainWindowController", e);
		}
	}
	
	//load one single ROM after manual adding into the mainWindowController
	void loadSingleRom(String titleID){
		LOGGER.info("loading a single ROM (ID: "+titleID+") into the mainWindowController ..."); 
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM local_roms where titleID = '"+titleID+"'"); 
			while (rs.next()) {
				mainWindowController.addGame(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
			stmt.close();
			rs.close();
		}catch (Exception e){
			LOGGER.error("error while loading a single ROM into the mainWindowController", e);
		}
	}
	
	//get all files with .rpx TODO add other formats
	void loadRomDirectory(String directory){
		File dir = new File(directory);
		File appFile;
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
			LOGGER.info("Getting all .rpx files in " + dir.getCanonicalPath()+" including those in subdirectories");
			List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			for (File file : files) {
				if(System.getProperty("os.name").equals("Linux")){
					appFile = new File(file.getParent()+"/app.xml");
				} else {
					appFile = new File(file.getParent()+"\\app.xml");
				}
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(appFile);
				String title_ID = document.getElementsByTagName("title_id").item(0).getTextContent();
				title_ID = title_ID.substring(0, 8) + "-" + title_ID.substring(8, title_ID.length());
				LOGGER.info("Name: "+file.getName()+"; Title ID: "+title_ID);
				ResultSet rs = stmt.executeQuery("SELECT * FROM games WHERE TitleID = '"+title_ID+"';");
				while (rs.next()) {
					if (checkEntry(rs.getString(2))) {
						LOGGER.info(rs.getString(2) + ": game already in database");
					}else{
						LOGGER.info("adding cover to cache ...");
						BufferedImage originalImage = ImageIO.read(new URL(rs.getString(6)));//change path to where file is located
					    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
					    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 400, 600);
					    if(System.getProperty("os.name").equals("Linux")) {
						    ImageIO.write(resizeImagePNG, "png", new File(pictureCache+"/"+rs.getString(3)+".png")); //change path where you want it saved
						    coverPath = pictureCache+"/"+rs.getString(3)+".png";
					    } else {
						    ImageIO.write(resizeImagePNG, "png", new File(pictureCache+"\\"+rs.getString(3)+".png")); //change path where you want it saved
						    coverPath = pictureCache+"\\"+rs.getString(3)+".png";
					    }
					    
						LOGGER.info(rs.getString(2) + ": adding ROM");
						addRom(rs.getString(2), coverPath, file.getCanonicalPath(), rs.getString(1), rs.getString(3), rs.getString(5),"","0");
					}
				}
			}
		} catch (IOException | SQLException | ParserConfigurationException | SAXException e) {
			LOGGER.error("error while loading ROMs from directory", e);
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
		/**
		 *  TODO needs to be implemented!
		 *  don't show ROM on the UI, but keep all parameter in case it's showing up again ask if old data should be used
		 */
		//LOGGER.info("check if entry removed not done yet!");
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
			LOGGER.error("failed to set the last played", e);
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
			LOGGER.error("failed to get the last played", e);
		}
		return lastPlayed;
	}
	
	void setTotalPlaytime(String timePlayed, String titleID){
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE local_roms SET timePlayed='"+timePlayed+"' WHERE titleID = '"+titleID+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			LOGGER.error("failed to set total play time", e);
			e.printStackTrace();
		}
	}
	
	String getTotalPlaytime(String titleID){
		String timePlayed = null;
		try{
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT timePlayed FROM local_roms WHERE titleID = '"+titleID+"';" );
			timePlayed = rs.getString(1);
			stmt.close();
			rs.close();
		}catch(SQLException e){
			LOGGER.error("failed to get total play time", e);
		}
		return timePlayed;
	}
	

}
