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

package application;
	
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	Stage primaryStage;
	public MainWindowController mainWindowController; //TODO find a better way
	CloudController cloudController;
	AnchorPane pane;
	private Scene scene;
	private String dirWin = System.getProperty("user.home") + "/Documents/cemu_UI";	//Windows: C:/Users/"User"/Documents/cemu_UI
	private String dirLinux = System.getProperty("user.home") + "/cemu_UI";	//Linux: /home/"User"/cemu_UI
	private String gamesDBdownloadURL = "https://github.com/Seil0/cemu_UI/raw/master/downloadContent/games.db";
	private File directory;
	private File configFile;
	private File gamesDBFile;
	@SuppressWarnings("unused")
	private File localDB;
	private File pictureCache;
    private static Logger LOGGER;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		cloudController = new CloudController(this);
		mainWindow();
		initActions();
	}
	
	private void mainWindow(){
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
			pane = loader.load();
//			primaryStage.setResizable(false);
			primaryStage.setTitle("cemu_UI");
//			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/Homeflix_Icon_64x64.png"))); //adds application icon

			mainWindowController = loader.getController();	//Link of FXMLController and controller class
			mainWindowController.setMain(this);	//call setMain
			
			//get os and the right paths
			if (System.getProperty("os.name").equals("Linux")) {
				directory = new File(dirLinux);
				configFile = new File(dirLinux + "/config.xml");
				gamesDBFile = new File(dirLinux + "/games.db");
				localDB = new File(dirLinux+"/localRoms.db");
				pictureCache= new File(dirLinux+"/picture_cache");
			} else {
				directory = new File(dirWin);
				configFile = new File(dirWin + "/config.xml");
				gamesDBFile = new File(dirWin + "/games.db");
				localDB = new File(dirWin+"/localRoms.db");
				pictureCache= new File(dirWin+"/picture_cache");
			}
			
			//startup checks
			//check if client_secret.jason is present
			if (Main.class.getResourceAsStream("/resources/client_secret.json") == null) {
				LOGGER.error("client_secret is missing!!!!!");
				
				Alert alert = new Alert(AlertType.ERROR);
		    	alert.setTitle("cemu_UI");
		    	alert.setHeaderText("Error");
		    	alert.setContentText("client_secret is missing! Please contact the maintainer. \nIf you compiled cemu_UI by yourself see: \nhttps://github.com/Seil0/cemu_UI/wiki/Documantation");
		    	alert.showAndWait();
			}

			LOGGER.info("Directory: " + directory.exists());
			LOGGER.info("Configfile: " + configFile.exists());
			if (!directory.exists()) {
				LOGGER.info("creating cemu_UI directory");
				directory.mkdir();
				pictureCache.mkdir();
			}
			
			if (!configFile.exists()) {
				LOGGER.info("firststart, setting default values");
				firstStart();
				mainWindowController.setColor("00a8cc");
				mainWindowController.setxPosHelper(0);
				mainWindowController.saveSettings();
				Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
				System.exit(0);	//finishes itself
			}
			
			if (pictureCache.exists() != true) {
				pictureCache.mkdir();
			}
			
			if (gamesDBFile.exists() != true) {
				try {
					LOGGER.info("downloading games.db... ");
					URL website = new URL(gamesDBdownloadURL);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(gamesDBFile);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					LOGGER.info("finished downloading games.db");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//loading settings and initialize UI, dbController.main() loads all databases
			mainWindowController.loadSettings();
			mainWindowController.initActions();
			mainWindowController.initUI();
			mainWindowController.dbController.main();
			if(mainWindowController.isCloudSync()) {
				cloudController.initializeConnection(mainWindowController.getCloudService(), mainWindowController.getCemuPath());
				cloudController.stratupCheck(mainWindowController.getCloudService(), mainWindowController.getCemuPath());
			}			
			mainWindowController.addUIData();
			
			scene = new Scene(pane);	//create new scene, append pane to scene
			scene.getStylesheets().add(Main.class.getResource("MainWindows.css").toExternalForm());
			primaryStage.setScene(scene);	//append scene to stage
			primaryStage.show();	//show stage
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void firstStart(){
		Alert alert = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
		alert.setTitle("cemu_UI");
		alert.setHeaderText("cemu installation");
		alert.setContentText("please select your cemu installation");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            mainWindowController.setCemuPath(selectedDirectory.getAbsolutePath());
            
		} else {
			mainWindowController.setCemuPath(null);
		}
		
		Alert alert2 = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
		alert2.setTitle("cemu_UI");
		alert2.setHeaderText("rom directory");
		alert2.setContentText("please select your rom directory");

		Optional<ButtonType> result2 = alert2.showAndWait();
		if (result2.get() == ButtonType.OK) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
                mainWindowController.setRomPath(selectedDirectory.getAbsolutePath());
            
		} else {
			mainWindowController.setRomPath(null);
		}
	}
	
	private void initActions() {
		final ChangeListener<Number> widthListener = new ChangeListener<Number>() {

			final Timer timer = new Timer();
			TimerTask saveTask = null; //task to execute save operation
			final long delayTime = 500; //delay until the window size is saved, if the window is resized earlier it will be killed, default is 500ms
			
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
				int xPosHelperMax = (int) Math.floor((mainWindowController.getMainAnchorPane().getWidth() - 36) / 217);

				mainWindowController.refreshplayBtnPosition();
				
				//call only if there is enough space for a new row
				if (mainWindowController.getOldXPosHelper() != xPosHelperMax) {
					mainWindowController.refreshUIData();
				}
				
				//if saveTask is already running kill it
				if (saveTask != null) saveTask.cancel();

				saveTask = new TimerTask() {
					@Override
				    public void run() { 
						mainWindowController.saveSettings();
					}
				};
				timer.schedule(saveTask, delayTime);
			}
		};
		
		final ChangeListener<Number> heightListener = new ChangeListener<Number>() {
			
			final Timer timer = new Timer();
			TimerTask saveTask = null; //task to execute save operation
			final long delayTime = 500; //delay until the window size is saved, if the window is resized earlier it will be killed, default is 500ms
			
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
				
				if (saveTask != null) saveTask.cancel();
				
				saveTask = new TimerTask() {
					@Override
				    public void run() { 
						mainWindowController.saveSettings();
					}
				};
				timer.schedule(saveTask, delayTime);
			}
		};
		
		final ChangeListener<Boolean> maximizeListener = new ChangeListener<Boolean>() {
			
		    @Override
		    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
            	primaryStage.setMaximized(false);
		    	
		    	Alert alert = new Alert(AlertType.WARNING);
            	alert.setTitle("edit");
            	alert.setHeaderText("cemu_UI");
            	alert.setContentText("maximized Window is not supporte!");
            	alert.initOwner(primaryStage);
            	alert.showAndWait();
            	
		    	LOGGER.warn("maximized Window is not supported");
		    }
		};
		
		//add listener to primaryStage
		primaryStage.widthProperty().addListener(widthListener);
		primaryStage.heightProperty().addListener(heightListener);
		primaryStage.maximizedProperty().addListener(maximizeListener);
	}
	
	public static void main(String[] args) {
		//delete old log file and create new
		if(System.getProperty("os.name").equals("Linux")){
			System.setProperty("logFilename", System.getProperty("user.home") + "/cemu_UI/app.log");
			File logFile = new File(System.getProperty("user.home") + "/cemu_UI/app.log");
			logFile.delete();
		}else{
			System.setProperty("logFilename", System.getProperty("user.home") + "/Documents/cemu_UI/app.log");
			File logFile = new File(System.getProperty("user.home") + "/Documents/cemu_UI/app.log");
			logFile.delete();
		}
		LOGGER = LogManager.getLogger(Main.class.getName());
		launch(args);
	}
}
