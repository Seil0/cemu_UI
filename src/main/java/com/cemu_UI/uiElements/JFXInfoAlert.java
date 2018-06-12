/**
 * Kellerkinder Framework Alerts
 * 
 * Copyright 2018  <@Seil0>
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

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JFXInfoAlert {
	
	private String headingText;
	private String bodyText;
	private String btnStyle;
	private Stage stage;

	/**
	 * Creates a new JFoenix Alert to show some information
	 * @param headerText	Heading text of the alert
	 * @param bodyText	Content text of the alert
	 * @param btnStyle		Style of the okay button
	 * @param stage			stage to which the dialog belongs
	 */
	public JFXInfoAlert(String headingText, String bodyText, String btnStyle, Stage stage) {
		setHeadingText(headingText);
		setBodyText(bodyText);
		setBtnStyle(btnStyle);
		setStage(stage);
	}
	
	public JFXInfoAlert() {
		// Auto-generated constructor stub
	}
	
	public void showAndWait( ) {
		JFXAlert<Void> alert = new JFXAlert<>(stage);
		
		JFXButton button = new JFXButton("Okay");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alert.close();
			}
		});
		button.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		button.setPrefHeight(32);
		button.setStyle(btnStyle);
		
		JFXDialogLayout content = new JFXDialogLayout();
		content.setActions(button);
		content.setHeading(new Text(headingText));
		content.setBody(new Text(bodyText));
		alert.setContent(content);
		alert.showAndWait();
	}

	public String getHeadingText() {
		return headingText;
	}

	public void setHeadingText(String headingText) {
		this.headingText = headingText;
	}

	public String getBodyText() {
		return bodyText;
	}

	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	public String getBtnStyle() {
		return btnStyle;
	}

	public void setBtnStyle(String btnStyle) {
		this.btnStyle = btnStyle;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
}