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


package com.cemu_UI.uiElements;

import java.io.File;

import com.cemu_UI.application.MainWindowController;
import com.cemu_UI.datatypes.EditDataType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class JFXEditGameDialog {
	
	private String headingText;
	private String bodyText;
	private String dialogBtnStyle;
	private int dialogWidth;
	private int dialogHeight;
	private EditDataType gameData;
	private Stage stage;
	private Pane pane;
	private MainWindowController mwc;

	/**
	 * Creates a new JFoenix Dialog to show some information with okay and cancel option
	 * @param headingText Heading Text, just the heading
	 * @param bodyText body Text, all other text belongs here
	 * @param dialogBtnStyle Style of the okay button
	 * @param dialogWidth dialog width
	 * @param dialogHeight dialog height
	 * @param stage the primary stage
	 * @param pane pane to which the dialog belongs
	 */
	public JFXEditGameDialog(String headingText, String bodyText, String dialogBtnStyle, int dialogWidth,
			int dialogHeight, MainWindowController mwc, Stage stage, Pane pane) {
		this.headingText = headingText;
		this.bodyText = bodyText;
		this.dialogBtnStyle = dialogBtnStyle;
		this.dialogWidth = dialogWidth;
		this.dialogHeight = dialogHeight;
		this.mwc = mwc;
		this.stage = stage;
		this.pane = pane;
	}

	public void show() { 
		JFXDialogLayout content = new JFXDialogLayout();
    	StackPane stackPane = new StackPane();
    	stackPane.autosize();
    	JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.LEFT, true);
    	
    	Label placeholder = new Label();
    	placeholder.setPrefSize(15, 10);
    		
    	TextField gameTitleTF = new TextField();
    	gameTitleTF.setPromptText("game tile");
    	TextField gameTitleIDTF = new TextField();
    	gameTitleIDTF.setPromptText("title ID");
    	TextField romPathTF = new TextField();
    	romPathTF.setPromptText("ROM path");
    	TextField gameCoverTF = new TextField();
    	gameCoverTF.setPromptText("cover path");
    	
    	JFXButton okayBtn = new JFXButton("Okay");
    	okayBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println(romPathTF.getText());
				System.out.println(gameCoverTF.getText());
				System.out.println(gameTitleTF.getText());
				System.out.println(gameTitleIDTF.getText());
				if (romPathTF.getText().toString().length() == 0 || gameCoverTF.getText().toString().length() == 0 || 
					gameTitleTF.getText().toString().length() == 0 || gameTitleIDTF.getText().toString().length() == 0) {
					
//					LOGGER.info("No parameter set!");
					
					//addGame error dialog
					String headingTextError = "Error while adding a new Game!";
			    	String bodyTextError = "There was some truble adding your game."
			    						+ "\nOne of the needed values was empty, please try again to add your game."; 
			    	JFXInfoDialog errorDialog = new JFXInfoDialog(headingTextError, bodyTextError, dialogBtnStyle, 350, 170, pane);
			    	errorDialog.show();
				} else {
					gameData.setRomPath(romPathTF.getText().toString());
					gameData.setCoverPath(gameCoverTF.getText().toString());
					gameData.setTitle(gameTitleTF.getText().toString());
					gameData.setTitleID(gameTitleIDTF.getText().toString());
					mwc.addBtnReturn(gameData);
		    		dialog.close();
				}
			}
    	});	
    	okayBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
    	okayBtn.setPrefHeight(32);
    	okayBtn.setStyle(dialogBtnStyle);
    	
    	JFXButton cancelBtn = new JFXButton("Cancel");
    	cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();
			}
		});
    	cancelBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
    	cancelBtn.setPrefHeight(32);
    	cancelBtn.setStyle(dialogBtnStyle);
    	
    	JFXButton selectPathBtn = new JFXButton("select .rpx file");
    	selectPathBtn.setPrefWidth(110);
    	selectPathBtn.setStyle(dialogBtnStyle);
    	selectPathBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	FileChooser romDirectoryChooser = new FileChooser();
                File romDirectory =  romDirectoryChooser.showOpenDialog(stage);
                romPathTF.setText(romDirectory.getAbsolutePath());
            }
    	});
    	
    	JFXButton selectCoverBtn = new JFXButton("select cover file");
    	selectCoverBtn.setPrefWidth(110);
    	selectCoverBtn.setStyle(dialogBtnStyle);
    	selectCoverBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	FileChooser coverDirectoryChooser = new FileChooser();
                File coverDirectory =  coverDirectoryChooser.showOpenDialog(stage);
                gameCoverTF.setText(coverDirectory.getAbsolutePath());
            }
    	});
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(15, 10, 10, 10));
    	grid.add(new Label("game title:"), 0, 0);
    	grid.add(gameTitleTF, 1, 0);
    	grid.add(new Label("title id:"), 0, 1);
    	grid.add(gameTitleIDTF, 1, 1);
    	grid.add(new Label("ROM path:"), 0, 2);
    	grid.add(romPathTF, 1, 2);
    	grid.add(selectPathBtn, 2, 2);
    	grid.add(new Label("cover path:"), 0, 3);
    	grid.add(gameCoverTF, 1, 3);
    	grid.add(selectCoverBtn, 2, 3);
    	
    	Text bdyText = new Text(bodyText);
    	
    	VBox vbox = new VBox();
    	vbox.getChildren().addAll(bdyText, grid);
    	
		content.setHeading(new Text(headingText));
    	content.setBody(vbox);
    	content.setActions(cancelBtn, placeholder, okayBtn);
    	content.setPrefSize(dialogWidth, dialogHeight);
    	pane.getChildren().add(stackPane);
    	AnchorPane.setTopAnchor(stackPane, (pane.getHeight()-content.getPrefHeight())/2);
    	AnchorPane.setLeftAnchor(stackPane, (pane.getWidth()-content.getPrefWidth())/2);
    	dialog.show();
	}
	
	public EditDataType getGameData() {
		return gameData;
	}
}
