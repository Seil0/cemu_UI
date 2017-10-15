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

package datatypes;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CourseTableDataType extends RecursiveTreeObject<CourseTableDataType> {

	 public final StringProperty title;
	 public final StringProperty id;
	 public final IntegerProperty time;
	 public final IntegerProperty stars;
	 
	 /**
	  * Data type used in the TreeTableview for
	  */
     public CourseTableDataType(String title, String id, int time, int stars) {
         this.title = new SimpleStringProperty(title);
         this.id = new SimpleStringProperty(id);
         this.time = new SimpleIntegerProperty(time);
         this.stars = new SimpleIntegerProperty(stars);
     }
}
