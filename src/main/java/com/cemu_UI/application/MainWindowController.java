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
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.controller.DBController;
import com.cemu_UI.controller.SmmdbAPIController;
import com.cemu_UI.controller.UpdateController;
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
	private JFXTextField executeCommandTextFiled;

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
	private ChoiceBox<String> languageChoisBox;

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
	private HBox bottomHBox;

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
	private Label languageLbl;

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
	DBController dbController;
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
	private String romDirectoryPath;
	private String gameExecutePath;
	private String color;
	private String dialogBtnStyle;
	private String selectedGameTitleID;
	private String selectedGameTitle;
	private String id;
	private String version = "0.3.0";
	private String buildNumber = "077";
	private String versionName = "Puzzle Plank Galaxy";
	private int xPos = -200;
	private int yPos = 17;
	private int xPosHelper;
	private int oldXPosHelper;
	private int selectedUIDataIndex;
	private int selected;
	private long lastLocalSync;
	private double windowWidth;
	private double windowHeight;
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	private ObservableList<String> branches = FXCollections.observableArrayList("stable", "beta");
	private ObservableList<String> languages = FXCollections.observableArrayList("English (en_US)", "Deutsch (de_DE)");
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
	
	// language support
	private ResourceBundle bundle;
	private String language;
	private String editHeadingText;
	private String editBodyText;
	private String removeHeadingText;
	private String removeBodyText;
	private String addUpdateHeadingText;
	private String addUpdateBodyText;
	private String addDLCHeadingText;
	private String addDLCBodyText;
	private String licensesLblHeadingText;
	private String licensesLblBodyText;
	private String showLicenses;
	private String aboutBtnHeadingText;
	private String aboutBtnBodyText;
	private String cloudSyncWaringHeadingText;
	private String cloudSyncWaringBodyText;
	private String cloudSyncErrorHeadingText;
	private String cloudSyncErrorBodyText;
	private String addGameBtnHeadingText;
	private String addGameBtnBodyText;
	private String addBtnReturnErrorHeadingText;
	private String addBtnReturnErrorBodyText;
	private String lastPlayed;
	private String today;
	private String yesterday;
	private String never;
	
	private String playBtnPlay;
	private String playBtnUpdating;
	private String playBtnCopyingFiles;
	private String smmdbDownloadBtnLoading;
	private String smmdbDownloadBtnDownload;
	
	public void setMain(Main m) {
		this.main = m;
		dbController = new DBController(main, this);
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
		
		cemuTextField.setText(cemuPath);
		romTextField.setText(romDirectoryPath);
		colorPicker.setValue(Color.valueOf(getColor()));
		fullscreenToggleBtn.setSelected(isFullscreen());
		cloudSyncToggleBtn.setSelected(isCloudSync());
		autoUpdateToggleBtn.setSelected(isAutoUpdate());
		branchChoisBox.setItems(branches);
		languageChoisBox.setItems(languages);
		bottomHBox.setPickOnBounds(false);
		
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
		
		setUILanguage();
		
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
					String headingText = editHeadingText + " \"" + selectedGameTitle + "\"";
					JFXEditGameDialog editGameDialog = new JFXEditGameDialog(headingText, editBodyText, dialogBtnStyle, 450,
							300, 1, MWC, main.getPrimaryStage(), main.getPane());
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
					String headingText = removeHeadingText + " \"" + selectedGameTitle + "\"";
					String bodyText = removeBodyText + " " + selectedGameTitle + " ?";
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
							LOGGER.info("Action canceld by user!");
						}
					};

					JFXOkayCancelDialog removeGameDialog = new JFXOkayCancelDialog(headingText, bodyText,
							dialogBtnStyle, 350, 170, okayAction, cancelAction, main.getPane(), bundle);
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
					String headingText = addUpdateHeadingText + " \"" + selectedGameTitle + "\"";
					EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							DirectoryChooser directoryChooser = new DirectoryChooser();
							File selectedDirecroty = directoryChooser.showDialog(main.getPrimaryStage());
							String updatePath = selectedDirecroty.getAbsolutePath();
							String[] parts = selectedGameTitleID.split("-"); // split string into 2 parts at "-"
							File srcDir = new File(updatePath);
							File destDir = new File(cemuPath + "/mlc01/usr/title/" + parts[0] + "/" + parts[1]);

							// if directory doesn't exist create it
							if (destDir.exists() != true) {
								destDir.mkdir();
							}

							try {
								LOGGER.info("copying the content of " + updatePath + " to " + destDir.toString());
								playBtn.setText(playBtnUpdating);
								playBtn.setDisable(true);
								FileUtils.copyDirectory(srcDir, destDir);
								playBtn.setText(playBtnPlay);
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
							LOGGER.info("Action canceld by user!");
						}
					};

					JFXOkayCancelDialog updateGameDialog = new JFXOkayCancelDialog(headingText, addUpdateBodyText,
							dialogBtnStyle, 350, 170, okayAction, cancelAction, main.getPane(), bundle);
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
					String headingText = addDLCHeadingText + " \"" + selectedGameTitle + "\"";
					EventHandler<ActionEvent> okayAction = new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							DirectoryChooser directoryChooser = new DirectoryChooser();
							File selectedDirecroty = directoryChooser.showDialog(main.getPrimaryStage());
							String dlcPath = selectedDirecroty.getAbsolutePath();
							String[] parts = selectedGameTitleID.split("-"); // split string into 2 parts at "-"
							File srcDir = new File(dlcPath);
							File destDir = new File(cemuPath + "/mlc01/usr/title/" + parts[0] + "/" + parts[1] + "/aoc");

							// if directory doesn't exist create it
							if (destDir.exists() != true) {
								destDir.mkdir();
							}

							try {
								LOGGER.info("copying the content of " + dlcPath + " to " + destDir.toString());
								playBtn.setText(playBtnCopyingFiles);
								playBtn.setDisable(true);
								FileUtils.copyDirectory(srcDir, destDir);
								playBtn.setText(playBtnPlay);
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
							LOGGER.info("Action canceld by user!");
						}
					};

					JFXOkayCancelDialog addDLCDialog = new JFXOkayCancelDialog(headingText, addDLCBodyText, dialogBtnStyle,
							350, 170, okayAction, cancelAction, main.getPane(), bundle);
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

				id = idColumn.getCellData(selected); // get name of selected item

				for (int i = 0; i < courses.size(); i++) {
					if (courses.get(i).getId() == id) {
						try {
							URL url = new URL("https://smmdb.ddns.net/courseimg/" + id + "_full?v=1");
							Image sourceImage = new Image(url.toURI().toString());
							
							// scale image to 142px
							double scalefactor = 142 / sourceImage.getHeight(); // calculate scaling factor
							int nWidth = (int) Math.rint(scalefactor * sourceImage.getWidth());
							int nHeight = (int) Math.rint(scalefactor * sourceImage.getHeight());
							Image scaledImage = new Image(url.toURI().toString(), nWidth, nHeight, false, true); // generate a scaled image
							
							smmdbImageView.setFitWidth(scaledImage.getWidth()); // set ImageView width to the image width 
							smmdbImageView.setImage(scaledImage); // set imageview to image
						} catch (MalformedURLException | URISyntaxException e) {
							LOGGER.warn("There was either a problem or no image!", e);
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
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Desktop.getDesktop().browse(new URI("https://github.com/Seil0/cemu_UI/issues/3"));
							} catch (IOException | URISyntaxException e) {
								LOGGER.error("An error ocoured while trying to open a Website.", e);
							}
						}
					});
					thread.start();
				}
			}
		});
		
        languageChoisBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
  		public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) {
          	  String language = languageChoisBox.getItems().get((int) new_value).toString();
          	language = language.substring(language.length()-6,language.length()-1);	//reading only en_US from English (en_US)
          	  setLanguage(language);
          	  setUILanguage();
          	  saveSettings();
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
									dialogBtnStyle, 510, 450, main.getPane());
							licenseDialog.show();
							licenseDialog.getTextArea().setEditable(false);
						}
					};
		        	
					JFXOkayCancelDialog licenseOverviewDialog = new JFXOkayCancelDialog(licensesLblHeadingText,
							licensesLblBodyText, dialogBtnStyle, 350, 275, okayAction, cancelAction, main.getPane(),
							bundle);
					licenseOverviewDialog.setCancelText(showLicenses);
					licenseOverviewDialog.show();
		        }
		    }
		});
        
        cemuTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (new File(newValue).exists()) {
					setCemuPath(newValue);
					saveSettings();
				} else {
					String bodyText = newValue + ": No such file or directory";
					JFXInfoDialog fileErrorDialog = new JFXInfoDialog("Waring!", bodyText, dialogBtnStyle, 190, 150, main.getPane());
					fileErrorDialog.show();
					LOGGER.warn(newValue + ": No such file or directory");
				}
			}
		});
        
        romTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (new File(newValue).exists()) {
					setRomDirectoryPath(newValue);
		    		saveSettings();
					reloadRoms();
				} else {
					String bodyText = newValue + ": No such file or directory";
					JFXInfoDialog fileErrorDialog = new JFXInfoDialog("Waring!", bodyText, dialogBtnStyle, 190, 150, main.getPane());
					fileErrorDialog.show();
					LOGGER.warn(newValue + ": No such file or directory");			
				}
			}
		});

		LOGGER.info("initializing Actions done!");
	}
    
	@FXML
	void detailsSlideoutBtnAction() {
		playBtnSlideOut();
	}
    
    @FXML
    void aboutBtnAction() {
    	String bodyText = "cemu_UI by @Seil0 \nVersion: " + version + " (" + buildNumber + ")  \"" + versionName + "\" \n"
    					+ aboutBtnBodyText;
    	JFXInfoDialog aboutDialog = new JFXInfoDialog(aboutBtnHeadingText, bodyText, dialogBtnStyle, 350, 200, main.getPane());
    	aboutDialog.show();
    }

	@FXML
	void settingsBtnAction() {
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
		reloadRoms();
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
						smmdbDownloadBtn.setText(smmdbDownloadBtnLoading);
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
							smmdbDownloadBtn.setText(smmdbDownloadBtnDownload);
							smmdbDownloadBtn.setDisable(false);
		                });
					}
				}
			});
			thread.start();
		}
	}
    	
	@FXML
	void playBtnAction() throws InterruptedException, IOException {
		dbController.setLastPlayed(selectedGameTitleID);
		playGame = new playGame(this, dbController);

		playGame.start();
	}

	@FXML
	void totalPlaytimeBtnAction() {
		
	}

	@FXML
	void lastTimePlayedBtnAction() {
		
	}
    
	@FXML
	void cemuTFBtnAction() {
		File cemuDirectory = directoryChooser.showDialog(main.getPrimaryStage());
		if (cemuDirectory != null) {
			cemuTextField.setText(cemuDirectory.getAbsolutePath());
		}
	}
    
	@FXML
	void romTFBtnAction() {
		File romDirectory = directoryChooser.showDialog(main.getPrimaryStage());
		if (romDirectory != null) {
			romTextField.setText(romDirectory.getAbsolutePath());
		}
	}
    
	@FXML
	void updateBtnAction() {
		updateController = new UpdateController(this, buildNumber, useBeta);
		Thread updateThread = new Thread(updateController);
		updateThread.setName("Updater");
		updateThread.start();
	}
    
	@FXML
	void autoUpdateToggleBtnAction() {
		if (isAutoUpdate()) {
			setAutoUpdate(false);
		} else {
			setAutoUpdate(true);
		}
		saveSettings();
	}
	
	@FXML
	void courseSearchTextFiledAction() {
		// not in use
	}
    
    @FXML
    void smmdbDownloadBtnAction() {
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
	void fullscreenToggleBtnAction() {
		if (fullscreen) {
			fullscreen = false;
		} else {
			fullscreen = true;
		}
		saveSettings();
	}
    
    @FXML
    void cloudSyncToggleBtnAction() {
    	if(cloudSync) {
    		cloudSync = false;
    	} else {
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

							if (main.getCloudController().initializeConnection(getCloudService(), getCemuPath())) {
								main.getCloudController().sync(getCloudService(), getCemuPath(), main.getDirectory().getPath());
								saveSettings();
							} else {
								cloudSyncToggleBtn.setSelected(false);

								// cloud sync init error dialog
								JFXInfoDialog cloudSyncErrorDialog = new JFXInfoDialog(cloudSyncErrorHeadingText,
										cloudSyncErrorBodyText, dialogBtnStyle, 450, 170, main.getPane());
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
	    	
			JFXOkayCancelDialog cloudSyncWarningDialog = new JFXOkayCancelDialog(cloudSyncWaringHeadingText,
					cloudSyncWaringBodyText, dialogBtnStyle, 419, 140, okayAction, cancelAction, main.getPane(),
					bundle);
			cloudSyncWarningDialog.show();
    	}
    }
    
	@FXML
	void colorPickerAction() {
		editColor(colorPicker.getValue().toString());
		applyColor();
	}
    
	@FXML
	void addBtnAction() {
		String headingText = addGameBtnHeadingText;
		String bodyText = addGameBtnBodyText;
		JFXEditGameDialog addGameDialog = new JFXEditGameDialog(headingText, bodyText, dialogBtnStyle, 450, 300, 0,
				this, main.getPrimaryStage(), main.getPane());
		addGameDialog.show();
	}
    
    /**
     * process the returning data from the addGame dialog
     * and add them to the database and the UI
     */
    public void addBtnReturn(String title, String coverPath, String romPath, String titleID) {
    	/**
		 * if one parameter dosen't contain any value do not add the game
		 * else convert the cover to .png add copy it into the picture cache,
		 * then add the rom to the local_roms database
		 */
		if (romPath.length() == 0 || coverPath.length() == 0 || title.length() == 0 || titleID.length() == 0) {
			LOGGER.info("No parameter set!");
			
			//addGame error dialog
			JFXInfoDialog errorDialog = new JFXInfoDialog(addBtnReturnErrorHeadingText, addBtnReturnErrorBodyText,
					dialogBtnStyle, 350, 170, main.getPane());
			errorDialog.show();

		} else {	
	    	File pictureCache = main.getPictureCache();
			String coverName = new File(coverPath).getName();
			try	{
			    BufferedImage originalImage = ImageIO.read(new File(coverPath)); //load cover
			    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 400, 600);
			    coverPath = pictureCache + "/" + coverName;
			    ImageIO.write(resizeImagePNG, "png", new File(coverPath)); //save image to pictureCache
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
					lastTimePlayedBtn.setText(lastPlayed + never);
					totalPlaytimeBtn.setText(dbController.getTotalPlaytime(titleID) + " min");
				} else {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

					int tToday = Integer.parseInt(dtf.format(LocalDate.now()).replaceAll("-", ""));
					int tYesterday = Integer.parseInt(dtf.format(LocalDate.now().minusDays(1)).replaceAll("-", ""));
					int tLastPlayedDay = Integer.parseInt(dbController.getLastPlayed(titleID).replaceAll("-", ""));

					if (tLastPlayedDay == tToday) {
						lastTimePlayedBtn.setText(lastPlayed + today);
					} else if (tLastPlayedDay == tYesterday) {
						lastTimePlayedBtn.setText(lastPlayed + yesterday);
					} else {
						lastTimePlayedBtn.setText(lastPlayed + dbController.getLastPlayed(titleID));
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
    
    /**
     * reload all ROMs from the ROM directory
     */
    public void reloadRoms() {
    	JFXSpinner spinner = new JFXSpinner();
		spinner.setPrefSize(30, 30);
		AnchorPane.setTopAnchor(spinner, (main.getPane().getPrefHeight()-spinner.getPrefHeight())/2);
    	AnchorPane.setLeftAnchor(spinner, (main.getPane().getPrefWidth()-spinner.getPrefWidth())/2);
    	
    	Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					//remove all games form gamesAnchorPane
			    	gamesAnchorPane.getChildren().removeAll(gamesAnchorPane.getChildren());
					main.getPane().getChildren().add(spinner); // add spinner to pane
                });
				
				dbController.loadRomDirectory(getRomDirectoryPath()); // reload the ROM directory
				games.removeAll(games); // remove all games from the mwc game list
				dbController.loadAllGames(); // load all games from the database to the mwc
				
				Platform.runLater(() -> {
					refreshUIData(); // refresh the list of games displayed on screen
					main.getPane().getChildren().remove(spinner);
                });
			}
		});
		thread.start();
    }
    
    //remove all games from gamesAnchorPane and add them afterwards
    public void refreshUIData() {
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
    
    // set the selected local strings to all needed elements
    void setUILanguage(){
		switch(getLanguage()){
		case "en_US":	
			bundle = ResourceBundle.getBundle("locals.cemu_UI-Local", Locale.US);	//us_English
			languageChoisBox.getSelectionModel().select(0);
			break;
     	case "de_DE":	
     		bundle = ResourceBundle.getBundle("locals.cemu_UI-Local", Locale.GERMAN);	//German
     		languageChoisBox.getSelectionModel().select(1);
			break;
     	default:		
     		bundle = ResourceBundle.getBundle("locals.cemu_UI-Local", Locale.US);	//default local
     		languageChoisBox.getSelectionModel().select(0);
			break;
		 }
		
		// Buttons
		aboutBtn.setText(bundle.getString("aboutBtn"));
		settingsBtn.setText(bundle.getString("settingsBtn"));
		addBtn.setText(bundle.getString("addBtn"));
		reloadRomsBtn.setText(bundle.getString("reloadRomsBtn"));
		smmdbBtn.setText(bundle.getString("smmdbBtn"));
		cemuTFBtn.setText(bundle.getString("cemuTFBtn"));
		romTFBtn.setText(bundle.getString("romTFBtn"));
		updateBtn.setText(bundle.getString("updateBtnCheckNow"));
		smmdbDownloadBtn.setText(bundle.getString("smmdbDownloadBtn"));
		playBtn.setText(bundle.getString("playBtn"));
		cloudSyncToggleBtn.setText(bundle.getString("cloudSyncToggleBtn"));
		autoUpdateToggleBtn.setText(bundle.getString("autoUpdateToggleBtn"));
		fullscreenToggleBtn.setText(bundle.getString("fullscreenToggleBtn"));
		
		// Labels
		cemu_UISettingsLbl.setText(bundle.getString("cemu_UISettingsLbl"));
		cemuDirectoryLbl.setText(bundle.getString("cemuDirectoryLbl"));
		romDirectoryLbl.setText(bundle.getString("romDirectoryLbl"));
		mainColorLbl.setText(bundle.getString("mainColorLbl"));
		languageLbl.setText(bundle.getString("languageLbl"));
		updateLbl.setText(bundle.getString("updateLbl"));
		branchLbl.setText(bundle.getString("branchLbl"));
		cemuSettingsLbl.setText(bundle.getString("cemuSettingsLbl"));
		licensesLbl.setText(bundle.getString("licensesLbl"));
		
		// Columns
		titleColumn.setText(bundle.getString("titleColumn"));
		idColumn.setText(bundle.getString("idColumn"));
		starsColumn.setText(bundle.getString("starsColumn"));
		timeColumn.setText(bundle.getString("timeColumn"));
		
		// Strings
		editHeadingText = bundle.getString("editHeadingText");
		editBodyText = bundle.getString("editBodyText");
		removeHeadingText = bundle.getString("removeHeadingText");
		removeBodyText = bundle.getString("removeBodyText");
		addUpdateHeadingText = bundle.getString("addUpdateHeadingText");
		addUpdateBodyText = bundle.getString("addUpdateBodyText");
		addDLCHeadingText = bundle.getString("addDLCHeadingText");
		addDLCBodyText = bundle.getString("addDLCBodyText");
		licensesLblHeadingText = bundle.getString("licensesLblHeadingText");
		licensesLblBodyText = bundle.getString("licensesLblBodyText");
		showLicenses = bundle.getString("showLicenses");
		aboutBtnHeadingText = bundle.getString("aboutBtnHeadingText");
		aboutBtnBodyText = bundle.getString("aboutBtnBodyText");
		cloudSyncWaringHeadingText = bundle.getString("cloudSyncWaringHeadingText");
		cloudSyncWaringBodyText = bundle.getString("cloudSyncWaringBodyText");
		cloudSyncErrorHeadingText = bundle.getString("cloudSyncErrorHeadingText");
		cloudSyncErrorBodyText = bundle.getString("cloudSyncErrorBodyText");
		addGameBtnHeadingText = bundle.getString("addGameBtnHeadingText");
		addGameBtnBodyText = bundle.getString("addGameBtnBodyText");
		addBtnReturnErrorHeadingText = bundle.getString("addBtnReturnErrorHeadingText");
		addBtnReturnErrorBodyText = bundle.getString("addBtnReturnErrorBodyText");
		lastPlayed = bundle.getString("lastPlayed");
		today = bundle.getString("today");
		yesterday = bundle.getString("yesterday");
		never = bundle.getString("never");
		
		playBtnPlay = bundle.getString("playBtnPlay");
		playBtnUpdating = bundle.getString("playBtnUpdating");
		playBtnCopyingFiles = bundle.getString("playBtnCopyingFiles");
		smmdbDownloadBtnLoading = bundle.getString("smmdbDownloadBtnLoading");
		smmdbDownloadBtnDownload = bundle.getString("smmdbDownloadBtnDownload");
	}

    // if AutoUpdate, then check for updates
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
    	*This is caused by the time the game objects are generates,
    	*it's before the window is opened so it's size is > 10
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
    
    // change the color of all needed GUI elements
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
    
    /**
     * save settings to the config.xml file
     */
	public void saveSettings() {
		LOGGER.info("saving Settings ...");

		try {
			props.setProperty("cemuPath", getCemuPath());
			props.setProperty("romPath", getRomDirectoryPath());
			props.setProperty("color", getColor());
			props.setProperty("language", getLanguage());
			props.setProperty("fullscreen", String.valueOf(isFullscreen()));
			props.setProperty("cloudSync", String.valueOf(isCloudSync()));
			props.setProperty("autoUpdate", String.valueOf(isAutoUpdate()));
			props.setProperty("useBeta", String.valueOf(isUseBeta()));
			if (getCloudService() == null) {
				props.setProperty("cloudService", "");
			} else {
				props.setProperty("cloudService", getCloudService());
			}
			props.setProperty("folderID", main.getCloudController().getFolderID(getCloudService()));
			props.setProperty("lastLocalSync", String.valueOf(getLastLocalSync()));
			props.setProperty("windowWidth", String.valueOf(mainAnchorPane.getWidth()));
			props.setProperty("windowHeight", String.valueOf(mainAnchorPane.getHeight()));

			OutputStream outputStream = new FileOutputStream(main.getConfigFile()); // new output-stream
			props.storeToXML(outputStream, "cemu_UI settings"); // write new .xml
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
	private void loadSettings() {
		LOGGER.info("loading settings ...");
		try {
			InputStream inputStream = new FileInputStream(main.getConfigFile());
			props.loadFromXML(inputStream); // new input-stream from .xml

			try {
				setCemuPath(props.getProperty("cemuPath"));
			} catch (Exception e) {
				LOGGER.error("cloud not load cemuPath", e);
				setCemuPath("");
			}

			try {
				setRomDirectoryPath(props.getProperty("romPath"));
			} catch (Exception e) {
				LOGGER.error("could not load romPath", e);
				setRomDirectoryPath("");
			}

			try {
				setColor(props.getProperty("color"));
			} catch (Exception e) {
				LOGGER.error("could not load color value, setting default instead", e);
				setColor("00a8cc");
			}

			if (props.getProperty("language") == null) {
				LOGGER.error("cloud not load language, setting default instead");
				setLanguage("en_US");
			} else {
				setLanguage(props.getProperty("language"));
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
				main.getCloudController().setFolderID(props.getProperty("folderID"), getCloudService());
			} catch (Exception e) {
				LOGGER.error("could not load folderID, disable cloud sync. Please contact an developer", e);
				setCloudSync(false);
			}

			try {
				setLastLocalSync(Long.parseLong(props.getProperty("lastLocalSync")));
			} catch (Exception e) {
				LOGGER.error("could not load lastSuccessSync, setting default instead", e);
				setLastLocalSync(0);
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
		bottomHBox.setVisible(true);
		playTrue = true;
		
		TranslateTransition playBtnTransition = new TranslateTransition(Duration.millis(300), bottomHBox);
		playBtnTransition.setFromY(56);
		playBtnTransition.setToY(0);
		playBtnTransition.play();
	}
	
	private void playBtnSlideOut(){
		playTrue = false;
		TranslateTransition playBtnTransition = new TranslateTransition(Duration.millis(300), bottomHBox);
		playBtnTransition.setFromY(0);
		playBtnTransition.setToY(56);
		playBtnTransition.play();
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
	
	public String getRomDirectoryPath() {
		return romDirectoryPath;
	}

	public void setRomDirectoryPath(String romDirectoryPath) {
		this.romDirectoryPath = romDirectoryPath;
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

	public long getLastLocalSync() {
		return lastLocalSync;
	}

	public void setLastLocalSync(long lastLocalSync) {
		this.lastLocalSync = lastLocalSync;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public AnchorPane getMainAnchorPane() {
		return mainAnchorPane;
	}

	public JFXButton getUpdateBtn() {
		return updateBtn;
	}

}
