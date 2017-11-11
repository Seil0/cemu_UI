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

package UIElements;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class JFXInfoDialog {

	private String headingText;
	private String bodyText;
	private String dialogBtnStyle;
	private int dialogWidth;
	private int dialogHeight;
	private Pane pane;

	/**
	 * Creates a new JFoenix Dialog to show some information
	 * @param headingText Heading Text, just the heading
	 * @param bodyText body Text, all other text belongs here
	 * @param dialogBtnStyle Style of the okay button
	 * @param dialogWidth dialog width
	 * @param dialogHeight dialog height
	 * @param pane pane to which the dialog belongs
	 */
	public JFXInfoDialog(String headingText, String bodyText, String dialogBtnStyle, int dialogWidth, int dialogHeight, Pane pane) {
		this.headingText = headingText;
		this.bodyText = bodyText;
		this.dialogBtnStyle = dialogBtnStyle;
		this.dialogWidth = dialogWidth;
		this.dialogHeight = dialogHeight;
		this.pane = pane;
	}

	public void show() {
		JFXDialogLayout content = new JFXDialogLayout();
		content.setHeading(new Text(headingText));
		content.setBody(new Text(bodyText));
		content.setPrefSize(dialogWidth, dialogHeight);
		StackPane stackPane = new StackPane();
		stackPane.autosize();
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.LEFT, true);
		JFXButton button = new JFXButton("Okay");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();
			}
		});
		button.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		button.setPrefHeight(32);
		button.setStyle(dialogBtnStyle);
		content.setActions(button);
		pane.getChildren().add(stackPane);
		AnchorPane.setTopAnchor(stackPane, (pane.getHeight() - content.getPrefHeight()) / 2);
		AnchorPane.setLeftAnchor(stackPane, (pane.getWidth() - content.getPrefWidth()) / 2);
		dialog.show();
	}
}
