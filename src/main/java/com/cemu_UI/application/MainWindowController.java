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

package com.cemu_UI.application;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.controller.SmmdbAPIController;
import com.cemu_UI.controller.UpdateController;
import com.cemu_UI.controller.dbController;
import com.cemu_UI.datatypes.CourseTableDataType;
import com.cemu_UI.datatypes.SmmdbApiDataType;
import com.cemu_UI.datatypes.UIROMDataType;
import com.cemu_UI.uiElements.JFXEditGameDialog;
import com.cemu_UI.uiElements.JFXInfoDialog;
import com.cemu_UI.uiElements.JFXOkayCancelDialog;
import com.cemu_UI.uiElements.JFXTextAreaInfoDialog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
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
	private JFXButton updateBtn;

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
	private JFXTextField courseSearchTextFiled;

	@FXML
	private TextFlow smmdbTextFlow;

	@FXML
	private JFXColorPicker colorPicker;

	@FXML
	private JFXToggleButton cloudSyncToggleBtn;

	@FXML
	private JFXToggleButton autoUpdateToggleBtn;

	@FXML
	private JFXToggleButton fullscreenToggleBtn;

	@FXML
	private ChoiceBox<String> branchChoisBox;

	@FXML
	private AnchorPane mainAnchorPane;

	@FXML
	private AnchorPane gamesAnchorPane;

	@FXML
	private AnchorPane settingsAnchorPane;

	@FXML
	private AnchorPane smmdbAnchorPane;

	@FXML
	private ScrollPane mainScrollPane;

	@FXML
	private ScrollPane settingsScrollPane;

	@FXML
	private ScrollPane smmdbScrollPane;
	
	@FXML
	private ScrollPane smmdbImageViewScrollPane;

	@FXML
	private VBox sideMenuVBox;

	@FXML
	private HBox topHBox;

	@FXML
	private ImageView smmdbImageView;

	@FXML
	private Label helpLbl;

	@FXML
	private Label cemu_UISettingsLbl;

	@FXML
	private Label cemuDirectoryLbl;

	@FXML
	private Label romDirectoryLbl;

	@FXML
	private Label mainColorLbl;

	@FXML
	private Label updateLbl;

	@FXML
	private Label branchLbl;

	@FXML
	private Label cemuSettingsLbl;

	@FXML
	private Label licensesLbl;

	@FXML
	private JFXTreeTableView<CourseTableDataType> courseTreeTable = new JFXTreeTableView<CourseTableDataType>();

	@FXML
	TreeItem<CourseTableDataType> root = new TreeItem<>(new CourseTableDataType("", "", 0, 0));

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
	SmmdbAPIController smmdbAPIController;
	playGame playGame;
	private static MainWindowController MWC;
	private UpdateController updateController;
	private boolean menuTrue = false;
	private boolean settingsTrue = false;
	private boolean playTrue = false;
	private boolean smmdbTrue = false;
	private boolean autoUpdate = false;
	private boolean useBeta = false;
	private boolean fullscreen;
	private boolean cloudSync;
	private String cloudService = ""; // set cloud provider (at the moment only GoogleDrive, Dropbox is planed)
	private String cemuPath;
	private String romPath;
	private String gameExecutePath;
	private String color;
	private String dialogBtnStyle;
	private String selectedGameTitleID;
	private String selectedGameTitle;
	private String id;
	private String version = "0.2.1";
	private String buildNumber = "059";
	private String versionName = "Puzzle Plank Galaxy";
	private int xPos = -200;
	private int yPos = 17;
	private int xPosHelper;
	private int oldXPosHelper;
	private int selectedUIDataIndex;
	private int selected;
	private double windowWidth;
	private double windowHeight;
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/cemu_UI");
	private File dirLinux = new File(System.getProperty("user.home") + "/cemu_UI");
	private File configFileWin = new File(dirWin + "/config.xml");
	private File configFileLinux = new File(dirLinux + "/config.xml");
	private File pictureCacheWin = new File(dirWin + "/picture_cache");
	private File pictureCacheLinux = new File(dirLinux + "/picture_cache");
	private ObservableList<String> branches = FXCollections.observableArrayList("stable", "beta");
	private ObservableList<String> smmIDs = FXCollections.observableArrayList("fe31b7f2", "44fc5929"); // TODO add more IDs
	private ObservableList<UIROMDataType> games = FXCollections.observableArrayList();
	ObservableList<SmmdbApiDataType> courses = FXCollections.observableArrayList();
	ObservableList<SmmdbApiDataType> filteredCourses = FXCollections.observableArrayList();
	ArrayList<Text> courseText = new ArrayList<Text>();
	ArrayList<Text> nameText = new ArrayList<Text>();
	Properties props = new Properties();
	Properties gameProps = new Properties();
	private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
	private HamburgerBackArrowBasicTransition burgerTask;
	private MenuItem edit = new MenuItem("edit");
	private MenuItem remove = new MenuItem("remove");
	private MenuItem addUpdate = new MenuItem("update");
	private MenuItem addDLC = new MenuItem("add DLC");
	private ContextMenu gameContextMenu = new ContextMenu(edit, remove, addUpdate, addDLC);
	private Label lastGameLabel = new Label();

	private ImageView add_circle_black = new ImageView(new Image("icons/ic_add_circle_black_24dp_1x.png"));
	private ImageView info_black = new ImageView(new Image("icons/ic_info_black_24dp_1x.png"));
	private ImageView settings_black = new ImageView(new Image("icons/ic_settings_black_24dp_1x.png"));
	private ImageView cached_black = new ImageView(new Image("icons/ic_cached_black_24dp_1x.png"));
	private ImageView smmdb_black = new ImageView(new Image("icons/ic_get_app_black_24dp_1x.png"));
	private ImageView add_circle_white = new ImageView(new Image("icons/ic_add_circle_white_24dp_1x.png"));
	private ImageView info_white = new ImageView(new Image("icons/ic_info_white_24dp_1x.png"));
	private ImageView settings_white = new ImageView(new Image("icons/ic_settings_white_24dp_1x.png"));
	private ImageView cached_white = new ImageView(new Image("icons/ic_cached_white_24dp_1x.png"));
	private ImageView smmdb_white = new ImageView(new Image("icons/ic_get_app_white_24dp_1x.png"));
	private Image close_black = new Image("icons/close_black_2048x2048.png");

	public void setMain(Main main) {
		this.main = main;
		dbController = new dbController(this);
		smmdbAPIController = new SmmdbAPIController();
	}
	
	/**
	 * initialize the MainWindowController
	 * loadSettings, checkAutoUpdate, initUI and initActions
	 */
	void init() {
		loadSettings();
		checkAutoUpdate();
		initUI();
		initActions();
	}
	
	/**
	 * initialize all variable UI parameters and elements
	 */
	private void initUI() {
		LOGGER.info("initializing UI ...");
		
		if (getWindowWidth() > 100 && getWindowHeight() > 100) {
			mainAnchorPane.setPrefSize(getWindowWidth(), getWindowHeight());	
		}
		refreshplayBtnPosition();
		
		cemuTextField.setText(cemuPath);
		romTextField.setText(romPath);
		colorPicker.setValue(Color.valueOf(getColor()));
		fullscreenToggleBtn.setSelected(isFullscreen());
		cloudSyncToggleBtn.setSelected(isCloudSync());
		autoUpdateToggleBtn.setSelected(isAutoUpdate());
		branchChoisBox.setItems(branches);
		
		if (isUseBeta()) {
			branchChoisBox.getSelectionModel().select(1);
		} else {
			branchChoisBox.getSelectionModel().select(0);
		}
		
		applyColor();
		
		// initialize courseTable
		titleColumn.setPrefWidth(185);
		timeColumn.setPrefWidth(112);
		starsColumn.setPrefWidth(90);
		
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
		courseTreeTable.getColumns().get(3).setVisible(false); // the idColumn should not bee displayed
		
		LOGGER.info("initializing UI done");
	}
	
	/**
	 * initialize all actions not initialized by a own method
	 */
	private void initActions() {
		LOGGER.info("initializing Actions ...");
		
		MWC = this;
		burgerTask = new HamburgerBackArrowBasicTransition(menuHam);
		menuHam.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			if (playTrue) {
	    		playBtnSlideOut();
	    	}
	    	if (menuTrue) { 
				sideMenuSlideOut();
				burgerTask.setRate(-1.0);
				burgerTask.play();
				menuTrue = false;
			} else {
				sideMenuSlideIn();
				burgerTask.setRate(1.0);
				burgerTask.play();
				menuTrue = true;
			}
			if (settingsTrue) {
				settingsScrollPane.setVisible(false);
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
				try {
					LOGGER.info("edit " + selectedGameTitleID);
					String[] gameInfo = dbController.getGameInfo(selectedGameTitleID);

					// new edit dialog
					String headingText = "edit a game";
					String bodyText = "You can edit the tile and rom/cover path.";
					JFXEditGameDialog editGameDialog = new JFXEditGameDialog(headingText, bodyText, dialogBtnStyle, 450,
							300, 1, MWC, main.primaryStage, main.pane);
					editGameDialog.setTitle(gameInfo[0]);
					editGameDialog.setCoverPath(gameInfo[1]);
					editGameDialog.setRomPath(gameInfo[2]);
					editGameDialog.setTitleID(gameInfo[3]);
					editGameDialog.show();
				} catch (Exception e) {
					LOGGER.warn("trying to edit " + selectedGameTitleID + ",which is not a valid type!", e);
				}
			}
		});
		
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {		
				try {
					LOGGER.info("remove " + selectedGameTitle + "(" + selectedGameTitleID + ")");
					String headingText = "remove \"" + selectedGameTitle + "\"";
					String bodyText = "Are you sure you want to delete " + selectedGameTitle + " ?";
					EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							try {
								games.remove(selectedUIDataIndex); // remove game form games-list
								dbController.removeGame(selectedGameTitleID); // remove game from database
								refreshUIData(); // refresh all games at gamesAnchorPane (UI)
							} catch (Exception e) {
								LOGGER.error("error while removing ROM from database!", e);
							}
						}
					};

					EventHandler<ActionEvent> cancelAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							// do nothing
						}
					};

					JFXOkayCancelDialog removeGameDialog = new JFXOkayCancelDialog(headingText, bodyText,
							dialogBtnStyle, 350, 170, okayAction, cancelAction, main.pane);
					removeGameDialog.show();
				} catch (Exception e) {
					LOGGER.error("error while removing " + selectedGameTitle + "(" + selectedGameTitleID + ")", e);
				}
            }
		});
		
		addUpdate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					LOGGER.info("update: " + selectedGameTitleID);
					String headingText = "update \"" + selectedGameTitle + "\"";
					String bodyText = "pleas select the update root directory";
					EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							DirectoryChooser directoryChooser = new DirectoryChooser();
							File selectedDirecroty = directoryChooser.showDialog(main.primaryStage);
							String updatePath = selectedDirecroty.getAbsolutePath();
							String[] parts = selectedGameTitleID.split("-"); // split string into 2 parts at "-"
							File srcDir = new File(updatePath);
							File destDir;

							if (System.getProperty("os.name").equals("Linux")) {
								destDir = new File(cemuPath + "/mlc01/usr/title/" + parts[0] + "/" + parts[1]);
							} else {
								destDir = new File(cemuPath + "\\mlc01\\usr\\title\\" + parts[0] + "\\" + parts[1]);
							}

							// if directory doesn't exist create it
							if (destDir.exists() != true) {
								destDir.mkdir();
							}

							try {
								LOGGER.info("copying the content of " + updatePath + " to " + destDir.toString());
								playBtn.setText("updating...");
								playBtn.setDisable(true);
								FileUtils.copyDirectory(srcDir, destDir); // TODO progress indicator
								playBtn.setText("play");
								playBtn.setDisable(false);
								LOGGER.info("copying files done!");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};

					EventHandler<ActionEvent> cancelAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							// do nothing
						}
					};

					JFXOkayCancelDialog updateGameDialog = new JFXOkayCancelDialog(headingText, bodyText,
							dialogBtnStyle, 350, 170, okayAction, cancelAction, main.pane);
					updateGameDialog.show();
				} catch (Exception e) {
					LOGGER.warn("trying to update " + selectedGameTitleID + ",which is not a valid type!", e);
				}
			}
		});
		
		addDLC.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					LOGGER.info("add DLC: " + selectedGameTitleID);
					String headingText = "add a DLC to \"" + selectedGameTitle + "\"";
					String bodyText = "pleas select the DLC root directory";
					EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							DirectoryChooser directoryChooser = new DirectoryChooser();
							File selectedDirecroty = directoryChooser.showDialog(main.primaryStage);
							String dlcPath = selectedDirecroty.getAbsolutePath();
							String[] parts = selectedGameTitleID.split("-"); // split string into 2 parts at "-"
							File srcDir = new File(dlcPath);
							File destDir;
							if (System.getProperty("os.name").equals("Linux")) {
								destDir = new File(cemuPath + "/mlc01/usr/title/" + parts[0] + "/" + parts[1] + "/aoc");
							} else {
								destDir = new File(
										cemuPath + "\\mlc01\\usr\\title\\" + parts[0] + "\\" + parts[1] + "\\aoc");
							}

							// if directory doesn't exist create it
							if (destDir.exists() != true) {
								destDir.mkdir();
							}

							try {
								LOGGER.info("copying the content of " + dlcPath + " to " + destDir.toString());
								playBtn.setText("copying files...");
								playBtn.setDisable(true);
								FileUtils.copyDirectory(srcDir, destDir); // TODO progress indicator
								playBtn.setText("play");
								playBtn.setDisable(false);
								LOGGER.info("copying files done!");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};

					EventHandler<ActionEvent> cancelAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							// do nothing
						}
					};

					JFXOkayCancelDialog addDLCDialog = new JFXOkayCancelDialog(headingText, bodyText, dialogBtnStyle,
							350, 170, okayAction, cancelAction, main.pane);
					addDLCDialog.show();
				} catch (Exception e) {
					LOGGER.warn("trying to add a dlc to " + selectedGameTitleID + ",which is not a valid type!", e);
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
		
		courseSearchTextFiled.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				filteredCourses.removeAll(filteredCourses);
				root.getChildren().remove(0, root.getChildren().size());

				for (int i = 0; i < courses.size(); i++) {
					if (courses.get(i).getTitle().toLowerCase()
							.contains(courseSearchTextFiled.getText().toLowerCase())) {

						// add data from courses to filteredCourses where title contains search input
						filteredCourses.add(courses.get(i));
					}
				}

				for (int i = 0; i < filteredCourses.size(); i++) {
					CourseTableDataType helpCourse = new CourseTableDataType(filteredCourses.get(i).getTitle(),
							filteredCourses.get(i).getId(), filteredCourses.get(i).getTime(),
							filteredCourses.get(i).getStars());

					root.getChildren().add(new TreeItem<CourseTableDataType>(helpCourse)); // add data to root-node
				}
			}
		});
		
		// Change-listener for TreeTable
		courseTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {
				selected = courseTreeTable.getSelectionModel().getSelectedIndex(); // get selected item

				// FIXME if a item is selected and you change the sorting,you can't select a new
				// item
				id = idColumn.getCellData(selected); // get name of selected item

				for (int i = 0; i < courses.size(); i++) {
					if (courses.get(i).getId() == id) {
						try {
							
							URL url = new URL("https://smmdb.ddns.net/courseimg/" + id + "_full?v=1");
							Image image = new Image(url.toURI().toString());
							
							System.out.println("Bild Höhe: " + image.getHeight() + ",Breite: " + image.getWidth());
							System.out.println("Imageview Höhe: " + smmdbImageView.getFitHeight() + ",Breite: " + smmdbImageView.getFitWidth());
							
							// scale image to 148px Height
							double scalefactor = 148 / image.getHeight();					
							System.out.println("Skalierung: " + scalefactor);
							
							int nWidth = (int) Math.rint(scalefactor * image.getWidth());
							int nHeight = (int) Math.rint(scalefactor * image.getHeight());
							
							System.out.println("Bild NEU Höhe: " + nHeight + ",Breite: " + nWidth);
							
							smmdbImageView.setFitWidth(image.getWidth()); // set ImageView width to the image width 
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
		
		helpLbl.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					try {
						Desktop.getDesktop().browse(new URI("https://github.com/Seil0/cemu_UI/issues/3"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		});

		branchChoisBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) {
				if (branchChoisBox.getItems().get((int) new_value).toString() == "beta") {
					setUseBeta(true);
				} else {
					setUseBeta(false);
				}
				saveSettings();
			}
		});
        
        licensesLbl.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		        	
		        	String headingText = "cemu_UI";
		        	String bodyText = "cemu_UI is licensed under the terms of GNU GPL 3.\n\n"
		        					+ "JFoenix, Apache License 2.0\n"
		        					+ "minimal-json, MIT License\n"
		        					+ "sqlite-jdbc, Apache License 2.0\n"
		        					+ "Apache Commons IO, Apache License 2.0\n"
		        					+ "Apache Commons Logging, Apache License 2.0\n"
		        					+ "Apache Commons Codec, Apache License 2.0\n"
		        					+ "Apache Log4j 2, Apache License 2.0\n";
		        	
		        	EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							// do nothing
						}
					};
					EventHandler<ActionEvent> cancelAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							String headingText = "cemu_UI";
							String bodyText = "";

							try {
								BufferedReader licenseBR = new BufferedReader(
										new InputStreamReader(getClass().getResourceAsStream("/licenses/licenses_show.txt")));
								String currentLine;
								
								
								while ((currentLine = licenseBR.readLine()) != null) {
									bodyText = bodyText + currentLine + "\n";	
								}
								
								licenseBR.close();
							} catch (IOException e) {
								LOGGER.error("Cloud not read the license file!", e);
							}
							
							JFXTextAreaInfoDialog licenseDialog = new JFXTextAreaInfoDialog(headingText, bodyText,
									dialogBtnStyle, 510, 450, main.pane);
							licenseDialog.show();
						}
					};
		        	
					JFXOkayCancelDialog licenseOverviewDialog = new JFXOkayCancelDialog(headingText, bodyText, dialogBtnStyle,
							350, 275, okayAction, cancelAction, main.pane);
					licenseOverviewDialog.setCancelText("show licenses");
					licenseOverviewDialog.show(); 	
		        }
		    }
		});

		LOGGER.info("initializing Actions done!");
	}
    
	@FXML
	void detailsSlideoutBtnAction(ActionEvent event) {
		playBtnSlideOut();
	}
    
    @FXML
    void aboutBtnAction() {
    	String headingText = "cemu_UI";
    	String bodyText = "cemu_UI by @Seil0 \nVersion: " + version + " (" + buildNumber + ")  \"" + versionName + "\" \n"
    					+ "This Application is made with free Software\n"
    					+ "and licensed under the terms of GNU GPL 3.\n\n"
    					+ "www.kellerkinder.xyz";
    	JFXInfoDialog aboutDialog = new JFXInfoDialog(headingText, bodyText, dialogBtnStyle, 350, 200, main.pane);
    	aboutDialog.show();
    }

	@FXML
	void settingsBtnAction(ActionEvent event) {
		if (smmdbTrue) {
			smmdbAnchorPane.setVisible(false);
			smmdbTrue = false;
		}
		if (settingsTrue) {
			settingsScrollPane.setVisible(false);
			settingsTrue = false;
			saveSettings();
		} else {
			settingsScrollPane.setVisible(true);
			settingsTrue = true;
		}
	}
    
	@FXML
	void reloadRomsBtnAction() throws IOException {
		
		JFXSpinner spinner = new JFXSpinner();
		spinner.setPrefSize(30, 30);
		spinner.setStyle(" -fx-background-color: #f4f4f4;");
		main.pane.getChildren().add(spinner);
		AnchorPane.setTopAnchor(spinner, (main.pane.getHeight()-spinner.getPrefHeight())/2);
    	AnchorPane.setLeftAnchor(spinner, (main.pane.getWidth()-spinner.getPrefWidth())/2);
    	
    	Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				dbController.loadRomDirectory(getRomPath()); // reload the rom directory
				
				Platform.runLater(() -> {
					refreshUIData(); // refresh the list of games displayed on screen
					main.pane.getChildren().remove(spinner);
                });
			}
		});
		thread.start();
	}
    
	@FXML
	void smmdbBtnAction() {
		// show smmdbAnchorPane
		if (smmdbTrue) {
			smmdbAnchorPane.setVisible(false);
			smmdbTrue = false;
		} else {
			smmdbAnchorPane.setVisible(true);
			smmdbTrue = true;

			// start query in new thread		
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Platform.runLater(() -> {
						smmdbDownloadBtn.setText("loading ...");
						smmdbDownloadBtn.setDisable(true);
						root.getChildren().remove(0,root.getChildren().size());
	                });
					courses.removeAll(courses); // remove existing courses
					courses.addAll(smmdbAPIController.startQuery()); // start query

					// add query response to courseTreeTable
					for (int i = 0; i < courses.size(); i++) {
						CourseTableDataType helpCourse = new CourseTableDataType(courses.get(i).getTitle(),
								courses.get(i).getId(), courses.get(i).getTime(), courses.get(i).getStars());

						Platform.runLater(() -> {
							root.getChildren().add(new TreeItem<CourseTableDataType>(helpCourse)); // add data to root-node
							smmdbDownloadBtn.setText("Download");
							smmdbDownloadBtn.setDisable(false);
		                });
					}
				}
			});
			thread.start();
		}
	}
    	
	@FXML
	void playBtnAction(ActionEvent event) throws InterruptedException, IOException {
		dbController.setLastPlayed(selectedGameTitleID);
		playGame = new playGame(this, dbController);

		playGame.start();
	}

	@FXML
	void totalPlaytimeBtnAction(ActionEvent event) {

	}

	@FXML
	void lastTimePlayedBtnAction(ActionEvent event) {

	}
    
	@FXML
	void cemuTFBtnAction(ActionEvent event) {
		File cemuDirectory = directoryChooser.showDialog(main.primaryStage);
		if (cemuDirectory == null) {
			LOGGER.info("No Directory selected");
		} else {
			setCemuPath(cemuDirectory.getAbsolutePath());
			saveSettings();
			cemuTextField.setText(getCemuPath());
			try {
				Runtime.getRuntime().exec("java -jar cemu_UI.jar"); // start again
				System.exit(0); // finishes itself
			} catch (IOException e) {
				LOGGER.error("an error occurred", e);
			}
		}
	}
    
	@FXML
	void romTFBtnAction(ActionEvent event) {
		File romDirectory = directoryChooser.showDialog(main.primaryStage);
		if (romDirectory == null) {
			LOGGER.info("No Directory selected");
		} else {
			setRomPath(romDirectory.getAbsolutePath());
			saveSettings();
			cemuTextField.setText(getCemuPath());
			try {
				Runtime.getRuntime().exec("java -jar cemu_UI.jar"); // start again
				System.exit(0); // finishes itself
			} catch (IOException e) {
				LOGGER.error("an error occurred", e);
			}
		}
	}
    
	@FXML
	void updateBtnAction(ActionEvent event) {
		updateController = new UpdateController(this, buildNumber, useBeta);
		Thread updateThread = new Thread(updateController);
		updateThread.setName("Updater");
		updateThread.start();
	}
    
	@FXML
	void autoUpdateToggleBtnAction(ActionEvent event) {
		if (isAutoUpdate()) {
			setAutoUpdate(false);
		} else {
			setAutoUpdate(true);
		}
		saveSettings();
	}
	
	@FXML
	void courseSearchTextFiledAction(ActionEvent event) {
		// not in use
	}
    
    @FXML
    void smmdbDownloadBtnAction(ActionEvent event) {
    	String downloadUrl = "http://smmdb.ddns.net/api/downloadcourse?id=" + id + "&type=zip";
    	String downloadFileURL = getCemuPath() + "/" + id + ".zip";	// getCemuPath() + "/" + smmID + "/" + id + ".rar"
    	String outputFile = getCemuPath() + "/";
    	
    	try {
    		LOGGER.info("beginning download ...");
			HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
			ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", conn.getInputStream());
			ProgressMonitor pm = pmis.getProgressMonitor();
	        pm.setMillisToDecideToPopup(0);
	        pm.setMillisToPopup(0);
	        pm.setMinimum(0);	// tell the progress bar that we start at the beginning of the stream
	        pm.setMaximum(conn.getContentLength());	// tell the progress bar the total number of bytes we are going to read.
			FileUtils.copyInputStreamToFile(pmis, new File(downloadFileURL));	// download file  + "/mlc01/emulatorSave"
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
					LOGGER.info("found smm directory: " + smmDirectory.getAbsolutePath());
					File[] courses = smmDirectory.listFiles(File::isDirectory);

					// get all existing courses in smm directory, new name is highest number +1
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

			if (destination != null) {
				try {			
					ZipFile zipFile = new ZipFile(source);
				    zipFile.extractAll(destination);
				    
				    // rename zip-file
				    File course = new File(destination + "/course000");
				    course.renameTo( new File(destination + "/" + courseName));
				    LOGGER.info("Added new course: " + courseName + ", full path is: " + destination + "/" + courseName);
				} catch (ZipException e) {
				    LOGGER.error("an error occurred during unziping the file!", e);
				}
			}
			
			downloadFile.delete();		
		} catch (IOException e) {
			LOGGER.error("something went wrong during downloading the course", e);
		}
    }
    
	@FXML
	void cemuTextFieldAction(ActionEvent event) {
		setCemuPath(cemuTextField.getText());
		saveSettings();
	}
    
	@FXML
	void romTextFieldAction(ActionEvent event) {
		setRomPath(romTextField.getText());
		saveSettings();
	}
    
	@FXML
	void fullscreenToggleBtnAction(ActionEvent event) {
		if (fullscreen) {
			fullscreen = false;
		} else {
			fullscreen = true;
		}
		saveSettings();
	}
    
    @FXML
    void cloudSyncToggleBtnAction(ActionEvent event) {
    	if(cloudSync) {
    		cloudSync = false;
    	} else {
    		String headingText = "activate cloud savegame sync (beta)";
    	   	String bodyText = "You just activate the cloud savegame sync function of cemu_UI, "
   							+ "\nwhich is currently in beta. Are you sure you want to do this?";
	    	
	    	EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>(){
	    		 @Override
	        	 public void handle(ActionEvent event){
	    			cloudSync = true;
	    			//TODO rework for other cloud services
	    			cloudService = "GoogleDrive";
	    			
	    			// start cloud sync in new thread			
	    			Thread thread = new Thread(new Runnable() {
		    			@Override
						public void run() {
		    				
		    				if (main.cloudController.initializeConnection(getCloudService(), getCemuPath())) {
			        	    	main.cloudController.sync(getCloudService(), getCemuPath());
			        	        saveSettings();
			    	    	} else {
			    	    		cloudSyncToggleBtn.setSelected(false);

			    	    	   	//cloud sync init error dialog
			    	    		String headingText = "Error while initializing cloud sync!";
			    		    	String bodyText = "There was some truble adding your game."
			    		    					+ "\nPlease upload the app.log (which can be found in the cemu_UI directory)"
			    		    					+ "\nto \"https://github.com/Seil0/cemu_UI/issues\" so we can fix this.";
			    	    	   	JFXInfoDialog cloudSyncErrorDialog = new JFXInfoDialog(headingText, bodyText, dialogBtnStyle, 450, 170, main.pane);
			    	    	   	cloudSyncErrorDialog.show();		
			    	    	}
		    				
		    			}
		    		});
		    		thread.start();	
	    		 }
	    	};
	    	
	    	EventHandler<ActionEvent> cancelAction = new EventHandler<ActionEvent>(){
	    		 @Override
	    		 public void handle(ActionEvent event){
	    			 cloudSyncToggleBtn.setSelected(false);
	    		 }
	    	};
	    	
			JFXOkayCancelDialog cloudSyncErrorDialog = new JFXOkayCancelDialog(headingText, bodyText, dialogBtnStyle,
					419, 140, okayAction, cancelAction, main.pane);
	    	cloudSyncErrorDialog.show();	
    	}
    }
    
	@FXML
	void colorPickerAction(ActionEvent event) {
		editColor(colorPicker.getValue().toString());
		applyColor();
	}
    
	@FXML
	void addBtnAction(ActionEvent event) {
		String headingText = "add a new game to cemu_UI";
		String bodyText = "";
		JFXEditGameDialog addGameDialog = new JFXEditGameDialog(headingText, bodyText, dialogBtnStyle, 450, 300, 0,
				this, main.primaryStage, main.pane);
		addGameDialog.show();
	}
    
    /**
     * process the returning data from the addGame dialog
     * and add them to the database and the UI
     */
    public void addBtnReturn(String title, String coverPath, String romPath, String titleID) {
    	File pictureCache;
    	
    	/**
		 * if one parameter dosen't contain any value do not add the game
		 * else convert the cover to .png add copy it into the picture cache,
		 * then add the rom to the local_roms database
		 */
    	System.out.println(romPath.length());
		if (romPath.length() == 0 || coverPath.length() == 0 || title.length() == 0 || titleID.length() == 0) {
			LOGGER.info("No parameter set!");
			
			//addGame error dialog
			String headingTextError = "Error while adding a new Game!";
	    	String bodyTextError = "There was some truble adding your game."
	    						+ "\nOne of the needed values was empty, please try again to add your game."; 
	    	JFXInfoDialog errorDialog = new JFXInfoDialog(headingTextError, bodyTextError, dialogBtnStyle, 350, 170, main.pane);	
	    	errorDialog.show();

		} else {
			String coverName = new File(coverPath).getName();
			try	{
				if (System.getProperty("os.name").equals("Linux")) {
					pictureCache = getPictureCacheLinux();
				} else {
					pictureCache = getPictureCacheWin();
				}
				
			    BufferedImage originalImage = ImageIO.read(new File(coverPath)); //load cover
			    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 400, 600);
			    ImageIO.write(resizeImagePNG, "png", new File(pictureCache+"\\"+coverName)); //save image to pictureCache
			    coverPath = pictureCache+"\\"+coverName;
			} catch (IOException e) {
				LOGGER.error("Ops something went wrong! Error while resizing cover.", e);
			}
			
			try {
				dbController.addGame(title, coverPath, romPath, titleID, "", "", "", "0");
				dbController.loadSingleGame(titleID);
				if (menuTrue) {
					sideMenuSlideOut();
					burgerTask.setRate(-1.0);
					burgerTask.play();
					menuTrue = false;
				}
				refreshUIData();
			} catch (SQLException e) {
				LOGGER.error("Oops, something went wrong! Error while adding a game.", e);
			}
		}
    }

	public void editBtnReturn(String title, String coverPath, String romPath, String titleID) {
		dbController.setGameInfo(title, coverPath, romPath, titleID);
		games.remove(selectedUIDataIndex);
		dbController.loadSingleGame(titleID);
		refreshUIData();

		LOGGER.info("successfully edited " + titleID + ", new name is \"" + title + "\"");
	}

    /**
     * add game to games (ArrayList) and initialize all needed actions (start, time stamps, titleID)
     * @param title : game title
     * @param coverPath : path to cover (cache)
     * @param romPath : path to ROM file (.rpx)
     * @param titleID : game ID
     */
    public void addGame(String title, String coverPath, String romPath, String titleID){
    	VBox VBox = new VBox();
    	Label gameTitleLabel = new Label();
    	JFXButton gameBtn = new JFXButton();
    	ImageView imageView = new ImageView();
    	Image coverImage = new Image(new File(coverPath).toURI().toString());
    	
    	generatePosition();

    	UIROMDataType uiROMElement = new UIROMDataType(VBox, gameTitleLabel, gameBtn, imageView, titleID, romPath);
    	
    	uiROMElement.getLabel().setText(title);
    	uiROMElement.getLabel().setMaxWidth(200);
    	uiROMElement.getLabel().setPadding(new Insets(0,0,0,8));
    	uiROMElement.getLabel().setFont(Font.font("System", FontWeight.BOLD, 14));
    	
    	// i think we can do this locally and remove the imageView from the data type since it's used as graphic
    	uiROMElement.getImageView().setImage(coverImage);
    	uiROMElement.getImageView().setFitHeight(300);
    	uiROMElement.getImageView().setFitWidth(200);
    	
    	uiROMElement.getButton().setGraphic(uiROMElement.getImageView());
    	uiROMElement.getButton().setContextMenu(gameContextMenu);
    	uiROMElement.getButton().setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 3); ");
    	uiROMElement.getButton().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				LOGGER.info("selected: " + title + "; ID: " + titleID);
				// getting the selected game index by comparing event.getSource() with games.get(i).getButton()
				for (int i = 0; i < games.size(); i++) {
					if (games.get(i).getButton() == event.getSource()) {
						selectedUIDataIndex = i;
					}
				}

				gameExecutePath = romPath;
				selectedGameTitleID = titleID;
				selectedGameTitle = title;

				// underlining selected Label
				lastGameLabel.setStyle("-fx-underline: false;");
				games.get(selectedUIDataIndex).getLabel().setStyle("-fx-underline: true;");
				lastGameLabel = games.get(selectedUIDataIndex).getLabel();

				// setting last played, if lastPlayed is empty game was never played before, else set correct date
				if (dbController.getLastPlayed(titleID).equals("") || dbController.getLastPlayed(titleID).equals(null)) {
					lastTimePlayedBtn.setText("Last played, never");
					totalPlaytimeBtn.setText(dbController.getTotalPlaytime(titleID) + " min");
				} else {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

					int today = Integer.parseInt(dtf.format(LocalDate.now()).replaceAll("-", ""));
					int yesterday = Integer.parseInt(dtf.format(LocalDate.now().minusDays(1)).replaceAll("-", ""));
					int lastPlayedDay = Integer.parseInt(dbController.getLastPlayed(titleID).replaceAll("-", ""));

					if (lastPlayedDay == today) {
						lastTimePlayedBtn.setText("Last played, today");
					} else if (lastPlayedDay == yesterday) {
						lastTimePlayedBtn.setText("Last played, yesterday");
					} else {
						lastTimePlayedBtn.setText("Last played, " + dbController.getLastPlayed(titleID));
					}
				}

				/**
				 * setting total playtime, if total playtime > 60 minutes, format is "x hours x
				 * minutes" (x h x min), else only minutes are showed
				 */
				if (Integer.parseInt(dbController.getTotalPlaytime(titleID)) > 60) {
					int hoursPlayed = (int) Math.floor(Integer.parseInt(dbController.getTotalPlaytime(titleID)) / 60);
					int minutesPlayed = Integer.parseInt(dbController.getTotalPlaytime(titleID)) - 60 * hoursPlayed;
					totalPlaytimeBtn.setText(hoursPlayed + " h     " + minutesPlayed + " min");
				} else {
					totalPlaytimeBtn.setText(dbController.getTotalPlaytime(titleID) + " min");
				}

				if (!playTrue) {
					playBtnSlideIn();
				}
				if (menuTrue) {
					sideMenuSlideOut();
					burgerTask.setRate(-1.0);
					burgerTask.play();
					menuTrue = false;
				}

			}
		});
    	
    	uiROMElement.getVBox().setLayoutX(getxPos());
    	uiROMElement.getVBox().setLayoutY(getyPos());
    	uiROMElement.getVBox().getChildren().addAll(gameTitleLabel,gameBtn);
    	
    	// add uiROMElement to games list
    	games.add(uiROMElement);
    }
    
	// add all games saved in games(ArrayList) to gamesAnchorPane
	void addUIData() {
		for (int i = 0; i < games.size(); i++) {
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
		double width;

		if (mainAnchorPane.getWidth() < 10) {
			width = mainAnchorPane.getPrefWidth();
		} else {
			width = mainAnchorPane.getWidth();
		}
		playBtn.setLayoutX((width / 2) - 50);
		totalPlaytimeBtn.setLayoutX((width / 2) - 50 - 20.5 - 100);
		lastTimePlayedBtn.setLayoutX((width / 2) + 50 + 20.5);
	}

	private void checkAutoUpdate() {

		if (isAutoUpdate()) {
			try {
				LOGGER.info("AutoUpdate: looking for updates on startup ...");
				updateController = new UpdateController(this, buildNumber, useBeta);
				Thread updateThread = new Thread(updateController);
				updateThread.setName("Updater");
				updateThread.start();
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		courseText.add(7, new Text(new java.util.Date((long) course.getLastmodified() * 1000) + "\n"));
		courseText.add(8, new Text(new java.util.Date((long) course.getUploaded() * 1000) + "\n"));
		courseText.add(9, new Text(course.getNintendoid() + "\n"));

		for (int i = 0; i < nameText.size(); i++) {
			nameText.get(i).setFont(Font.font("System", FontWeight.BOLD, 14));
			courseText.get(i).setFont(Font.font("System", 14));
			smmdbTextFlow.getChildren().addAll(nameText.get(i), courseText.get(i));
		}

	}

    /**
     * xPosHelper based on window width = -24(Windows)/-36(Linux)
     * calculates how many games can be displayed in one row
     */
    private void generatePosition() {
    	int xPosHelperMax;
    	
    	/**FIXME somehow the window width is set to 8, if we can find a way to get always the real window with
    	*(at the beginning we have to use prefWidth after resizing Width) we can remove this
    	*/
    	if (mainAnchorPane.getWidth() < 10) {
    		xPosHelperMax = (int) Math.floor((mainAnchorPane.getPrefWidth() - 36) / 217);
    	} else {
    		xPosHelperMax = (int) Math.floor((mainAnchorPane.getWidth() - 36) / 217);
    	}
    	
    	if(xPosHelper == xPosHelperMax){
    		oldXPosHelper = xPosHelper;
    		xPos = 17;
    		yPos = yPos + 345;		
    		xPosHelper = 1;
    	}else{
    		xPos = xPos + 217;
    		xPosHelper++;
    	}
    	
//    	System.out.println("Breit: " + main.pane.getWidth());
//    	System.out.println("Breit2: " + mainAnchorPane.getWidth());
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
		
		if (icolor.compareTo(ccolor) == -1) {
			dialogBtnStyle = btnStyleWhite;

			aboutBtn.setStyle("-fx-text-fill: WHITE;");
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			addBtn.setStyle("-fx-text-fill: WHITE;");
			reloadRomsBtn.setStyle("-fx-text-fill: WHITE;");
			smmdbBtn.setStyle("-fx-text-fill: WHITE;");
			playBtn.setStyle("-fx-text-fill: WHITE; -fx-font-family: Roboto Medium;");
			cemuTFBtn.setStyle(btnStyleWhite);
			romTFBtn.setStyle(btnStyleWhite);
			updateBtn.setStyle(btnStyleWhite);
			smmdbDownloadBtn.setStyle(btnStyleWhite);
			playBtn.setStyle(btnStyleWhite);

			aboutBtn.setGraphic(info_white);
			settingsBtn.setGraphic(settings_white);
			addBtn.setGraphic(add_circle_white);
			reloadRomsBtn.setGraphic(cached_white);
			smmdbBtn.setGraphic(smmdb_white);

			menuHam.getStyleClass().add("jfx-hamburgerW");
		} else {
			dialogBtnStyle = btnStyleBlack;

			aboutBtn.setStyle("-fx-text-fill: BLACK;");
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			addBtn.setStyle("-fx-text-fill: BLACK;");
			reloadRomsBtn.setStyle("-fx-text-fill: BLACK;");
			smmdbBtn.setStyle("-fx-text-fill: BLACK;");
			playBtn.setStyle("-fx-text-fill: BLACK; -fx-font-family: Roboto Medium;");
			cemuTFBtn.setStyle(btnStyleBlack);
			romTFBtn.setStyle(btnStyleBlack);
			updateBtn.setStyle(btnStyleBlack);
			smmdbDownloadBtn.setStyle(btnStyleBlack);
			playBtn.setStyle(btnStyleBlack);

			aboutBtn.setGraphic(info_black);
			settingsBtn.setGraphic(settings_black);
			addBtn.setGraphic(add_circle_black);
			reloadRomsBtn.setGraphic(cached_black);
			smmdbBtn.setGraphic(smmdb_black);

			menuHam.getStyleClass().add("jfx-hamburgerB");
		}

		for (int i = 0; i < games.size(); i++) {
			games.get(i).getButton().setRipplerFill(Paint.valueOf(getColor()));
		}
	}
		
    public void saveSettings(){
    	LOGGER.info("saving Settings ...");
    	OutputStream outputStream;	//new output-stream
    	try {
    		props.setProperty("cemuPath", getCemuPath());
			props.setProperty("romPath", getRomPath());
			props.setProperty("color", getColor());
			props.setProperty("fullscreen", String.valueOf(isFullscreen()));
			props.setProperty("cloudSync", String.valueOf(isCloudSync()));
			props.setProperty("autoUpdate", String.valueOf(isAutoUpdate()));
			props.setProperty("useBeta", String.valueOf(isUseBeta()));
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
    private void loadSettings(){
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
				setAutoUpdate(Boolean.parseBoolean(props.getProperty("autoUpdate")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				setAutoUpdate(false);
			}
			
			try {
				setUseBeta(Boolean.parseBoolean(props.getProperty("useBeta")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				setUseBeta(false);
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
		//slide in in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(-175);
		translateTransition.setToX(0);
		translateTransition.play();
	}
	
	private void sideMenuSlideOut(){
		//slide out in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(0);
		translateTransition.setToX(-175);
		translateTransition.play();
	}
	
	private void playBtnSlideIn(){
		playBtn.setVisible(true);
		lastTimePlayedBtn.setVisible(true);
		totalPlaytimeBtn.setVisible(true);
		playTrue = true;
		
		TranslateTransition playBtnTransition = new TranslateTransition(Duration.millis(300), playBtn);
		playBtnTransition.setFromY(56);
		playBtnTransition.setToY(0);
		playBtnTransition.play();
		
		TranslateTransition lastTimePlayedBtnTransition = new TranslateTransition(Duration.millis(300), lastTimePlayedBtn);
		lastTimePlayedBtnTransition.setFromY(56);
		lastTimePlayedBtnTransition.setToY(0);
		lastTimePlayedBtnTransition.play();
		
		TranslateTransition timePlayedBtnTransition = new TranslateTransition(Duration.millis(300), totalPlaytimeBtn);
		timePlayedBtnTransition.setFromY(56);
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
		LOGGER.info(getColor());
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

	public File getPictureCacheLinux() {
		return pictureCacheLinux;
	}

	public void setPictureCacheLinux(File pictureCacheLinux) {
		this.pictureCacheLinux = pictureCacheLinux;
	}

	public File getPictureCacheWin() {
		return pictureCacheWin;
	}

	public void setPictureCacheWin(File pictureCacheWin) {
		this.pictureCacheWin = pictureCacheWin;
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

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public boolean isUseBeta() {
		return useBeta;
	}

	public void setUseBeta(boolean useBeta) {
		this.useBeta = useBeta;
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
	
	public int getOldXPosHelper() {
		return oldXPosHelper;
	}

	public void setOldXPosHelper(int oldXPosHelper) {
		this.oldXPosHelper = oldXPosHelper;
	}

	public AnchorPane getMainAnchorPane() {
		return mainAnchorPane;
	}

	public JFXButton getUpdateBtn() {
		return updateBtn;
	}

}
