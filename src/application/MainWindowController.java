package application;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private JFXButton timePlayedBtn;
    
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
    private AnchorPane settingsAnchorPane;
    
    @FXML
    private AnchorPane gamesAnchorPane;
    
    @FXML
    private ScrollPane scrollPaneMain;
    
    @FXML
    private VBox sideMenuVBox;
    
    @FXML
    private HBox topHBox;
    
    private Main main;
    private boolean menuTrue = false;
    private boolean settingsTrue = false;
    private boolean playTrue = false;
    private boolean fullscreen;
    private String cemuPath;
    private String romPath;
    private String gameExecutePath;
    private String selectedGameTitleID;
    private String selectedGameTitle;
    private String color;
    private int xPos = -200;
    private int yPos = 17;
    private int xPosHelper;
	private FileChooser fileChooser = new FileChooser();
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/cemu_UI");
	private File dirLinux = new File(System.getProperty("user.home") + "/cemu_UI");
	private File fileWin = new File(dirWin + "/config.xml");
	private File fileLinux = new File(dirLinux + "/config.xml");
	File pictureCacheWin = new File(dirWin+"/picture_cache");
	File pictureCacheLinux = new File(dirLinux+"/picture_cache");
	private ArrayList<JFXButton> gameCover = new ArrayList<JFXButton>();
	private ArrayList<Label> gameLabel = new ArrayList<Label>();
	private ArrayList<VBox> gameVBox = new ArrayList<VBox>();
    Properties props = new Properties();
    Properties gameProps = new Properties();
    private MenuItem edit = new MenuItem("edit");
    private MenuItem remove = new MenuItem("remove");
    private MenuItem update = new MenuItem("update");
    private MenuItem addDLC = new MenuItem("add DLC");
	private ContextMenu gameContextMenu = new ContextMenu(edit, remove, update, addDLC);
	private MouseEvent selectedEvent;
	
	private Label lastGameLabel = new Label();
	
    dbController dbController;
    
	public void setMain(Main main) {
		this.main = main;
		dbController = new dbController(this);
	}
	
	void initUI(){
		cemuTextField.setText(cemuPath);
		romTextField.setText(romPath);
		colorPicker.setValue(Color.valueOf(getColor()));
		fullscreenToggleBtn.setSelected(isFullscreen());
		addDLC.setDisable(true);
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
            		int i = gameCover.indexOf((selectedEvent).getSource());
            		
            		Alert alert = new Alert(AlertType.CONFIRMATION);
                	alert.setTitle("remove");
                	alert.setHeaderText("cemu_UI");
                	alert.setContentText("Are you sure you want to delete "+selectedGameTitle+" ?");
                	alert.initOwner(main.primaryStage);
                	
                	Optional<ButtonType> result = alert.showAndWait();
        			if (result.get() == ButtonType.OK){
    					try {
    						gameVBox.remove(i);
        					gameCover.remove(i);
        					gameLabel.remove(i);
							dbController.removeRom(selectedGameTitleID);
	    					gamesAnchorPane.getChildren().remove(i);
	    					
	    					//TODO remove if animations are done
	    					Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
	    					System.exit(0);	//finishes itself
	    					
						} catch (SQLException | IOException e) {
							e.printStackTrace();
						}
        			}
            		
					//TODO nachrück animation
//					platz(i)/4 -> aufrunden = Reihe; plath(i)-(platz(i)/4 -> abrunden*4)
//					jetzt haben wir den platz des gelöschten elements und lönnen alle nachfolgenden nachrücken
//        			double a = 13;
//            		double b = 4;
//            		a = a/b;
//            		System.out.println(Math.ceil(a)); //aufrunden
//            		System.out.println(Math.floor(a));	//abrunden
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
            	System.out.println("add DLC");
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
    	alert.setContentText("cemu_UI by @Seil0 \npre release 0.1.1 \nwww.kellerkinder.xyz");
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
    	dbController.loadRomDirectory(getRomPath());
    	Runtime.getRuntime().exec("java -jar cemu_UI.jar");	//start again (preventing Bugs)
		System.exit(0);	//finishes itself
    }
    
    @FXML
    void playBtnAction(ActionEvent event) throws InterruptedException, IOException{
    	dbController.setLastPlayed(selectedGameTitleID);
    	long startTime;
    	long endTime;
    	int playedTime;
    	int timePlayed;
    	Process p;
    	
    	main.primaryStage.setIconified(true);
    	startTime = System.currentTimeMillis();
    	try{
    		
    		if(fullscreen){
    			p = Runtime.getRuntime().exec(getCemuPath()+"\\Cemu.exe -f -g \""+gameExecutePath+"\"");
    		}else{
    			p = Runtime.getRuntime().exec(getCemuPath()+"\\Cemu.exe -g \""+gameExecutePath+"\"");
    		}
    		
    		p.waitFor();
    		endTime = System.currentTimeMillis();
    		playedTime = (int)  Math.floor(((endTime - startTime)/1000/60));
    		System.out.println((endTime - startTime)/1000+"; "+(endTime - startTime)/1000/60+"; "+playedTime);
    		
    		
    		timePlayed = Integer.parseInt(dbController.getTimePlayed(selectedGameTitleID))+playedTime;
    		System.out.println(timePlayed);
    		
    		dbController.setTimePlayed(Integer.toString(timePlayed), selectedGameTitleID);
    		timePlayedBtn.setText(dbController.getTimePlayed(selectedGameTitleID)+ " min");
    		main.primaryStage.setIconified(false);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    @FXML
    void timePlayedBtnAction(ActionEvent event){
    	
    }
    
    @FXML
    void lastTimePlayedBtnAction(ActionEvent event){
    	
    }
    
    @FXML
    void cemuTFBtnAction(ActionEvent event) {
    	File cemuDirectory = fileChooser.showOpenDialog(main.primaryStage);
        if(cemuDirectory == null){
            System.out.println("No Directory selected");
        }else{
        	setCemuPath(cemuDirectory.getAbsolutePath());
        	saveSettings();
        	cemuTextField.setText(getCemuPath());
			try {
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
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
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
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
		saveSettings();
    }
    
    @FXML
    void romTextFieldAction(ActionEvent event){
    	setRomPath(romTextField.getText());
		saveSettings();
    }
    
    @FXML
    void fullscreenToggleBtnAction(ActionEvent event){
    	if(fullscreen){
    		fullscreen = false;
    	}else{
    		fullscreen = true;
    	}
    	saveSettings();
    }
    
    @FXML
    void colorPickerAction(ActionEvent event){
    	editColor(colorPicker.getValue().toString());
    	applyColor();
    }
    
    @FXML
    void addBtnAction(ActionEvent event){
    	String romPath = null;
    	String coverPath = null;
    	String coverName = null;
    	String title = null;
    	String titleID = null;
    	File pictureCache;
    	boolean exit = false;
    	   	
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

			        BufferedImage originalImage = ImageIO.read(new File(coverPath));//change path to where file is located
			        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			        BufferedImage resizeImagePNG = resizeImage(originalImage, type, 400, 600);
			        ImageIO.write(resizeImagePNG, "png", new File(pictureCache+"\\"+coverName)); //change path where you want it saved
			        coverPath = pictureCache+"\\"+coverName;
			} catch (IOException e) {
			    System.out.println("Ops something went wrong!");
			}
			
			try {
				
				dbController.addRom(title, coverPath, romPath, titleID, "", "", "", "");
				dbController.loadSingleRom(titleID);
			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    void addGame(String title, String coverPath, String romPath, String titleID){
    	ImageView imageView = new ImageView();	//TODO abgerundete Kanten,
    	Label gameTitleLabel = new Label(title);
    	File coverFile = new File(coverPath);
    	VBox VBox = new VBox();
    	JFXButton gameBtn = new JFXButton();
    	Image coverImage = new Image(coverFile.toURI().toString());
    	
    	generatePosition();
//    	System.out.println("Title: "+title+"; cover: "+coverPath+"; rom: "+romPath);
//    	System.out.println("X: "+getxPos()+"; Y: "+getyPos());
    	gameVBox.add(VBox);
    	gameCover.add(gameBtn);
    	gameLabel.add(gameTitleLabel);
    	gameTitleLabel.setMaxWidth(200);
    	imageView.setImage(coverImage);
    	imageView.setFitHeight(300);
    	imageView.setFitWidth(200);
    	gameBtn.setGraphic(imageView);
    	gameBtn.setContextMenu(gameContextMenu);	
    	gameBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		     @Override
		     public void handle(MouseEvent event) {
            	System.out.println("selected: "+title+"; ID: "+titleID);
            	
            	gameExecutePath = romPath;
            	selectedGameTitleID = titleID;
            	selectedGameTitle = title;
            	selectedEvent = event;
            	
            	lastGameLabel.setStyle("-fx-underline: false;");
            	gameLabel.get(gameCover.indexOf(event.getSource())).setStyle("-fx-underline: true;");
            	lastGameLabel = gameLabel.get(gameCover.indexOf(event.getSource()));
            	
            	if(dbController.getLastPlayed(titleID).equals("") || dbController.getLastPlayed(titleID).equals(null)){
            		lastTimePlayedBtn.setText("Last played, never");
            		timePlayedBtn.setText(dbController.getTimePlayed(titleID)+ " min");
            	}else{
                	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                	
                	int localInt = Integer.parseInt(dtf.format(LocalDate.now()).replaceAll("-", ""));
                	int lastInt = Integer.parseInt(dbController.getLastPlayed(titleID).replaceAll("-", ""));
                	
                	if(localInt == lastInt){
                		lastTimePlayedBtn.setText("Last played, today");
                	}else if(localInt-1 == lastInt){
                		lastTimePlayedBtn.setText("Last played, yesterday");
                	}else{
                    	lastTimePlayedBtn.setText("Last played, "+dbController.getLastPlayed(titleID));
                	}
                	
                	timePlayedBtn.setText(dbController.getTimePlayed(titleID)+ " min");
            	}

            	if(playTrue == false){
            		playBtnSlideIn();
            	}
            }	
        });
    	
    	gameTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
    	VBox.getChildren().addAll(gameTitleLabel,gameBtn);
    	VBox.setLayoutX(getxPos());
    	VBox.setLayoutY(getyPos());
    	
    	gamesAnchorPane.getChildren().add(VBox);
    }

    
    private void generatePosition(){
    	
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
		String style = "-fx-background-color: #"+getColor()+";";
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: BLACK;";
		String timeBtnStyle = "-fx-button-type: RAISED; -fx-background-color: #ffffff; -fx-text-fill: BLACK;";
		getColor();
		
		sideMenuVBox.setStyle(style);
		topHBox.setStyle(style);
		cemuTextField.setFocusColor(Color.valueOf(getColor()));
		romTextField.setFocusColor(Color.valueOf(getColor()));
		
		aboutBtn.setStyle("-fx-text-fill: BLACK;");
		settingsBtn.setStyle("-fx-text-fill: BLACK;");
		addBtn.setStyle("-fx-text-fill: BLACK;");
		reloadRomsBtn.setStyle("-fx-text-fill: BLACK;");
		playBtn.setStyle("-fx-text-fill: BLACK;");
		cemuTFBtn.setStyle(btnStyleBlack);
		romTFBtn.setStyle(btnStyleBlack);
		playBtn.setStyle(btnStyleBlack);
		
		lastTimePlayedBtn.setStyle(timeBtnStyle);
		timePlayedBtn.setStyle(timeBtnStyle);
    }
		
    void saveSettings(){
    	System.out.print("saving Settings... ");
    		OutputStream outputStream;	//new output-stream
    		try {
    			props.setProperty("cemuPath", getCemuPath());
				props.setProperty("romPath", getRomPath());
				props.setProperty("color", getColor());
				props.setProperty("fullscreen", String.valueOf(isFullscreen()));
    			if(System.getProperty("os.name").equals("Linux")){
    				outputStream = new FileOutputStream(fileLinux);
    			}else{
    				outputStream = new FileOutputStream(fileWin);
    			}
    			props.storeToXML(outputStream, "cemu_UI settings");	//writes new .xml
    			outputStream.close();
    			System.out.println("done!");
    		} catch (IOException e) {
//    			if(firststart == false){
//    				showErrorMsg(errorLoad, e);
    				e.printStackTrace();
//    			}
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
			inputStream.close();
			System.out.println("done!");
		} catch (IOException e) {
//			if(firststart == false){
//				showErrorMsg(errorSave, e);
				e.printStackTrace();
//			}

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
		translateTransition.setFromX(-150);
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
		translateTransition.setToX(-150);
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
	
	private void playBtnSlideOut(){
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
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
	    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
	    Graphics2D g = resizedImage.createGraphics();
	    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
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

}
