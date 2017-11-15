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

package com.cemu_UI.datatypes;

public class EditDataType {

	private String romPath;
	private String coverPath;
	private String title;
	private String titleID;
	
	 /**
	  * Data type used for the add/edit game dialog
	  */
	public EditDataType(String romPath, String coverPath, String title, String titleID) {
		this.romPath = romPath;
		this.coverPath = coverPath;
		this.title = title;
		this.titleID = titleID;
	}

	public String getRomPath() {
		return romPath;
	}
	
	public void setRomPath(String romPath) {
		this.romPath = romPath;
	}

	public String getCoverPath() {
		return coverPath;
	}
	
	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleID() {
		return titleID;
	}
	
	public void setTitleID(String titleID) {
		this.titleID = titleID;
	}
	
}
