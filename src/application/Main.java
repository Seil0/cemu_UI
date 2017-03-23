package application;
	
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	Stage primaryStage;
	private MainWindowController mainWindowController;
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/cemu_UI");	//Windows: C:/Users/"User"/Documents/HomeFlix
	private File dirLinux = new File(System.getProperty("user.home") + "/cemu_UI");	//Linux: /home/"User"/HomeFlix
	private File fileWin = new File(dirWin + "/config.xml");	//Windows: C:/Users/"User"/Documents/HomeFlix/config.xml
	private File fileLinux = new File(dirLinux + "/config.xml");	//Linux: /home/"User"/HomeFlix/config.xml
	private File pictureCacheWin = new File(dirWin+"/picture_cache");
	private File pictureCacheLinux = new File(dirLinux+"/picture_cache");
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		mainWindow();
	}
	
	private void mainWindow(){
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
			AnchorPane pane = loader.load();
			primaryStage.setMinHeight(600.00);
			primaryStage.setMinWidth(900.00);
			primaryStage.setResizable(false);
			primaryStage.setTitle("cemu_UI");
//			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/recources/Homeflix_Icon_64x64.png"))); //adds application icon

			mainWindowController = loader.getController();	//Link of FXMLController and controller class
			mainWindowController.setMain(this);	//call setMain
			
			//Linux					if directory exists -> check config.xml
			if(System.getProperty("os.name").equals("Linux")){
				if(dirLinux.exists() != true){
					dirLinux.mkdir();
					pictureCacheLinux.mkdir();
				}else if(fileLinux.exists() != true){
					firstStart();
					mainWindowController.setColor("00a8cc");
					mainWindowController.setxPosHelper(0);
					mainWindowController.saveSettings();
					Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
					System.exit(0);	//finishes itself
				}
				if(pictureCacheLinux.exists() != true){
					pictureCacheLinux.mkdir();
				}
			//windows
			}else{
				if(dirWin.exists() != true){
					dirWin.mkdir();
					pictureCacheWin.mkdir();
				}else if(fileWin.exists() != true){
					firstStart();
					mainWindowController.setColor("00a8cc");
					mainWindowController.setxPosHelper(0);
					mainWindowController.saveSettings();
					Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
					System.exit(0);	//finishes itself
				}
				if(pictureCacheWin.exists() != true){
					pictureCacheWin.mkdir();
				}
			}
			
			//TODO download games.db
			mainWindowController.loadSettings();
			
			mainWindowController.dbController.main();
			mainWindowController.dbController.loadRoms();
			
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
