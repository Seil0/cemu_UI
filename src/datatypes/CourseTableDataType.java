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
	 public final IntegerProperty downloads;
	 public final IntegerProperty stars;
	 public final IntegerProperty id;

     public CourseTableDataType(String title, int downloads, int stars, int id) {
         this.title = new SimpleStringProperty(title);
         this.downloads = new SimpleIntegerProperty(downloads);
         this.stars = new SimpleIntegerProperty(stars);
         this.id = new SimpleIntegerProperty(id);
     }
}
