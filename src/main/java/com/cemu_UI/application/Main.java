/**
 * cemu_UI
 * 
 * Copyright 2017-2018  <@Seil0>
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
package com.cemu_UI.application;
	
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

import com.cemu_UI.controller.CloudController;

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
	
	private Stage primaryStage;
	private MainWindowController mainWindowController;
	private CloudController cloudController;
	private AnchorPane pane;
	private Scene scene;
	private static String userHome = System.getProperty("user.home");
	private static String userName = System.getProperty("user.name");
	private static String osName = System.getProperty("os.name");
	private static String osArch = System.getProperty("os.arch");
	private static String osVers = System.getProperty("os.version");
	private static String javaVers = System.getProperty("java.version");
	private static String javaVend= System.getProperty("java.vendor");
	private String gamesDBdownloadURL = "https://github.com/Seil0/cemu_UI/raw/master/downloadContent/games.db";
	private static String dirCemuUI;
	private static File directory;
	private static File configFile;
	private static File gamesDBFile;
	private static File reference_gamesFile;
	private static File pictureCache;
    private static Logger LOGGER;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			LOGGER.info("OS: " + osName + " " + osVers + " " + osArch);
			LOGGER.info("Java: " + javaVend + " " + javaVers);
			LOGGER.info("User: " + userName + " " + userHome);
			
			this.primaryStage = primaryStage;
			mainWindowController = new MainWindowController(this);
			
			mainWindow();
			initActions();
		} catch (Exception e) {
			LOGGER.error("ooooops",e);
		}
		
	}
	
	private void mainWindow(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClassLoader.getSystemResource("fxml/MainWindow.fxml"));
			loader.setController(mainWindowController);
			pane = (AnchorPane) loader.load();
			primaryStage.setMinWidth(265.00);
			primaryStage.setMinHeight(425.00);
			primaryStage.setTitle("cemu_UI");
//			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream(""))); //adds application icon
			primaryStage.setOnCloseRequest(event -> System.exit(1));
			
			// generate window
			scene = new Scene(pane); // create new scene, append pane to scene
			scene.getStylesheets().add(Main.class.getResource("/css/MainWindows.css").toExternalForm());
			primaryStage.setScene(scene); // append scene to stage
			primaryStage.show(); // show stage
			
			cloudController = new CloudController(mainWindowController); // call cloudController constructor
			
			// startup checks
			// check if client_secret.json is present
			if (Main.class.getResourceAsStream("/client_secret.json") == null) {
				LOGGER.error("client_secret is missing!!!!!");
				
				Alert alert = new Alert(AlertType.ERROR);
		    	alert.setTitle("cemu_UI");
		    	alert.setHeaderText("Error");
		    	alert.setContentText("client_secret is missing! Please contact the maintainer. \nIf you compiled cemu_UI by yourself see: \nhttps://github.com/Seil0/cemu_UI/wiki/Documantation");
		    	alert.showAndWait();
			}

			if (!directory.exists()) {
				LOGGER.info("creating cemu_UI directory");
				directory.mkdir();
				pictureCache.mkdir();
			}
			
			if (!configFile.exists()) {
				LOGGER.info("firststart, setting default values");
				firstStart();
				mainWindowController.setColor("00a8cc");
				mainWindowController.setAutoUpdate(false);
				mainWindowController.setLanguage("en_US");
				mainWindowController.setLastLocalSync(0);
				mainWindowController.setxPosHelper(0);
				mainWindowController.saveSettings();
			}
			
			if (!pictureCache.exists()) {
				pictureCache.mkdir();
			}
			
			
			if (!reference_gamesFile.exists()) {
				if (gamesDBFile.exists()) {
					gamesDBFile.delete();
				}
				try {
					LOGGER.info("downloading ReferenceGameList.db... ");
					URL website = new URL(gamesDBdownloadURL);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(reference_gamesFile);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					LOGGER.info("finished downloading games.db");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// init here as it loads the games to the mwc and the gui, therefore the window must exist
			mainWindowController.init();
			mainWindowController.getDbController().init();
			
			// if cloud sync is activated start sync
			if(mainWindowController.isCloudSync()) {
				cloudController.initializeConnection(mainWindowController.getCloudService(), mainWindowController.getCemuPath());
				cloudController.sync(mainWindowController.getCloudService(), mainWindowController.getCemuPath(), directory.getPath());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		if (osName.contains("Windows")) {
			dirCemuUI = userHome + "/Documents/cemu_UI";
		} else {
			dirCemuUI = userHome + "/cemu_UI";
		}
		
		directory = new File(dirCemuUI);
		configFile = new File(dirCemuUI + "/config.xml");
		gamesDBFile = new File(dirCemuUI + "/games.db");
		reference_gamesFile = new File(dirCemuUI + "/reference_games.db");
		pictureCache= new File(dirCemuUI+"/picture_cache");
		
		// delete old log file and create new
		System.setProperty("logFilename", dirCemuUI + "/app.log");
		File logFile = new File(dirCemuUI + "/app.log");
		logFile.delete();
		LOGGER = LogManager.getLogger(Main.class.getName());
		launch(args);
	}
	
	private void firstStart() {
		Alert alert = new Alert(AlertType.CONFIRMATION); // new alert with file-chooser
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
		
		Alert alert2 = new Alert(AlertType.CONFIRMATION); // new alert with file-chooser
		alert2.setTitle("cemu_UI");
		alert2.setHeaderText("rom directory");
		alert2.setContentText("please select your rom directory");

		Optional<ButtonType> result2 = alert2.showAndWait();
		if (result2.get() == ButtonType.OK) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(primaryStage);
			mainWindowController.setRomDirectoryPath(selectedDirectory.getAbsolutePath());
		} else {
			mainWindowController.setRomDirectoryPath(null);
		}
	}
	
	private void initActions() {
		final ChangeListener<Number> widthListener = new ChangeListener<Number>() {

			final Timer timer = new Timer();
			TimerTask saveTask = null; // task to execute save operation
			final long delayTime = 500; // delay until the window size is saved, if the window is resized earlier it will be killed, default is 500ms
			
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
				int xPosHelperMax = (int) Math.floor((mainWindowController.getMainAnchorPane().getWidth() - 36) / 217);
				
				// call only if there is enough space for a new row
				if (mainWindowController.getOldXPosHelper() != xPosHelperMax) {
					mainWindowController.refreshUIData();
				}
				
				// if saveTask is already running kill it
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
			TimerTask saveTask = null; // task to execute save operation
			final long delayTime = 500; // delay until the window size is saved, if the window is resized earlier it will be killed, default is 500ms
			
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
		
		// add listener to primaryStage
		primaryStage.widthProperty().addListener(widthListener);
		primaryStage.heightProperty().addListener(heightListener);
		primaryStage.maximizedProperty().addListener(maximizeListener);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public CloudController getCloudController() {
		return cloudController;
	}

	public void setCloudController(CloudController cloudController) {
		this.cloudController = cloudController;
	}

	public AnchorPane getPane() {
		return pane;
	}

	public void setPane(AnchorPane pane) {
		this.pane = pane;
	}

	public File getDirectory() {
		return directory;
	}

	public File getConfigFile() {
		return configFile;
	}

	public File getGamesDBFile() {
		return gamesDBFile;
	}

	public File getReference_gamesFile() {
		return reference_gamesFile;
	}

	public File getPictureCache() {
		return pictureCache;
	}
}
