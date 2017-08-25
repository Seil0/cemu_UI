/**
 * Datatype used in the TreeTableview for
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

     public CourseTableDataType(String title, String id, int time, int stars) {
         this.title = new SimpleStringProperty(title);
         this.id = new SimpleStringProperty(id);
         this.time = new SimpleIntegerProperty(time);
         this.stars = new SimpleIntegerProperty(stars);
     }
}
