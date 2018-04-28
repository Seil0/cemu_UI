/**
 * cemu_UI
 * 
 * Copyright 2017-2018  <@Seil0>
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
package com.cemu_UI.datatypes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SmmdbApiDataType {

	private final IntegerProperty courseTheme = new SimpleIntegerProperty();
	private final IntegerProperty gameStyle = new SimpleIntegerProperty();
	private final IntegerProperty difficulty = new SimpleIntegerProperty();
	private final IntegerProperty lastmodified = new SimpleIntegerProperty();
	private final IntegerProperty uploaded = new SimpleIntegerProperty();
	private final IntegerProperty autoScroll = new SimpleIntegerProperty();
	private final IntegerProperty stars = new SimpleIntegerProperty();
	private final IntegerProperty time = new SimpleIntegerProperty();
	private final StringProperty owner = new SimpleStringProperty();
	private final StringProperty id = new SimpleStringProperty();
	private final StringProperty nintendoid = new SimpleStringProperty();
	private final StringProperty title = new SimpleStringProperty();
	
	/**
	 * Data type used for the smmdbapi query
	 */
	public SmmdbApiDataType(final int courseTheme, final int gameStyle, final int difficulty, final int lastmodified,
							final int uploaded, final int autoScroll, final int stars, final int time,
							final String owner, final String id, final String nintendoid, final String title) {
		this.id.set(id);
		this.owner.set(owner);
		this.courseTheme.set(courseTheme);
		this.gameStyle.set(gameStyle);
		this.difficulty.set(difficulty);
		this.lastmodified.set(lastmodified);
		this.uploaded.set(uploaded);
		this.autoScroll.set(autoScroll);
		this.stars.set(stars);
		this.time.set(time);
		this.nintendoid.set(nintendoid);
		this.title.set(title);
	}
	
	public IntegerProperty courseThemeProperty(){
		return courseTheme;
	}
	
	
	public IntegerProperty gameStyleProperty(){
		return gameStyle;
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
	
	public IntegerProperty autoScrollProperty(){
		return autoScroll;
	}
	
	public IntegerProperty starsProperty(){
		return stars;
	}
	
	public IntegerProperty timeProperty(){
		return time;
	}
	
	public StringProperty ownerProperty(){
		return owner;
	}
	
	public StringProperty idProperty(){
		return id;
	}
	
	public StringProperty nintendoidProperty(){
		return nintendoid;
	}
	
	public StringProperty titleProperty(){
		return title;
	}

	public int getCourseTheme() {
		return courseThemeProperty().get();
	}

	public int getGameStyle() {
		return gameStyleProperty().get();
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

	public int getAutoScroll() {
		return autoScrollProperty().get();
	}

	public int getStars() {
		return starsProperty().get();
	}
	
	public int getTime() {
		return timeProperty().get();
	}
	
	public String getOwner() {
		return ownerProperty().get();
	}

	public String getId() {
		return idProperty().get();
	}
	
	public String getNintendoid() {
		return nintendoidProperty().get();
	}

	public String getTitle() {
		return titleProperty().get();
	}

	public final void setCourseTheme(int courseTheme) {
		courseThemeProperty().set(courseTheme);
	}

	public final void setGameStyle(int gameStyle) {
		gameStyleProperty().set(gameStyle);
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

	public final void setAutoScroll(int autoScroll) {
		autoScrollProperty().set(autoScroll);
	}

	public final void setStars(int stars) {
		starsProperty().set(stars);
	}

	public final void setTime(int time) {
		timeProperty().set(time);
	}
	
	public final void setOwner(String owner) {
		ownerProperty().set(owner);
	}

	public final void setId(String id) {
		idProperty().set(id);
	}
	
	public final void setNintendoid(String nintendoid) {
		nintendoidProperty().set(nintendoid);
	}

	public final void setTitle(String title) {
		titleProperty().set(title);
	}
	
}
