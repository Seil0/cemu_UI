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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class JFXOkayCancelDialog {
	
	private String headingText;
	private String bodyText;
	private String dialogBtnStyle;
	private String okayText = "okay";
	private String cancelText = "cancel";
	private int dialogWidth;
	private int dialogHeight;
	private EventHandler<ActionEvent> okayAction;
	private EventHandler<ActionEvent> cancelAction;
	private Pane pane;
	
	/**
	 * Creates a new JFoenix Dialog to show some information with okay and cancel option
	 * @param headingText Heading Text, just the heading
	 * @param bodyText body Text, all other text belongs here
	 * @param dialogBtnStyle Style of the okay button
	 * @param dialogWidth dialog width
	 * @param dialogHeight dialog height
	 * @param okayAction action which is performed if the okay button is clicked
	 * @param cancelAction action which is performed if the cancel button is clicked
	 * @param pane pane to which the dialog belongs
	 */
	public JFXOkayCancelDialog(String headingText, String bodyText, String dialogBtnStyle, int dialogWidth,
			int dialogHeight, EventHandler<ActionEvent> okayAction, EventHandler<ActionEvent> cancelAction, Pane pane) {
		this.headingText = headingText;
		this.bodyText = bodyText;
		this.dialogBtnStyle = dialogBtnStyle;
		this.dialogWidth = dialogWidth;
		this.dialogHeight = dialogHeight;
		this.okayAction = okayAction;
		this.cancelAction = cancelAction;
		this.pane = pane;
	}
	
	public void show() {
		JFXDialogLayout content = new JFXDialogLayout();
    	content.setHeading(new Text(headingText));
    	content.setBody(new Text(bodyText));
    	StackPane stackPane = new StackPane();
    	stackPane.autosize();
    	JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.LEFT, true);
    	JFXButton okayBtn = new JFXButton(okayText);
    	okayBtn.addEventHandler(ActionEvent.ACTION, (e)-> {
    		dialog.close();
    	});
    	okayBtn.addEventHandler(ActionEvent.ACTION, okayAction);
    	okayBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
    	okayBtn.setPrefHeight(32);
    	okayBtn.setStyle(dialogBtnStyle);
    	JFXButton cancelBtn = new JFXButton(cancelText);
    	cancelBtn.addEventHandler(ActionEvent.ACTION, (e)-> {
    		dialog.close();
    	});
    	cancelBtn.addEventHandler(ActionEvent.ACTION, cancelAction);
    	cancelBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
    	cancelBtn.setPrefHeight(32);
    	cancelBtn.setStyle(dialogBtnStyle);
    	content.setActions(cancelBtn, okayBtn);
    	content.setPrefSize(dialogWidth, dialogHeight);
    	pane.getChildren().add(stackPane);
    	AnchorPane.setTopAnchor(stackPane, (pane.getHeight()-content.getPrefHeight())/2);
    	AnchorPane.setLeftAnchor(stackPane, (pane.getWidth()-content.getPrefWidth())/2);
    	dialog.show();
	}

	public String getOkayText() {
		return okayText;
	}

	public void setOkayText(String okayText) {
		this.okayText = okayText;
	}

	public String getCancelText() {
		return cancelText;
	}

	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}

	public EventHandler<ActionEvent> getOkayAction() {
		return okayAction;
	}

	public void setOkayAction(EventHandler<ActionEvent> okayAction) {
		this.okayAction = okayAction;
	}

	public EventHandler<ActionEvent> getCancelAction() {
		return cancelAction;
	}

	public void setCancelAction(EventHandler<ActionEvent> cancelAction) {
		this.cancelAction = cancelAction;
	}

}

