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

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import datatypes.CourseTableDataType;
import datatypes.SmmdbApiDataType;
import datatypes.UIROMDataType;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

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
    private JFXButton smmdbBtn;
    
    @FXML
    private JFXButton cemuTFBtn;
    
    @FXML
    private JFXButton romTFBtn;
    
    @FXML
    private JFXButton smmdbDownloadBtn;
    
    @FXML
    private JFXButton playBtn;
    
    @FXML
    private JFXButton lastTimePlayedBtn;
    
    @FXML
    JFXButton totalPlaytimeBtn;
    
    @FXML
    private JFXHamburger menuHam;
    
    @FXML
    private JFXTextField cemuTextField;
    
    @FXML
    private JFXTextField romTextField;
    
    @FXML
    private TextFlow smmdbTextFlow;
    
    @FXML
    private JFXColorPicker colorPicker;
    
    @FXML
    private JFXToggleButton fullscreenToggleBtn;
    
    @FXML
    private JFXToggleButton cloudSyncToggleBtn;

    @FXML
    private AnchorPane settingsAnchorPane;
    
    @FXML
    private AnchorPane smmdbAnchorPane;
    
    @FXML
    private AnchorPane gamesAnchorPane;
    
    @FXML
    private AnchorPane mainAnchorPane;
    
    @FXML
    private ScrollPane scrollPaneMain;
    
    @FXML
    private ScrollPane smmdbScrollPane;
    
    @FXML
    private VBox sideMenuVBox;
    
    @FXML
    private HBox topHBox;
    
    @FXML
    private ImageView smmdbImageView;
    
    @FXML
    private Label helpLabel;
    
    
    @FXML
    private JFXTreeTableView<CourseTableDataType> courseTreeTable = new JFXTreeTableView<CourseTableDataType>();
    
    @FXML
    TreeItem<CourseTableDataType> root = new TreeItem<>(new CourseTableDataType("","",0,0));
    
    @FXML
    private JFXTreeTableColumn<CourseTableDataType, String> titleColumn = new JFXTreeTableColumn<>("title");
    
    @FXML
    private JFXTreeTableColumn<CourseTableDataType, String> idColumn = new JFXTreeTableColumn<>("id");
    
    @FXML
    private JFXTreeTableColumn<CourseTableDataType, Integer> starsColumn = new JFXTreeTableColumn<>("stars");
    
    @FXML
    private JFXTreeTableColumn<CourseTableDataType, Integer> timeColumn = new JFXTreeTableColumn<>("time");
    
    Main main;
    dbController dbController;
    SmmdbApiQuery smmdbApiQuery;
    playGame playGame;
    private boolean menuTrue = false;
    private boolean settingsTrue = false;
    private boolean playTrue = false;
    private boolean smmdbTrue = false;
    private boolean fullscreen;
    private boolean cloudSync;
    private String cloudService = ""; //set cloud provider (at the moment only GoogleDrive, Dropbox is planed)
    private String cemuPath;
    private String romPath;
    private String gameExecutePath;
    private String color;
    private String dialogBtnStyle;
    private String selectedGameTitleID;
    private String selectedGameTitle;
    private String id;
    private String version = "0.1.7";
    private String buildNumber = "041";
    private String versionName = "Throwback Galaxy";
    private int xPos = -200;
    private int yPos = 17;
    private int xPosHelper;
    private int selectedUIDataIndex;
    private int selected;
    private double windowWidth;
    private double windowHeight;
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/cemu_UI");
	private File dirLinux = new File(System.getProperty("user.home") + "/cemu_UI");
	private File configFileWin = new File(dirWin + "/config.xml");
	private File configFileLinux = new File(dirLinux + "/config.xml");
	File pictureCacheWin = new File(dirWin+"/picture_cache");
	File pictureCacheLinux = new File(dirLinux+"/picture_cache");
	private ObservableList<String> smmIDs = FXCollections.observableArrayList("fe31b7f2", "44fc5929");	//TODO add more IDs
	private ObservableList<UIROMDataType> games = FXCollections.observableArrayList();
	ObservableList<SmmdbApiDataType> courses = FXCollections.observableArrayList();
	ArrayList<Text> courseText = new ArrayList<Text>();
	ArrayList<Text> nameText = new ArrayList<Text>();
    Properties props = new Properties();
    Properties gameProps = new Properties();
    private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
    private MenuItem edit = new MenuItem("edit");
    private MenuItem remove = new MenuItem("remove");
    private MenuItem update = new MenuItem("update");
    private MenuItem addDLC = new MenuItem("add DLC");
	private ContextMenu gameContextMenu = new ContextMenu(edit, remove, update, addDLC);
	private Label lastGameLabel = new Label();
	
	private ImageView add_circle_black = new ImageView(new Image("resources/icons/ic_add_circle_black_24dp_1x.png"));
	private ImageView info_black = new ImageView(new Image("resources/icons/ic_info_black_24dp_1x.png"));
	private ImageView settings_black = new ImageView(new Image("resources/icons/ic_settings_black_24dp_1x.png"));
	private ImageView cached_black = new ImageView(new Image("resources/icons/ic_cached_black_24dp_1x.png"));	
	private ImageView smmdb_black = new ImageView(new Image("resources/icons/ic_get_app_black_24dp_1x.png"));
	private ImageView add_circle_white = new ImageView(new Image("resources/icons/ic_add_circle_white_24dp_1x.png"));
	private ImageView info_white = new ImageView(new Image("resources/icons/ic_info_white_24dp_1x.png"));
	private ImageView settings_white = new ImageView(new Image("resources/icons/ic_settings_white_24dp_1x.png"));
	private ImageView cached_white = new ImageView(new Image("resources/icons/ic_cached_white_24dp_1x.png"));
	private ImageView smmdb_white = new ImageView(new Image("resources/icons/ic_get_app_white_24dp_1x.png"));
	private Image close_black = new Image("resources/icons/close_black_2048x2048.png");
    
	public void setMain(Main main) {
		this.main = main;
		dbController = new dbController(this);
		smmdbApiQuery = new SmmdbApiQuery();
	}
	
	void initUI() {
		System.out.println(getWindowWidth());
		if (getWindowWidth() > 100 && getWindowHeight() > 100) {
			mainAnchorPane.setPrefSize(getWindowWidth(), getWindowHeight());	
		}
		refreshplayBtnPosition();
		
		cemuTextField.setText(cemuPath);
		romTextField.setText(romPath);
		colorPicker.setValue(Color.valueOf(getColor()));
		fullscreenToggleBtn.setSelected(isFullscreen());
		cloudSyncToggleBtn.setSelected(isCloudSync());
		edit.setDisable(true);
		applyColor();
		
		//initialize courseTable
		titleColumn.setPrefWidth(160);
		timeColumn.setPrefWidth(127);
		starsColumn.setPrefWidth(100);
		
		courseTreeTable.setRoot(root);
		courseTreeTable.setShowRoot(false);
		courseTreeTable.setEditable(false);
		
		titleColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().title);
		idColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().id);
		starsColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().stars.asObject());
		timeColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().time.asObject());
		
		courseTreeTable.getColumns().add(titleColumn);
		courseTreeTable.getColumns().add(timeColumn);
		courseTreeTable.getColumns().add(starsColumn);
		courseTreeTable.getColumns().add(idColumn);
		courseTreeTable.getColumns().get(3).setVisible(false); //hide idColumn (important)
	}
	
	/**
	 * initialize all actions not initialized by a own method
	 */
	void initActions() {
		LOGGER.info("initializing Actions ...");
		
		HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(menuHam);
		menuHam.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			if (playTrue) {
	    		playBtnSlideOut();
	    	}
	    	if (menuTrue){ 
				sideMenuSlideOut();
				burgerTask.setRate(-1.0);
				burgerTask.play();
				menuTrue = false;
			}else{
				sideMenuSlideIn();
				burgerTask.setRate(1.0);
				burgerTask.play();
				menuTrue = true;
			}
			if (settingsTrue) {
				settingsAnchorPane.setVisible(false);
//				setPath(tfPath.getText());
				saveSettings();
				settingsTrue = false;
			}
			if (smmdbTrue) {
				smmdbAnchorPane.setVisible(false);
				smmdbTrue = false;
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
            		System.out.println("show edit window TODO!"); //TODO show edit window
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
    						//remove game from database
    						games.remove(selectedUIDataIndex);
							dbController.removeRom(selectedGameTitleID);
							
    						//refresh all games at gamesAnchorPane (UI)
    						refreshUIData();  						
						} catch (Exception e) {
							LOGGER.error("error!",e);
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
            		LOGGER.error("trying to update null! null is not valid!");
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
            			
            			LOGGER.info(updatePath);
            			LOGGER.info(destDir.toString());

            			if(destDir.exists() != true){
            				destDir.mkdir();
            			}

        	            try {
        	            	LOGGER.info("copying files...");
        	            	playBtn.setText("updating...");
        	            	playBtn.setDisable(true);
							FileUtils.copyDirectory(srcDir, destDir);	//TODO progress indicator
							playBtn.setText("play");
							playBtn.setDisable(false);
							LOGGER.info("copying files done!");
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
            	
            	LOGGER.info("add DLC: "+selectedGameTitleID);
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
            			
            			LOGGER.info(dlcPath);
            			LOGGER.info(destDir.toString());

            			if(destDir.exists() != true){
            				destDir.mkdir();
            			}

        	            try {
        	            	LOGGER.info("copying files...");
        	            	playBtn.setText("copying files...");
        	            	playBtn.setDisable(true);
							FileUtils.copyDirectory(srcDir, destDir);	//TODO progress indicator
							playBtn.setText("play");
							playBtn.setDisable(false);
							LOGGER.info("copying files done!");
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
		
		//Change-listener for TreeTable
		courseTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {	
				@Override
				public void changed(ObservableValue<?> observable, Object oldVal, Object newVal){
					selected = courseTreeTable.getSelectionModel().getSelectedIndex(); //get selected item
					
					//FIXME if a item is selected and you change the sorting,you can't select a new item
					id = idColumn.getCellData(selected); //get name of selected item

					for (int i = 0; i < courses.size(); i++) {
						if (courses.get(i).getId() == id) {	
							try {
								URL url = new URL("http://smmdb.ddns.net/courseimg/" + id + "_full.jpg?v=3");
								Image image = new Image(url.toURI().toString());
								smmdbImageView.setImage(image);
							} catch (MalformedURLException | URISyntaxException e) {
								e.printStackTrace();
								smmdbImageView.setImage(close_black);
							}
							addCourseDescription(courses.get(i));
						}
					}	
				}
		 });
		
		helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            try {
						Desktop.getDesktop().browse(new URI("https://github.com/Seil0/cemu_UI/issues/3"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
		        }
		    }
		});
		LOGGER.info("initializing Actions done!");
	}
    
    @FXML
    void detailsSlideoutBtnAction(ActionEvent event){
    	playBtnSlideOut();
    }
    
    @FXML
    void aboutBtnAction(){  	
    	JFXDialogLayout content= new JFXDialogLayout();
    	content.setHeading(new Text("cemu_UI"));
    	content.setBody(new Text("cemu_UI by @Seil0 \nVersion: "+version+" ("+buildNumber+")  \""+versionName+"\" \nThis Application is made with free Software\nwww.kellerkinder.xyz"));
    	content.setPrefSize(350, 170);
    	StackPane stackPane = new StackPane();
    	stackPane.autosize();
    	JFXDialog dialog =new JFXDialog(stackPane, content, JFXDialog.DialogTransition.LEFT, true);
    	JFXButton button=new JFXButton("Okay");
    	button.setOnAction(new EventHandler<ActionEvent>(){
    	    @Override
    	    public void handle(ActionEvent event){
    	        dialog.close();
    	    }
    	});
    	button.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
    	button.setPrefHeight(32);
    	button.setStyle(dialogBtnStyle);
    	content.setActions(button);
    	main.pane.getChildren().add(stackPane);
    	AnchorPane.setTopAnchor(stackPane, (main.pane.getHeight()-content.getPrefHeight())/2);
    	AnchorPane.setLeftAnchor(stackPane, (main.pane.getWidth()-content.getPrefWidth())/2);
    	dialog.show();
    }

    @FXML
    void settingsBtnAction(ActionEvent event) {
    	if (smmdbTrue) {
    		smmdbAnchorPane.setVisible(false);
    		smmdbTrue = false;
    	}
    	if (settingsTrue) {
    		settingsAnchorPane.setVisible(false);
      		settingsTrue = false;
    		saveSettings();
    	} else {
    		settingsAnchorPane.setVisible(true);
    		settingsTrue = true;
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
    void smmdbBtnAction() {
    	//show smmdbAnchorPane
    	if (smmdbTrue) {
    		smmdbAnchorPane.setVisible(false);
    		smmdbTrue = false;
    	} else {
    		smmdbAnchorPane.setVisible(true);
    		smmdbTrue = true;
    		
    		//start query
        	courses.removeAll(courses);
        	courses.addAll(smmdbApiQuery.startQuery());
        	
        	//add query response to courseTreeTable
    		for(int i = 0; i < courses.size(); i++){
    			CourseTableDataType helpCourse = new CourseTableDataType(courses.get(i).getTitle(),  courses.get(i).getId(),
    																	 courses.get(i).getTime(), courses.get(i).getStars());
    			
    			root.getChildren().add(new TreeItem<CourseTableDataType>(helpCourse));	//add data to root-node
    		}
    	}
    }
    	
    @FXML
    void playBtnAction(ActionEvent event) throws InterruptedException, IOException{
    	dbController.setLastPlayed(selectedGameTitleID);
    	playGame = new playGame(this,dbController);
    	
    	playGame.start();
    }
    
    @FXML
    void totalPlaytimeBtnAction(ActionEvent event){
    	
    }
    
    @FXML
    void lastTimePlayedBtnAction(ActionEvent event){
    	
    }
    
    @FXML
    void cemuTFBtnAction(ActionEvent event) {
    	File cemuDirectory = directoryChooser.showDialog(main.primaryStage);
        if(cemuDirectory == null){
        	LOGGER.info("No Directory selected");
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
        	LOGGER.info("No Directory selected");
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
    void smmdbDownloadBtnAction(ActionEvent event){
    	String downloadUrl = "http://smmdb.ddns.net/api/downloadcourse?id=" + id + "&type=zip";
    	String downloadFileURL = getCemuPath() + "/" + id + ".zip";	//getCemuPath() + "/" + smmID + "/" + id + ".rar"
    	String outputFile = getCemuPath() + "/";
    	
    	try {
			HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
			ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", conn.getInputStream());
			ProgressMonitor pm = pmis.getProgressMonitor();
	        pm.setMillisToDecideToPopup(0);
	        pm.setMillisToPopup(0);
	        pm.setMinimum(0);	// tell the progress bar that we start at the beginning of the stream
	        pm.setMaximum(conn.getContentLength());	// tell the progress bar the total number of bytes we are going to read.
			FileUtils.copyInputStreamToFile(pmis, new File(downloadFileURL));	//download file  + "/mlc01/emulatorSave"
			pmis.close();
			LOGGER.info("downloaded successfull");

			File downloadFile = new File(downloadFileURL);
			
			String source = downloadFileURL;
			String destination = null;
			int highestCourseNumber = 0;
			String courseName = null;
			
			for (int i = 0; i < smmIDs.size(); i++) {
				File smmDirectory = new File(outputFile + "mlc01/emulatorSave/" + smmIDs.get(i));

				if (smmDirectory.exists()) {
					File[] courses = smmDirectory.listFiles(File::isDirectory);

					//get all existing courses in smm directory, new name is highest number +1
					for (int j = 0; j < courses.length; j++) {
						int courseNumber = Integer.parseInt(courses[j].getName().substring(6));
						if (courseNumber > highestCourseNumber) {
							highestCourseNumber = courseNumber;
						}
					}
					
					String number = "000" + (highestCourseNumber +1);
					courseName = "course" + number.substring(number.length() -3, number.length());
					File courseDirectory = new File(outputFile + "mlc01/emulatorSave/" + smmIDs.get(i) + "/");
					
					destination = courseDirectory.getPath();
				}	
			}

			try {			
				ZipFile zipFile = new ZipFile(source);
			    zipFile.extractAll(destination);
			    
			    //rename zipfile
			    File course = new File(destination + "/course000");
			    course.renameTo( new File(destination + "/" + courseName));
			    LOGGER.info("Added new course: " + courseName + ", full path is: " + destination + "/" + courseName);
			} catch (ZipException e) {
			    LOGGER.error("an error occurred during unziping the file!", e);
			}
			
			downloadFile.delete();		
		} catch (IOException e) {
			LOGGER.error("something went wrong during downloading the course", e);
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
    void cloudSyncToggleBtnAction(ActionEvent event){
    	if(cloudSync) {
    		cloudSync = false;
    	} else {
    		
        	JFXDialogLayout content= new JFXDialogLayout();
        	content.setHeading(new Text("activate cloud savegame sync (beta)"));
        	content.setBody(new Text("You just activate the cloud savegame sync function of cemu_UI, \nwhich is currently in beta. Are you sure you want to do this?"));
        	StackPane stackPane = new StackPane();
        	stackPane.autosize();
        	JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.LEFT, true);
        	JFXButton okayBtn = new JFXButton("Okay");
        	okayBtn.setOnAction(new EventHandler<ActionEvent>(){
        	    @Override
        	    public void handle(ActionEvent event){
        	    	cloudSync = true;
    				//TODO rework for other cloud services
    				cloudService = "GoogleDrive";
    	    		main.cloudController.initializeConnection(getCloudService(), getCemuPath());
    	    		main.cloudController.sync(getCloudService(), getCemuPath());
    	        	saveSettings();
        	        dialog.close();
        	    }
        	});
        	JFXButton cancelBtn = new JFXButton("Cancel");
        	cancelBtn.setOnAction(new EventHandler<ActionEvent>(){
        	    @Override
        	    public void handle(ActionEvent event){
        	    	cloudSyncToggleBtn.setSelected(false);
        	        dialog.close();
        	    }
        	});
        	Label placeholder = new Label();
        	placeholder.setPrefSize(15, 10);
        	okayBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
        	cancelBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
        	okayBtn.setPrefHeight(32);
        	cancelBtn.setPrefHeight(32);
        	okayBtn.setStyle(dialogBtnStyle);
        	cancelBtn.setStyle(dialogBtnStyle);
        	content.setActions(cancelBtn, placeholder, okayBtn);
        	content.setPrefSize(419, 140);
        	main.pane.getChildren().add(stackPane);
        	AnchorPane.setTopAnchor(stackPane, (main.pane.getHeight()-content.getPrefHeight())/2);
        	AnchorPane.setLeftAnchor(stackPane, (main.pane.getWidth()-content.getPrefWidth())/2);
        	dialog.show();
    	}
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
			LOGGER.info("No parameter set!");
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
				LOGGER.error("Ops something went wrong!", e);
			}
			
			try {	
				dbController.addRom(title, coverPath, romPath, titleID, "", "", "", "0");
				dbController.loadSingleRom(titleID);
			} catch (SQLException e) {
				LOGGER.error("Oops, something went wrong! Error during adding a game.", e);
			}
		}
    }
    
    /**
     * add game to games(ArrayList) and initialize all needed actions (start, time stamps, titleID)
     * @param title : game title
     * @param coverPath : path to cover (cache)
     * @param romPath : path to rom file (.rpx)
     * @param titleID : rom ID
     */
    void addGame(String title, String coverPath, String romPath, String titleID){
    	ImageView imageView = new ImageView();
    	Label gameTitleLabel = new Label(title);
    	File coverFile = new File(coverPath);
    	VBox VBox = new VBox();
    	JFXButton gameBtn = new JFXButton();
    	Image coverImage = new Image(coverFile.toURI().toString());
    	
    	generatePosition();
    	LOGGER.info("add " + getxPos());	//TODO debug
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
		    	 LOGGER.info("selected: "+title+"; ID: "+titleID);
            	//getting the selected game index by comparing event.getSource() with games.get(i).getButton()
            	for(int i=0; i<games.size(); i++){
            		if(games.get(i).getButton() == event.getSource()){
            			selectedUIDataIndex = i;
            		}
            	}
            	
            	gameExecutePath = romPath;
            	selectedGameTitleID = titleID;
            	selectedGameTitle = title;
            	
            	//underling selected Label
            	lastGameLabel.setStyle("-fx-underline: false;");
            	games.get(selectedUIDataIndex).getLabel().setStyle("-fx-underline: true;");
            	lastGameLabel = games.get(selectedUIDataIndex).getLabel();
            	
            	//setting last played, if lastPlayed is empty game was never played before, else set correct date
            	if(dbController.getLastPlayed(titleID).equals("") || dbController.getLastPlayed(titleID).equals(null)){
            		lastTimePlayedBtn.setText("Last played, never");
            		totalPlaytimeBtn.setText(dbController.getTotalPlaytime(titleID)+ " min");
            	}else{
                	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                	
                	int today = Integer.parseInt(dtf.format(LocalDate.now()).replaceAll("-", ""));
                	int yesterday  = Integer.parseInt(dtf.format(LocalDate.now().minusDays(1)).replaceAll("-", ""));
                	int lastPlayedDay = Integer.parseInt(dbController.getLastPlayed(titleID).replaceAll("-", ""));
                	
                	if(today == lastPlayedDay){
                		lastTimePlayedBtn.setText("Last played, today");
                	}else if(yesterday == lastPlayedDay){
                		lastTimePlayedBtn.setText("Last played, yesterday");
                	}else{
                    	lastTimePlayedBtn.setText("Last played, "+dbController.getLastPlayed(titleID));
                	}
            	}
            	
            	//setting total playtime, if total playtime > 60 minutes, formate is "x hours x minutes" (xh x min), else only minutes are showed 
            	if(Integer.parseInt(dbController.getTotalPlaytime(titleID)) > 60){
            		int hoursPlayed = (int) Math.floor(Integer.parseInt(dbController.getTotalPlaytime(titleID))/60);
            		int minutesPlayed = Integer.parseInt(dbController.getTotalPlaytime(titleID))-60*hoursPlayed;
            		totalPlaytimeBtn.setText(hoursPlayed+" h     "+minutesPlayed+" min");
            	}else{
            		totalPlaytimeBtn.setText(dbController.getTotalPlaytime(titleID)+ " min");
            	}
            	
            	if (!playTrue) {
            		playBtnSlideIn();
            	}
            	if (menuTrue) {
					sideMenuSlideOut();
				}
            	
            }	
        });
    	games.add(new UIROMDataType(VBox, gameTitleLabel, gameBtn, titleID, romPath));
    }
    
    //add all games saved in games(ArrayList) to the gamesAnchorPane
    void addUIData() {
    	for(int i=0; i<games.size(); i++){
    		gamesAnchorPane.getChildren().add(games.get(i).getVBox());
    	}
    }
    
    //remove all games from gamesAnchorPane and add them afterwards
    void refreshUIData() {
    	//remove all games form gamesAnchorPane
    	gamesAnchorPane.getChildren().removeAll(gamesAnchorPane.getChildren());

		//reset position
	    xPos = -200;
	    yPos = 17;
	    xPosHelper = 0;
	    
	    //add all games to gamesAnchorPane (UI)
		for(int i=0; i< games.size(); i++){
			generatePosition();
	    	games.get(i).getVBox().setLayoutX(getxPos());
	    	games.get(i).getVBox().setLayoutY(getyPos());
			gamesAnchorPane.getChildren().add(games.get(i).getVBox());
		}
    }
    
    void refreshplayBtnPosition() {
    	playBtn.setLayoutX((mainAnchorPane.getWidth()/2)-50);
    	totalPlaytimeBtn.setLayoutX((mainAnchorPane.getWidth()/2)-50-20.5-100);
    	lastTimePlayedBtn.setLayoutX((mainAnchorPane.getWidth()/2)+50+20.5);
    }
    
    private void addCourseDescription(SmmdbApiDataType course) {
    	String courseTheme;
    	String gameStyle;
    	String difficulty;
    	String autoscroll;
    	smmdbTextFlow.getChildren().remove(0, smmdbTextFlow.getChildren().size());
    	nameText.clear();
    	courseText.clear();
    	
    	switch (course.getCourseTheme()) {
		case 0:
			courseTheme = "Ground";
			break;
		case 1:
			courseTheme = "Underground";
			break;
		case 2:
			courseTheme = "Castle";
			break;
		case 3:
			courseTheme = "Airship";
			break;
		case 4:
			courseTheme = "Underwater";
			break;
		case 5:
			courseTheme = "Ghost House";
			break;
		default:
			courseTheme = "notset";
			break;
		}
    	
    	switch (course.getGameStyle()) {
		case 0:
			gameStyle = "SMB";
			break;
		case 1:
			gameStyle = "SMB3";
			break;
		case 2:
			gameStyle = "SMW";
			break;
		case 3:
			gameStyle = "NSMBU";
			break;
		default:
			gameStyle = "notset";
			break;
		}

    	switch (course.getDifficulty()) {
		case 0:
			difficulty = "Easy";
			break;
		case 1:
			difficulty = "Normal";
			break;
		case 2:
			difficulty = "Expert";
			break;
		case 3:
			difficulty = "Super Expert";
			break;
		case 4:
			difficulty = "Mixed";
			break;
		default:
			difficulty = "notset";
			break;
		}
    	
    	switch (course.getAutoScroll()) {
		case 0:
			autoscroll = "disabled";
			break;
		case 1:
			autoscroll = "slow";
			break;
		case 2:
			autoscroll = "medium";
			break;
		case 3:
			autoscroll = "fast";
			break;
		default:
			autoscroll = "notset";
			break;
		}
    	
    	nameText.add(0, new Text("title" + ": "));
    	nameText.add(1, new Text("owner" + ": "));
    	nameText.add(2, new Text("Course-Theme" + ": "));
    	nameText.add(3, new Text("Game-Style" + ": "));
    	nameText.add(4, new Text("difficulty" + ": "));
    	nameText.add(5, new Text("Auto-Scroll" + ": "));
    	nameText.add(6, new Text("Time" + ": "));
    	nameText.add(7, new Text("lastmodified" + ": "));
    	nameText.add(8, new Text("uploaded" + ": "));
    	nameText.add(9, new Text("nintendoid" + ": "));
    	
    	courseText.add(0, new Text(course.getTitle() + "\n"));
    	courseText.add(1, new Text(course.getOwner() + "\n"));
    	courseText.add(2, new Text(courseTheme + "\n"));
    	courseText.add(3, new Text(gameStyle + "\n"));
    	courseText.add(4, new Text(difficulty + "\n"));
    	courseText.add(5, new Text(autoscroll + "\n"));
    	courseText.add(6, new Text(course.getTime() + "\n"));
    	courseText.add(7, new Text(new java.util.Date((long)course.getLastmodified()*1000) + "\n"));
    	courseText.add(8, new Text(new java.util.Date((long)course.getUploaded()*1000) + "\n"));
    	courseText.add(9, new Text(course.getNintendoid() + "\n"));
    	
    	for(int i=0; i<nameText.size(); i++){
			nameText.get(i).setFont(Font.font ("System", FontWeight.BOLD, 14));
			courseText.get(i).setFont(Font.font ("System", 14));
			smmdbTextFlow.getChildren().addAll(nameText.get(i),courseText.get(i));
		}

    }

    /**
     * xPosHelper based on window width = -24(Windows)/-36(Linux)
     * calculates how many games can be displayed in one row
     */
    private void generatePosition() {
    	int xPosHelperMax;
    	//TODO  see issue #main.1
    	if(System.getProperty("os.name").equals("Linux")){
    		xPosHelperMax = (int) Math.floor((mainAnchorPane.getWidth() - 36) / 217);
    	} else {
    		xPosHelperMax = (int) Math.floor((mainAnchorPane.getWidth() - 24) / 217);
    	}
    	if(xPosHelper == xPosHelperMax){
    		xPos = 17;
    		yPos = yPos + 345;		
    		xPosHelper = 1;
    	}else{
    		xPos = xPos + 217;
    		xPosHelper++;
    	}
//    	System.out.println("Breit: " + mainAnchorPane.getWidth());
//    	System.out.println("xPosHelper: " + xPosHelper);
//    	System.out.println("yPos: " + yPos);
//    	System.out.println("xPos: " + xPos);
    }
    
    private void applyColor() {
		String boxStyle = "-fx-background-color: #"+getColor()+";";
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: BLACK;";
		String btnStyleWhite = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: WHITE;";
		BigInteger icolor = new BigInteger(getColor(),16);
		BigInteger ccolor = new BigInteger("78909cff",16);
		
		sideMenuVBox.setStyle(boxStyle);
		topHBox.setStyle(boxStyle);
		cemuTextField.setFocusColor(Color.valueOf(getColor()));
		romTextField.setFocusColor(Color.valueOf(getColor()));
		
		if(icolor.compareTo(ccolor) == -1){
			dialogBtnStyle = btnStyleWhite;
			
			aboutBtn.setStyle("-fx-text-fill: WHITE;");
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			addBtn.setStyle("-fx-text-fill: WHITE;");
			reloadRomsBtn.setStyle("-fx-text-fill: WHITE;");
			smmdbBtn.setStyle("-fx-text-fill: WHITE;");
			playBtn.setStyle("-fx-text-fill: WHITE; -fx-font-family: Roboto Medium;");
			cemuTFBtn.setStyle(btnStyleWhite);
			romTFBtn.setStyle(btnStyleWhite);
			smmdbDownloadBtn.setStyle(btnStyleWhite);
			playBtn.setStyle(btnStyleWhite);
			
			aboutBtn.setGraphic(info_white);
			settingsBtn.setGraphic(settings_white);
			addBtn.setGraphic(add_circle_white);
			reloadRomsBtn.setGraphic(cached_white);
			smmdbBtn.setGraphic(smmdb_white);
			
			menuHam.getStyleClass().add("jfx-hamburgerW");
		}else{
			dialogBtnStyle = btnStyleBlack;
			
			aboutBtn.setStyle("-fx-text-fill: BLACK;");
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			addBtn.setStyle("-fx-text-fill: BLACK;");
			reloadRomsBtn.setStyle("-fx-text-fill: BLACK;");
			smmdbBtn.setStyle("-fx-text-fill: BLACK;");
			playBtn.setStyle("-fx-text-fill: BLACK; -fx-font-family: Roboto Medium;");
			cemuTFBtn.setStyle(btnStyleBlack);
			romTFBtn.setStyle(btnStyleBlack);
			smmdbDownloadBtn.setStyle(btnStyleBlack);
			playBtn.setStyle(btnStyleBlack);
			
			aboutBtn.setGraphic(info_black);
			settingsBtn.setGraphic(settings_black);
			addBtn.setGraphic(add_circle_black);
			reloadRomsBtn.setGraphic(cached_black);
			smmdbBtn.setGraphic(smmdb_black);
			
			menuHam.getStyleClass().add("jfx-hamburgerB");
		}

		for(int i=0; i<games.size(); i++){
			games.get(i).getButton().setRipplerFill(Paint.valueOf(getColor()));
		}
    }
		
    void saveSettings(){
    	LOGGER.info("saving Settings ...");
    	OutputStream outputStream;	//new output-stream
    	try {
    		props.setProperty("cemuPath", getCemuPath());
			props.setProperty("romPath", getRomPath());
			props.setProperty("color", getColor());
			props.setProperty("fullscreen", String.valueOf(isFullscreen()));
			props.setProperty("cloudSync", String.valueOf(cloudSync));
			if (getCloudService() == null) {
				props.setProperty("cloudService", "");
			} else {
				props.setProperty("cloudService", getCloudService());
			}
			props.setProperty("folderID", main.cloudController.getFolderID(getCloudService()));
			props.setProperty("windowWidth", String.valueOf(mainAnchorPane.getWidth()));
			props.setProperty("windowHeight", String.valueOf(mainAnchorPane.getHeight()));
    		if(System.getProperty("os.name").equals("Linux")){
    			outputStream = new FileOutputStream(configFileLinux);
    		}else{
    			outputStream = new FileOutputStream(configFileWin);
    		}
    		props.storeToXML(outputStream, "cemu_UI settings");	//write new .xml
    		outputStream.close();
    		LOGGER.info("saving Settings done!");
    	} catch (IOException e) {
    		LOGGER.error("an error occured", e);
    	}
    }
    
    /**
     * loading saved settings from the config.xml file
     * if a value is not present, default is used instead
     */
    void loadSettings(){
    	LOGGER.info("loading settings ...");
		InputStream inputStream;
		try {
			if(System.getProperty("os.name").equals("Linux")){
				inputStream = new FileInputStream(configFileLinux);
			}else{
				inputStream = new FileInputStream(configFileWin);
			}
			props.loadFromXML(inputStream);	//new input-stream from .xml
			
			try {
				setCemuPath(props.getProperty("cemuPath"));
			} catch (Exception e) {
				LOGGER.error("cloud not load cemuPath", e);
				setCemuPath("");
			}
			
			try {
				setRomPath(props.getProperty("romPath"));
			} catch (Exception e) {
				LOGGER.error("could not load romPath", e);
				setRomPath("");
			}
			
			try {
				setColor(props.getProperty("color"));
			} catch (Exception e) {
				LOGGER.error("could not load color value, setting default instead", e);
				setColor("00a8cc");
			}
			
			try {
				setFullscreen(Boolean.parseBoolean(props.getProperty("fullscreen")));
			} catch (Exception e) {
				LOGGER.error("could not load fullscreen, setting default instead", e);
				setFullscreen(false);
			}
			
			try {
				setCloudSync(Boolean.parseBoolean(props.getProperty("cloudSync")));
			} catch (Exception e) {
				LOGGER.error("could not load cloudSync, setting default instead", e);
				setCloudSync(false);
			}
			
			try {
				setCloudService(props.getProperty("cloudService"));
			} catch (Exception e) {
				LOGGER.error("could not load cloudSync", e);
				setCloudService("");
			}
			
			try {
				main.cloudController.setFolderID(props.getProperty("folderID"), getCloudService());
			} catch (Exception e) {
				LOGGER.error("could not load folderID, disable cloud sync. Please contact an developer", e);
				setCloudSync(false);
			}
			
			try {
				setWindowWidth(Double.parseDouble(props.getProperty("windowWidth")));
			} catch (Exception e) {
				LOGGER.error("could not load windowWidth, setting default instead", e);
				setWindowWidth(904);
			}
			
			try {
				setWindowHeight(Double.parseDouble(props.getProperty("windowHeight")));
			} catch (Exception e) {
				LOGGER.error("could not load windowHeight, setting default instead", e);
				setWindowHeight(600);
			}
			
			inputStream.close();
	    	LOGGER.info("loading settings done!");
		} catch (IOException e) {
			LOGGER.error("an error occured", e);
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
		totalPlaytimeBtn.setVisible(true);
		playTrue = true;
		
		TranslateTransition playBtnTransition = new TranslateTransition(Duration.millis(300), playBtn);
		playBtnTransition.setFromY(55);
		playBtnTransition.setToY(0);
		playBtnTransition.play();
		
		TranslateTransition lastTimePlayedBtnTransition = new TranslateTransition(Duration.millis(300), lastTimePlayedBtn);
		lastTimePlayedBtnTransition.setFromY(55);
		lastTimePlayedBtnTransition.setToY(0);
		lastTimePlayedBtnTransition.play();
		
		TranslateTransition timePlayedBtnTransition = new TranslateTransition(Duration.millis(300), totalPlaytimeBtn);
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
		
		TranslateTransition timePlayedBtnTransition = new TranslateTransition(Duration.millis(300), totalPlaytimeBtn);
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
	
	@SuppressWarnings("unused")
	/**
	 * @return the main color in hexadecimal format
	 */
	private String hexToRgb() {
		System.out.println(getColor());
		int hex = Integer.parseInt(getColor().substring(0, 5), 16);
		
	    int r = (hex & 0xFF0000) >> 16;
	    int g = (hex & 0xFF00) >> 8;
	    int b = (hex & 0xFF);
	    
	    return r + ", " + g + ", " + b;
	}
	
	/**
	 * 
	 * @param originalImage original image which size is changed
	 * @param type type of the original image (PNG,JPEG,...)
	 * @param imgWidth wanted width
	 * @param imgHeigth wanted height
	 * @return the rezised image
	 */
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

	public JFXButton getPlayBtn() {
		return playBtn;
	}

	public void setPlayBtn(JFXButton playBtn) {
		this.playBtn = playBtn;
	}

	public double getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(double windowWidth) {
		this.windowWidth = windowWidth;
	}

	public double getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(double windowHeight) {
		this.windowHeight = windowHeight;
	}

}
