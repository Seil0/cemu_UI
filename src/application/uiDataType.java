package application;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class uiDataType {
	
	private final SimpleObjectProperty<VBox> vBox = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Label> label = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<JFXButton> button = new SimpleObjectProperty<>();
	private final StringProperty titleID = new SimpleStringProperty();
	private final StringProperty romPath = new SimpleStringProperty();
	
	public uiDataType (final VBox vBox, final Label label, final JFXButton button, final String titleID, final String romPath){
		this.vBox.set(vBox);
		this.label.set(label);
		this.button.set(button);
		this.titleID.set(titleID);
	}
	
	public SimpleObjectProperty<VBox> vBoxProperty(){
		return vBox;
	}
	
	public SimpleObjectProperty<Label> labelProperty(){
		return label;
	}
	
	public SimpleObjectProperty<JFXButton> buttonProperty(){
		return button;
	}
	
	public StringProperty titleIDProperty(){
		return titleID;
	}
	
	public StringProperty romPathProperty(){
		return romPath;
	}
	
	
	public final VBox getVBox() {
		return vBoxProperty().get();
	}
	
	public final Label getLabel() {
		return labelProperty().get();
	}
	
	public final JFXButton getButton() {
		return buttonProperty().get();
	}
	
	public final String getTitleID(){
		return titleIDProperty().get();
	}
	
	public final String getRomPath(){
		return romPathProperty().get();
	}
	
	
	public final void setVBox(VBox vBox) {
		vBoxProperty().set(vBox);
	}
	
	public final void setLabel(Label label) {
		labelProperty().set(label);
	}
	
	public final void setButton(JFXButton button) {
		buttonProperty().set(button);
	}
	
	public final void setTitleID(String titleID){
		titleIDProperty().set(titleID);
	}
	
	public final void setRomPath(String romPath){
		romPathProperty().set(romPath);
	}
}
