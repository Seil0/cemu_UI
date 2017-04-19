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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Properties;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MainWindowController {
    
    @FXML
    private JFXButton aboutBtn;

    @FXML
    private JFXButton settingsBtn;
    
    @FXML
    private JFXButton addBtn;
    
    @FXML
    private JFXButton reloadRomsBtn;
    
    @FXML
    private JFXButton cemuTFBtn;
    
    @FXML
    private JFXButton romTFBtn;
    
    @FXML
    private JFXButton playBtn;
    
    @FXML
    JFXButton timePlayedBtn;
    
    @FXML
    private JFXButton lastTimePlayedBtn;
    
    @FXML
    private JFXHamburger menuHam;
    
    @FXML
    private JFXTextField cemuTextField;
    
    @FXML
    private JFXTextField romTextField;
    
    @FXML
    private JFXColorPicker colorPicker;
    
    @FXML
    private JFXToggleButton fullscreenToggleBtn;
    
    @FXML
    private JFXToggleButton cloudSyncToggleBtn;

    @FXML
    private AnchorPane settingsAnchorPane;
    
    @FXML
    private AnchorPane gamesAnchorPane;
    
    @FXML
    private ScrollPane scrollPaneMain;
    
    @FXML
    private VBox sideMenuVBox;
    
    @FXML
    private HBox topHBox;
    
    Main main;
    dbController dbController;
    playGame playGame;
    private boolean menuTrue = false;
    private boolean settingsTrue = false;
    private boolean playTrue = false;
    private boolean fullscreen;
    private boolean cloudSync;
    private String cloudService = "GoogleDrive"; //set cloud provider (at the moment only GoogleDrive, Dropbox is planed)
    private String cemuPath;
    private String romPath;
    private String gameExecutePath;
    private String selectedGameTitleID;
    private String selectedGameTitle;
    private String color;
    private String version = "0.1.4";
    private String buildNumber = "007";
	@SuppressWarnings("unused")
	private String versionName = "";
    private int xPos = -200;
    private int yPos = 17;
    private int xPosHelper;
    private int selectedUIDataIndex;
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/cemu_UI");
	private File dirLinux = new File(System.getProperty("user.home") + "/cemu_UI");
	private File fileWin = new File(dirWin + "/config.xml");
	private File fileLinux = new File(dirLinux + "/config.xml");
	File pictureCacheWin = new File(dirWin+"/picture_cache");
	File pictureCacheLinux = new File(dirLinux+"/picture_cache");
	private ObservableList<uiDataType> games = FXCollections.observableArrayList();
    Properties props = new Properties();
    Properties gameProps = new Properties();
    private MenuItem edit = new MenuItem("edit");
    private MenuItem remove = new MenuItem("remove");
    private MenuItem update = new MenuItem("update");
    private MenuItem addDLC = new MenuItem("add DLC");
	private ContextMenu gameContextMenu = new ContextMenu(edit, remove, update, addDLC);
	private Label lastGameLabel = new Label();
	
	private ImageView add_circle_black = new ImageView(new Image("recources/icons/ic_add_circle_black_24dp_1x.png"));
	private ImageView info_black = new ImageView(new Image("recources/icons/ic_info_black_24dp_1x.png"));
	private ImageView settings_black = new ImageView(new Image("recources/icons/ic_settings_black_24dp_1x.png"));
	private ImageView cached_black = new ImageView(new Image("recources/icons/ic_cached_black_24dp_1x.png"));	
	private ImageView add_circle_white = new ImageView(new Image("recources/icons/ic_add_circle_white_24dp_1x.png"));
	private ImageView info_white = new ImageView(new Image("recources/icons/ic_info_white_24dp_1x.png"));
	private ImageView settings_white = new ImageView(new Image("recources/icons/ic_settings_white_24dp_1x.png"));
	private ImageView cached_white = new ImageView(new Image("recources/icons/ic_cached_white_24dp_1x.png"));
    
	public void setMain(Main main) {
		this.main = main;
		dbController = new dbController(this);
	}
	
	void initUI(){		
		cemuTextField.setText(cemuPath);
		romTextField.setText(romPath);
		colorPicker.setValue(Color.valueOf(getColor()));
		fullscreenToggleBtn.setSelected(isFullscreen());
		edit.setDisable(true);
		applyColor();
	}
	
	void initActions() {
		System.out.print("initializing Actions... ");
		
		HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(menuHam);
		menuHam.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			if(playTrue){
	    		playBtnSlideOut();
	    	}
	    	if(menuTrue == false){
				sideMenuSlideIn();
				burgerTask.setRate(1.0);
				burgerTask.play();
				menuTrue = true;
			}else{
				sideMenuSlideOut();
				burgerTask.setRate(-1.0);
				burgerTask.play();
				menuTrue = false;
			}
			if(settingsTrue == true){
				settingsAnchorPane.setVisible(false);
//				setPath(tfPath.getText());
				saveSettings();
				settingsTrue = false;
			}
		});
		
		edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	System.out.println("edit "+selectedGameTitleID);
            	if(selectedGameTitleID == null){
            		System.out.println("trying to edit null! null is not valid!");
	            	Alert alert = new Alert(AlertType.WARNING);
	            	alert.setTitle("edit");
	            	alert.setHeaderText("cemu_UI");
	            	alert.setContentText("please select a game, \""+selectedGameTitleID+"\" is not a valid type");
	            	alert.initOwner(main.primaryStage);
	            	alert.showAndWait();
            	}else{
            		System.out.println("show edit window TODO!"); //TODO 
            	}
            }
		});
		
		remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	System.out.println("remove "+selectedGameTitleID);
            	if(selectedGameTitleID == null){
            		System.out.println("trying to remove null! null is not valid!");
                	Alert alert = new Alert(AlertType.WARNING);
                	alert.setTitle("remove");
                	alert.setHeaderText("cemu_UI");
                	alert.setContentText("please select a game, \""+selectedGameTitleID+"\" is not a valid type");
                	
                	alert.initOwner(main.primaryStage);
                	alert.showAndWait();
            	}
            	else{
            		Alert alert = new Alert(AlertType.CONFIRMATION);
                	alert.setTitle("remove");
                	alert.setHeaderText("cemu_UI");
                	alert.setContentText("Are you sure you want to delete "+selectedGameTitle+" ?");
                	alert.initOwner(main.primaryStage);
                	
                	Optional<ButtonType> result = alert.showAndWait();
        			if (result.get() == ButtonType.OK){
    					try {
    						//remove all elements from gamesAnchorPane
	    					for(int i=0; i< games.size(); i++){
	    						gamesAnchorPane.getChildren().remove(games.get(i).getVBox());
	    					}
	    					//remove game from database
    						games.remove(selectedUIDataIndex);
							dbController.removeRom(selectedGameTitleID);
	    					gamesAnchorPane.getChildren().removeAll(games);
	    					//reset position
	    				    xPos = -200;
	    				    yPos = 17;
	    				    xPosHelper = 0;
	    					//add all games to gamesAnchorPane
	    					for(int i=0; i< games.size(); i++){
	    				    	generatePosition();
	    				    	games.get(i).getVBox().setLayoutX(getxPos());
	    				    	games.get(i).getVBox().setLayoutY(getyPos());
	    						gamesAnchorPane.getChildren().add(games.get(i).getVBox());
	    					}
						} catch (Exception e) {
							e.printStackTrace();
						}
        			}
            	}
            }
		});
		
		update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	String titleID = selectedGameTitleID;
            	String updatePath;
            	System.out.println("update: "+selectedGameTitleID);
            	if(selectedGameTitleID == null){
            		System.out.println("trying to update null! null is not valid!");
	            	Alert alert = new Alert(AlertType.WARNING);
	            	alert.setTitle("edit");
	            	alert.setHeaderText("cemu_UI");
	            	alert.setContentText("please select a game, \""+selectedGameTitleID+"\" is not a valid type");
	            	alert.initOwner(main.primaryStage);
	            	alert.showAndWait();
            	}else{
        			Alert updateAlert = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
        			updateAlert.setTitle("cemu_UI");
        			updateAlert.setHeaderText("update "+selectedGameTitle);
        			updateAlert.setContentText("pleas select the update root directory");
        			updateAlert.initOwner(main.primaryStage);

        			Optional<ButtonType> result = updateAlert.showAndWait();
        			if (result.get() == ButtonType.OK){
        				DirectoryChooser directoryChooser = new DirectoryChooser();
        	            File selectedDirecroty =  directoryChooser.showDialog(main.primaryStage);
        	            updatePath = selectedDirecroty.getAbsolutePath();
            			String[] parts = titleID.split("-");	//split string into 2 parts at "-"
            			
            			File srcDir = new File(updatePath);
            			File destDir = new File(cemuPath+"\\mlc01\\usr\\title\\"+parts[0]+"\\"+parts[1]);
            			
            			System.out.println(updatePath);
            			System.out.println(destDir.toString());

            			if(destDir.exists() != true){
            				destDir.mkdir();
            			}

        	            try {
        	            	System.out.println("copying files...");
        	            	playBtn.setText("updating...");
        	            	playBtn.setDisable(true);
							FileUtils.copyDirectory(srcDir, destDir);	//TODO progress indicator
							playBtn.setText("play");
							playBtn.setDisable(false);
							System.out.println("done!");
						} catch (IOException e) {
							e.printStackTrace();
						}
        			} else {
        				updatePath = null;
        			}
            	}
            }
		});
		
		addDLC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
             	String titleID = selectedGameTitleID;
            	String dlcPath;
            	
            	System.out.println("add DLC: "+selectedGameTitleID);
            	if(selectedGameTitleID == null){
            		System.out.println("trying to add a dlc to null! null is not valid!");
	            	Alert alert = new Alert(AlertType.WARNING);
	            	alert.setTitle("add DLC");
	            	alert.setHeaderText("cemu_UI");
	            	alert.setContentText("please select a game, \""+selectedGameTitleID+"\" is not a valid type");
	            	alert.initOwner(main.primaryStage);
	            	alert.showAndWait();
            	}else{
        			Alert updateAlert = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
        			updateAlert.setTitle("cemu_UI");
        			updateAlert.setHeaderText("add a DLC to "+selectedGameTitle);
        			updateAlert.setContentText("pleas select the DLC root directory");
        			updateAlert.initOwner(main.primaryStage);

        			Optional<ButtonType> result = updateAlert.showAndWait();
        			if (result.get() == ButtonType.OK){
        				DirectoryChooser directoryChooser = new DirectoryChooser();
        	            File selectedDirecroty =  directoryChooser.showDialog(main.primaryStage);
        	            dlcPath = selectedDirecroty.getAbsolutePath();
            			String[] parts = titleID.split("-");	//split string into 2 parts at "-"
            			
            			File srcDir = new File(dlcPath);
            			File destDir = new File(cemuPath+"\\mlc01\\usr\\title\\"+parts[0]+"\\"+parts[1]+"\\aoc");
            			
            			System.out.println(dlcPath);
            			System.out.println(destDir.toString());

            			if(destDir.exists() != true){
            				destDir.mkdir();
            			}

        	            try {
        	            	System.out.println("copying files...");
        	            	playBtn.setText("copying files...");
        	            	playBtn.setDisable(true);
							FileUtils.copyDirectory(srcDir, destDir);	//TODO progress indicator
							playBtn.setText("play");
							playBtn.setDisable(false);
							System.out.println("done!");
						} catch (IOException e) {
							e.printStackTrace();
						}
        			} else {
        				dlcPath = null;
        			}
            	}
		     }
		});
            
		gamesAnchorPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		     @Override
		     public void handle(MouseEvent event) {
		    	 if (playTrue) {
			    	 playBtnSlideOut();
				}
		     }
		});
		
		topHBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		     @Override
		     public void handle(MouseEvent event) {
		    	 if (playTrue) {
			    	 playBtnSlideOut();
				}
		     }
		});
		System.out.println("done!");
	}
    
    @FXML
    void detailsSlideoutBtnAction(ActionEvent event){
    	playBtnSlideOut();
    }
    
    @FXML
    void aboutBtnAction(){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("about");
    	alert.setHeaderText("cemu_UI");
    	alert.setContentText("cemu_UI by @Seil0 \npre release "+version+" ("+buildNumber+") \nwww.kellerkinder.xyz");
    	alert.initOwner(main.primaryStage);
    	alert.showAndWait();
    }

    @FXML
    void settingsBtnAction(ActionEvent event) {
    	if(settingsTrue == false){
    		settingsAnchorPane.setVisible(true);
    		settingsTrue = true;
    	}else{
    		settingsAnchorPane.setVisible(false);
    		saveSettings();
    		settingsTrue = false;
    	}
    }
    
    @FXML
    void reloadRomsBtnAction() throws IOException{
    	reloadRomsBtn.setText("reloading...");
    	dbController.loadRomDirectory(getRomPath()); //TODO own thread
    	Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
		System.exit(0);	//finishes itself
    }
    
    @FXML
    void playBtnAction(ActionEvent event) throws InterruptedException, IOException{
    	dbController.setLastPlayed(selectedGameTitleID);
    	playGame = new playGame(this,dbController);
    	
    	playGame.start();
    }
    
    @FXML
    void timePlayedBtnAction(ActionEvent event){
    	
    }
    
    @FXML
    void lastTimePlayedBtnAction(ActionEvent event){
    	
    }
    
    @FXML
    void cemuTFBtnAction(ActionEvent event) {
    	File cemuDirectory = directoryChooser.showDialog(main.primaryStage);
        if(cemuDirectory == null){
            System.out.println("No Directory selected");
        }else{
        	setCemuPath(cemuDirectory.getAbsolutePath());
        	saveSettings();
        	cemuTextField.setText(getCemuPath());
			try {
				Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				System.out.println("es ist ein Fehler aufgetreten");
				e.printStackTrace();
			}
        }
    }
    
    @FXML
    void romTFBtnAction(ActionEvent event) {
    	File romDirectory = directoryChooser.showDialog(main.primaryStage); 
        if(romDirectory == null){
            System.out.println("No Directory selected");
        }else{
        	setRomPath(romDirectory.getAbsolutePath());
        	saveSettings();
        	cemuTextField.setText(getCemuPath());
			try {
				Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				System.out.println("es ist ein Fehler aufgetreten");
				e.printStackTrace();
			}
        }
    }
    
    @FXML
    void cemuTextFieldAction(ActionEvent event){
    	setCemuPath(cemuTextField.getText());
		saveSettings();//TODO remove (only save on exit settings)
    }
    
    @FXML
    void romTextFieldAction(ActionEvent event){
    	setRomPath(romTextField.getText());
		saveSettings();//TODO remove (only save on exit settings)
    }
    
    @FXML
    void fullscreenToggleBtnAction(ActionEvent event){
    	if(fullscreen){
    		fullscreen = false;
    	}else{
    		fullscreen = true;
    	}
    	saveSettings();//TODO remove (only save on exit settings)
    }
    
    @FXML
    void cloudSyncToggleBtnAction(ActionEvent event){
    	if(cloudSync) {
    		cloudSync = false;
    	} else {
    		cloudSync = true;
    		main.cloudController.initializeConnection(getCloudService(), getCemuPath());
    		main.cloudController.sync(getCloudService(), getCemuPath());
    	}
    	saveSettings();//TODO remove (only save on exit settings)
    }
    
    @FXML
    void colorPickerAction(ActionEvent event){
    	editColor(colorPicker.getValue().toString());
    	applyColor();
    }
    
    @FXML
    void addBtnAction(ActionEvent event){
    	boolean exit = false;
    	String romPath = null;
    	String coverPath = null;
    	String coverName = null;
    	String title = null;
    	String titleID = null;
    	File pictureCache;
    	   	
    	TextInputDialog titleDialog = new TextInputDialog();
    	titleDialog.setTitle("cemu_UI");
    	titleDialog.setHeaderText("add new Game");
    	titleDialog.setContentText("Please enter the name of the game you want to add:");
    	titleDialog.initOwner(main.primaryStage);
    	
    	Optional<String> titleResult = titleDialog.showAndWait();
    	if (titleResult.isPresent()){
    		title = titleResult.get();
    	}else{
    		exit = true;
    	}
    	
    	if(exit == false){
        	TextInputDialog titleIDDialog = new TextInputDialog();
        	titleIDDialog.setTitle("cemu_UI");
        	titleIDDialog.setHeaderText("add new Game");
        	titleIDDialog.setContentText("Please enter the title-ID (12345678-12345678) \nof the game you want to add:");
        	titleIDDialog.initOwner(main.primaryStage);
        	
        	Optional<String> titleIDResult = titleIDDialog.showAndWait();
        	if (titleIDResult.isPresent()){
        		titleID = titleIDResult.get();
        	}else{
        		exit = true;
        	}
    	}

		if(exit == false){
			Alert romAlert = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
	    	romAlert.setTitle("cemu_UI");
	    	romAlert.setHeaderText("add new Game");
	    	romAlert.setContentText("Please select the .rpx file from the game you want to add.");
	    	romAlert.initOwner(main.primaryStage);

			Optional<ButtonType> result = romAlert.showAndWait();
			if (result.get() == ButtonType.OK){
				FileChooser directoryChooser = new FileChooser();
	            File selectedDirectory =  directoryChooser.showOpenDialog(main.primaryStage);
	            romPath = selectedDirectory.getAbsolutePath();
			} else {
				exit = true;
			}
		}
		
		if(exit == false){
			Alert coverAlert = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
			coverAlert.setTitle("cemu_UI");
			coverAlert.setHeaderText("add new Game");
			coverAlert.setContentText("Please select the cover for the game you want to add.");
			coverAlert.initOwner(main.primaryStage);

			Optional<ButtonType> coverResult = coverAlert.showAndWait();
			if (coverResult.get() == ButtonType.OK){
				FileChooser directoryChooser = new FileChooser();
	            File selectedDirectory =  directoryChooser.showOpenDialog(main.primaryStage);
	            coverPath = selectedDirectory.getAbsolutePath();
			} else {
				exit = true;
			}
		}
		
		/**
		 * if exit == true then don't add a rom
		 * else convert the cover to .png add copy it into the picture cache
		 * then add the rom to the local_roms database
		 */
		if(exit){
			System.out.println("No parameter set!");
		}else{
			coverName = new File(coverPath).getName();
			try	{
				if(System.getProperty("os.name").equals("Linux")){
					pictureCache = pictureCacheLinux;
				}else{
					pictureCache = pictureCacheWin;
				}
				
			    BufferedImage originalImage = ImageIO.read(new File(coverPath));//load cover
			    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 400, 600);
			    ImageIO.write(resizeImagePNG, "png", new File(pictureCache+"\\"+coverName)); //save image to pictureCache
			    coverPath = pictureCache+"\\"+coverName;
			} catch (IOException e) {
			    System.out.println("Ops something went wrong!");
			}
			
			try {	
				dbController.addRom(title, coverPath, romPath, titleID, "", "", "", "0");
				dbController.loadSingleRom(titleID);
			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    void addGame(String title, String coverPath, String romPath, String titleID){
    	ImageView imageView = new ImageView();
    	Label gameTitleLabel = new Label(title);
    	File coverFile = new File(coverPath);
    	VBox VBox = new VBox();
    	JFXButton gameBtn = new JFXButton();
    	Image coverImage = new Image(coverFile.toURI().toString());
    	
    	generatePosition();

    	VBox.setLayoutX(getxPos());
    	VBox.setLayoutY(getyPos());
    	VBox.getChildren().addAll(gameTitleLabel,gameBtn);	
    	gameTitleLabel.setMaxWidth(200);
    	gameTitleLabel.setPadding(new Insets(0,0,0,8));
    	gameTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
    	imageView.setImage(coverImage);
    	imageView.setFitHeight(300);
    	imageView.setFitWidth(200);
    	gameBtn.setGraphic(imageView);
    	gameBtn.setContextMenu(gameContextMenu);
    	gameBtn.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 3); ");
    	gameBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		     @Override
		     public void handle(MouseEvent event) {
            	System.out.println("selected: "+title+"; ID: "+titleID);
            	
            	for(int i=0; i<games.size(); i++){
            		if(games.get(i).getButton() == event.getSource()){
            			selectedUIDataIndex = i;
            		}
            	}
            	
            	gameExecutePath = romPath;
            	selectedGameTitleID = titleID;
            	selectedGameTitle = title;
            	
            	lastGameLabel.setStyle("-fx-underline: false;");
            	games.get(selectedUIDataIndex).getLabel().setStyle("-fx-underline: true;");
            	lastGameLabel = games.get(selectedUIDataIndex).getLabel();

            	if(dbController.getLastPlayed(titleID).equals("") || dbController.getLastPlayed(titleID).equals(null)){
            		lastTimePlayedBtn.setText("Last played, never");
            		timePlayedBtn.setText(dbController.getTimePlayed(titleID)+ " min");
            	}else{
                	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                	
                	int today = Integer.parseInt(dtf.format(LocalDate.now()).replaceAll("-", ""));
                	int yesterday  = Integer.parseInt(dtf.format(LocalDate.now().minusDays(1)).replaceAll("-", ""));
                	int lastday = Integer.parseInt(dbController.getLastPlayed(titleID).replaceAll("-", ""));
                	
                	if(today == lastday){
                		lastTimePlayedBtn.setText("Last played, today");
                	}else if(yesterday == lastday){
                		lastTimePlayedBtn.setText("Last played, yesterday");
                	}else{
                    	lastTimePlayedBtn.setText("Last played, "+dbController.getLastPlayed(titleID));
                	}
                	if(Integer.parseInt(dbController.getTimePlayed(titleID)) > 60){
                		int hoursPlayed = (int) Math.floor(Integer.parseInt(dbController.getTimePlayed(titleID))/60);
                		int minutesPlayed = Integer.parseInt(dbController.getTimePlayed(titleID))-60*hoursPlayed;
                		timePlayedBtn.setText(hoursPlayed+"h "+minutesPlayed+"min");
                	}else{
                	timePlayedBtn.setText(dbController.getTimePlayed(titleID)+ " min");
                	}
            	}

            	if(playTrue == false){
            		playBtnSlideIn();
            	}
            }	
        });

    	games.add(new uiDataType(VBox, gameTitleLabel, gameBtn, titleID, romPath));
    }
    
    void addUIData(){
    	for(int i=0; i<games.size(); i++){
    		gamesAnchorPane.getChildren().add(games.get(i).getVBox());
    	}
    }

    //TODO xPosHelper based on window with
    private void generatePosition(){
//    	System.out.println(main.primaryStage.getWidth());
    	if(xPosHelper == 4){
    		xPos = 17;
    		yPos = yPos + 345;		
    		xPosHelper = 1;
    	}else{
    		xPos = xPos + 217;
    		xPosHelper++;
    	}
    }
    
    private void applyColor(){
		String boxStyle = "-fx-background-color: #"+getColor()+";";
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: BLACK;";
		String btnStyleWhite = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: WHITE;";
		BigInteger icolor = new BigInteger(getColor(),16);
		BigInteger ccolor = new BigInteger("78909cff",16);
		getColor();
		
		sideMenuVBox.setStyle(boxStyle);
		topHBox.setStyle(boxStyle);
		cemuTextField.setFocusColor(Color.valueOf(getColor()));
		romTextField.setFocusColor(Color.valueOf(getColor()));
		
		if(icolor.compareTo(ccolor) == -1){
			aboutBtn.setStyle("-fx-text-fill: WHITE;");
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			addBtn.setStyle("-fx-text-fill: WHITE;");
			reloadRomsBtn.setStyle("-fx-text-fill: WHITE;");
			playBtn.setStyle("-fx-text-fill: WHITE; -fx-font-family: Roboto Medium;");
			cemuTFBtn.setStyle(btnStyleWhite);
			romTFBtn.setStyle(btnStyleWhite);
			playBtn.setStyle(btnStyleWhite);
			
			aboutBtn.setGraphic(info_white);
			settingsBtn.setGraphic(settings_white);
			addBtn.setGraphic(add_circle_white);
			reloadRomsBtn.setGraphic(cached_white);
			
			menuHam.getStyleClass().add("jfx-hamburgerW");
		}else{
			aboutBtn.setStyle("-fx-text-fill: BLACK;");
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			addBtn.setStyle("-fx-text-fill: BLACK;");
			reloadRomsBtn.setStyle("-fx-text-fill: BLACK;");
			playBtn.setStyle("-fx-text-fill: BLACK; -fx-font-family: Roboto Medium;");
			cemuTFBtn.setStyle(btnStyleBlack);
			romTFBtn.setStyle(btnStyleBlack);
			playBtn.setStyle(btnStyleBlack);
			
			aboutBtn.setGraphic(info_black);
			settingsBtn.setGraphic(settings_black);
			addBtn.setGraphic(add_circle_black);
			reloadRomsBtn.setGraphic(cached_black);
			
			menuHam.getStyleClass().add("jfx-hamburgerB");
		}

		for(int i=0; i<games.size(); i++){
			games.get(i).getButton().setRipplerFill(Paint.valueOf(getColor()));
		}
    }
		
    void saveSettings(){
    	System.out.print("saving Settings... ");
    		OutputStream outputStream;	//new output-stream
    		try {
    			props.setProperty("cemuPath", getCemuPath());
				props.setProperty("romPath", getRomPath());
				props.setProperty("color", getColor());
				props.setProperty("fullscreen", String.valueOf(isFullscreen()));
				props.setProperty("cloudSync", String.valueOf(cloudSync));
				props.setProperty("folderID", main.cloudController.getFolderID(getCloudService()));
    			if(System.getProperty("os.name").equals("Linux")){
    				outputStream = new FileOutputStream(fileLinux);
    			}else{
    				outputStream = new FileOutputStream(fileWin);
    			}
    			props.storeToXML(outputStream, "cemu_UI settings");	//write new .xml
    			outputStream.close();
    			System.out.println("done!");
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    }
    
    void loadSettings(){
    	System.out.print("loading settings... ");
		InputStream inputStream;
		try {
			if(System.getProperty("os.name").equals("Linux")){
				inputStream = new FileInputStream(fileLinux);
			}else{
				inputStream = new FileInputStream(fileWin);
			}
			props.loadFromXML(inputStream);	//new input-stream from .xml
			setCemuPath(props.getProperty("cemuPath"));
			setRomPath(props.getProperty("romPath"));
			setColor(props.getProperty("color"));
			setFullscreen(Boolean.parseBoolean(props.getProperty("fullscreen")));
			setCloudSync(Boolean.parseBoolean(props.getProperty("cloudSync")));
			main.cloudController.setFolderID(props.getProperty("folderID"), getCloudService());
			inputStream.close();
			System.out.println("done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void sideMenuSlideIn(){
		sideMenuVBox.setVisible(true);
		//fade in from 40% to 100% opacity in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(0.4);
		fadeTransition.setToValue(1.0);
		//slide in in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(-175);
		translateTransition.setToX(0);
		//in case both animations are used (add (fadeTransition, translateTransition) in the second line under this command)    
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);//(fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	private void sideMenuSlideOut(){
//		sideMenuVBox.setVisible(false);
		//fade out from 100% to 40% opacity in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.4);
		//slide out in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(0);
		translateTransition.setToX(-175);
		//in case both animations are used (add (fadeTransition, translateTransition) in the second line under this command)	    
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);//(fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	private void playBtnSlideIn(){
		playBtn.setVisible(true);
		lastTimePlayedBtn.setVisible(true);
		timePlayedBtn.setVisible(true);
		playTrue = true;
		
		TranslateTransition playBtnTransition = new TranslateTransition(Duration.millis(300), playBtn);
		playBtnTransition.setFromY(55);
		playBtnTransition.setToY(0);
		playBtnTransition.play();
		
		TranslateTransition lastTimePlayedBtnTransition = new TranslateTransition(Duration.millis(300), lastTimePlayedBtn);
		lastTimePlayedBtnTransition.setFromY(55);
		lastTimePlayedBtnTransition.setToY(0);
		lastTimePlayedBtnTransition.play();
		
		TranslateTransition timePlayedBtnTransition = new TranslateTransition(Duration.millis(300), timePlayedBtn);
		timePlayedBtnTransition.setFromY(55);
		timePlayedBtnTransition.setToY(0);
		timePlayedBtnTransition.play();
	}
	
	void playBtnSlideOut(){
		playTrue = false;
		TranslateTransition playBtnTransition = new TranslateTransition(Duration.millis(300), playBtn);
		playBtnTransition.setFromY(0);
		playBtnTransition.setToY(56);
		playBtnTransition.play();
		
		TranslateTransition lastTimePlayedBtnTransition = new TranslateTransition(Duration.millis(300), lastTimePlayedBtn);
		lastTimePlayedBtnTransition.setFromY(0);
		lastTimePlayedBtnTransition.setToY(56);
		lastTimePlayedBtnTransition.play();
		
		TranslateTransition timePlayedBtnTransition = new TranslateTransition(Duration.millis(300), timePlayedBtn);
		timePlayedBtnTransition.setFromY(0);
		timePlayedBtnTransition.setToY(56);
		timePlayedBtnTransition.play();
	}
	
	private void editColor(String input){
		StringBuilder sb = new StringBuilder(input);
		sb.delete(0, 2);
		this.color = sb.toString();
		saveSettings();
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int imgWidth, int imgHeigth) {
	    BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeigth, type);
	    Graphics2D g = resizedImage.createGraphics();
	    g.drawImage(originalImage, 0, 0, imgWidth, imgHeigth, null);
	    g.dispose();

	    return resizedImage;
	}

	public String getCemuPath() {
		return cemuPath;
	}

	public void setCemuPath(String cemuPath) {
		this.cemuPath = cemuPath;
	}
	
	public String getRomPath() {
		return romPath;
	}

	public void setRomPath(String romPath) {
		this.romPath = romPath;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getxPosHelper() {
		return xPosHelper;
	}

	public void setxPosHelper(int xPosHelper) {
		this.xPosHelper = xPosHelper;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isCloudSync() {
		return cloudSync;
	}

	public void setCloudSync(boolean cloudSync) {
		this.cloudSync = cloudSync;
	}

	public String getGameExecutePath() {
		return gameExecutePath;
	}

	public void setGameExecutePath(String gameExecutePath) {
		this.gameExecutePath = gameExecutePath;
	}

	public String getSelectedGameTitleID() {
		return selectedGameTitleID;
	}

	public void setSelectedGameTitleID(String selectedGameTitleID) {
		this.selectedGameTitleID = selectedGameTitleID;
	}

	public String getCloudService() {
		return cloudService;
	}

	public void setCloudService(String cloudService) {
		this.cloudService = cloudService;
	}

}
