/**
 * Datatype used for the smmdbapi query
 */
package datatypes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SmmdbApiDataType {

	private final IntegerProperty id = new SimpleIntegerProperty();
	private final IntegerProperty owner = new SimpleIntegerProperty();
	private final IntegerProperty coursetype = new SimpleIntegerProperty();
	private final IntegerProperty leveltype = new SimpleIntegerProperty();
	private final IntegerProperty difficulty = new SimpleIntegerProperty();
	private final IntegerProperty lastmodified = new SimpleIntegerProperty();
	private final IntegerProperty uploaded = new SimpleIntegerProperty();
	private final IntegerProperty downloads = new SimpleIntegerProperty();
	private final IntegerProperty stars = new SimpleIntegerProperty();
	private final IntegerProperty ispackage = new SimpleIntegerProperty();
	private final IntegerProperty updatereq = new SimpleIntegerProperty();
	private final StringProperty nintendoid = new SimpleStringProperty();
	private final StringProperty title = new SimpleStringProperty();
	
	public SmmdbApiDataType(final int id, final int owner, final int coursetype, final int leveltype, final int difficulty, 
							final int lastmodified, final int uploaded, final int downloads, final int stars, final int ispackage,
							final int updatereq, final String nintendoid, final String title) {
		this.id.set(id);
		this.owner.set(owner);
		this.coursetype.set(coursetype);
		this.leveltype.set(leveltype);
		this.difficulty.set(difficulty);
		this.lastmodified.set(lastmodified);
		this.uploaded.set(uploaded);
		this.downloads.set(downloads);
		this.stars.set(stars);
		this.ispackage.set(ispackage);
		this.updatereq.set(updatereq);
		this.nintendoid.set(nintendoid);
		this.title.set(title);
	}
	
	public IntegerProperty idProperty(){
		return id;
	}
	
	public IntegerProperty ownerProperty(){
		return owner;
	}
	
	public IntegerProperty coursetypeProperty(){
		return coursetype;
	}
	
	public StringProperty nintendoidProperty(){
		return nintendoid;
	}
	
	public IntegerProperty leveltypeProperty(){
		return leveltype;
	}
	
	public IntegerProperty difficultyProperty(){
		return difficulty;
	}
	
	public IntegerProperty lastmodifiedProperty(){
		return lastmodified;
	}
	
	public IntegerProperty uploadedProperty(){
		return uploaded;
	}
	
	public IntegerProperty downloadsProperty(){
		return downloads;
	}
	
	public IntegerProperty starsProperty(){
		return stars;
	}
	
	public IntegerProperty ispackageProperty(){
		return ispackage;
	}
	
	public IntegerProperty updatereqProperty(){
		return updatereq;
	}
	
	public StringProperty titleProperty(){
		return title;
	}

	public int getId() {
		return idProperty().get();
	}

	public int getOwner() {
		return ownerProperty().get();
	}

	public int getCoursetype() {
		return coursetypeProperty().get();
	}

	public int getLeveltype() {
		return leveltypeProperty().get();
	}

	public int getDifficulty() {
		return difficultyProperty().get();
	}

	public int getLastmodified() {
		return lastmodifiedProperty().get();
	}

	public int getUploaded() {
		return uploadedProperty().get();
	}

	public int getDownloads() {
		return downloadsProperty().get();
	}

	public int getStars() {
		return starsProperty().get();
	}
	
	public int getIspackage() {
		return ispackageProperty().get();
	}

	public int getUpdatereq() {
		return updatereqProperty().get();
	}
	
	public String getNintendoid() {
		return nintendoidProperty().get();
	}

	public String getTitle() {
		return titleProperty().get();
	}

	public final void setId(int id) {
		idProperty().set(id);
	}

	public final void setOwner(int owner) {
		ownerProperty().set(owner);
	}

	public final void setCoursetype(int coursetype) {
		coursetypeProperty().set(coursetype);
	}

	public final void setLeveltype(int leveltype) {
		leveltypeProperty().set(leveltype);
	}

	public final void setDifficulty(int difficulty) {
		difficultyProperty().set(difficulty);
	}

	public final void setLastmodified(int lastmodified) {
		lastmodifiedProperty().set(lastmodified);
	}

	public final void setUploaded(int uploaded) {
		uploadedProperty().set(uploaded);
	}

	public final void setDownloads(int downloads) {
		downloadsProperty().set(downloads);
	}

	public final void setStars(int stars) {
		starsProperty().set(stars);
	}
	
	public final void setIspackage(int ispackage) {
		ispackageProperty().set(ispackage);
	}

	public final void setUpdatereq(int updatereq) {
		updatereqProperty().set(updatereq);
	}

	public final void setNintendoid(String nintendoid) {
		nintendoidProperty().set(nintendoid);
	}

	public final void setTitle(String title) {
		titleProperty().set(title);
	}

}
