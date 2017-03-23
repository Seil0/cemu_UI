package application;
	
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

import javafx.application.Application;
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
	private MainWindowController mainWindowController;
	private String dirWin = System.getProperty("user.home") + "/Documents/cemu_UI";	//Windows: C:/Users/"User"/Documents/HomeFlix
	private String dirLinux = System.getProperty("user.home") + "/cemu_UI";	//Linux: /home/"User"/HomeFlix
	private String gamesDBdownloadURL = "https://github.com/Seil0/cemu_UI/raw/master/downloadContent/games.db";
	private File directory;
	private File configFile;
	private File gamesDBFile;
	@SuppressWarnings("unused")
	private File localDB;
	private File pictureCache;

	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		mainWindow();
	}
	
	private void mainWindow(){
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
			AnchorPane pane = loader.load();
			primaryStage.setResizable(false);
			primaryStage.setTitle("cemu_UI");
//			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/recources/Homeflix_Icon_64x64.png"))); //adds application icon

			mainWindowController = loader.getController();	//Link of FXMLController and controller class
			mainWindowController.setMain(this);	//call setMain
			
			//get os and the right paths
			if(System.getProperty("os.name").equals("Linux")){
				directory = new File(dirLinux);
				configFile = new File(dirLinux + "/config.xml");
				gamesDBFile = new File(dirLinux + "/games.db");
				localDB = new File(dirLinux+"/localRoms.db");
				pictureCache= new File(dirLinux+"/picture_cache");
			}else{
				directory = new File(dirWin);	
				configFile = new File(dirWin + "/config.xml");
				gamesDBFile = new File(dirWin + "/games.db");
				localDB = new File(dirWin+"/localRoms.db");
				pictureCache= new File(dirWin+"/picture_cache");
			}
			
			//startup checks
			System.out.println(directory.exists());
			System.out.println(configFile.exists());
			if(directory.exists() != true){
				System.out.println("mkdir all");
				directory.mkdir();
				pictureCache.mkdir();
			}
			
			if(configFile.exists() != true){
				System.out.println("firststart");
				firstStart();
				mainWindowController.setColor("00a8cc");
				mainWindowController.setxPosHelper(0);
				mainWindowController.saveSettings();
				Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
				System.exit(0);	//finishes itself
			}
			
			if(pictureCache.exists() != true){
				pictureCache.mkdir();
			}
			
			if(gamesDBFile.exists() != true){
				try {
					System.out.print("downloading games.db... ");
					URL website = new URL(gamesDBdownloadURL);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(gamesDBFile);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					System.out.println("done!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//loading settings and initialize UI
			mainWindowController.loadSettings();
			mainWindowController.dbController.main();
			mainWindowController.initActions();
			mainWindowController.initUI();
			
			Scene scene = new Scene(pane);	//create new scene, append pane to scene
			
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
		alert.setContentText("pleas select your cemu installation");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            mainWindowController.setCemuPath(selectedDirectory.getAbsolutePath());
            
		} else {
			mainWindowController.setCemuPath(null);
		}
		
		Alert alert2 = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
		alert2.setTitle("cemu_UI");
		alert2.setHeaderText("rom directory");
		alert2.setContentText("pleas select your rom directory");

		Optional<ButtonType> result2 = alert2.showAndWait();
		if (result2.get() == ButtonType.OK){
			DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
                mainWindowController.setRomPath(selectedDirectory.getAbsolutePath());
            
		} else {
			mainWindowController.setRomPath(null);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
