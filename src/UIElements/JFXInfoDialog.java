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
	private int dialogHeigh;
	private Pane pane;

	public JFXInfoDialog(String headingText, String bodyText, String dialogBtnStyle, int dialogWidth, int dialogHeigh, Pane pane) {
		this.headingText = headingText;
		this.bodyText = bodyText;
		this.dialogBtnStyle = dialogBtnStyle;
		this.dialogWidth = dialogWidth;
		this.dialogHeigh = dialogHeigh;
		this.pane = pane;
	}

	public void show() {
		JFXDialogLayout content = new JFXDialogLayout();
		content.setHeading(new Text(headingText));
		content.setBody(new Text(bodyText));
		content.setPrefSize(dialogWidth, dialogHeigh);
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
