package eu.arrowhead.Database;

//import org.glassfish.jersey.internal.guava.MoreObjects;
import com.google.common.base.MoreObjects;

public class DatabaseRequest {

  String timeFrom;
  String timeTo;
  String id;
  String databaseType;
  String nPoints;

  public DatabaseRequest(){
  }

  public DatabaseRequest(String timeFrom, String timeTo, String id, String databaseType){
    this.timeFrom = timeFrom;
    this.timeTo = timeTo;
    this.id = id;
    this.databaseType = databaseType;
  }

  public DatabaseRequest(String nPoints, String id, String databaseType){
    this.nPoints = nPoints;
    this.id = id;
    this.databaseType = databaseType;
  }

  public String getTimeFrom() {
    return timeFrom;
  }

  public String getTimeTo() {
    return timeTo;
  }

  public String getId() {
    return id;
  }

  public String getDatabaseType() {
    return databaseType;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("timeFrom", timeFrom)
            .add("timeTo", timeTo)
            .add("id", id)
            .add("databaseType", databaseType).toString();
  }
}
