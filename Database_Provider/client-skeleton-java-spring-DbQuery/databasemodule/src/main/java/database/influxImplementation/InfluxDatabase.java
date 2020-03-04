package database.influxImplementation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.DatabaseAuthorisation;
import database.IDatabase;
import database.tools.JsonTools;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class InfluxDatabase extends IDatabase {

  private InfluxDB influxDB = null;
  private BatchPoints batchPoints = null;
  private int dataPointCounter = 0;
  private final int BATCH_SIZE = 1;

  // ####################################################################################
  // ### Constructor
  // ####################################################################################
  /**
   *
   * @param auth
   */
  public InfluxDatabase(DatabaseAuthorisation auth){
    dbAuth = auth;
  }
  // ####################################################################################


  // ####################################################################################
  // ### Implemented IDatabase methods
  // ####################################################################################

  /**
   *
   * @return
   */
  @Override
  public boolean connect() {
    try{
      System.out.println("Connecting to " + dbAuth.getDbPath() + "...");
      influxDB = InfluxDBFactory.connect(dbAuth.getDbPath(), dbAuth.getUserName(), dbAuth.getPwd());
      System.out.println("Connected");
      influxDB.setDatabase(dbAuth.getDbName());
      return true;
    }catch(Exception ex){
      System.out.println("Could not connect to database: " + dbAuth.getDbPath());
      return false;
    }
  }

  /**
   *
   * @return
   */
  @Override
  public boolean close() {
    System.out.println("Closing database connection...");
    try{
      influxDB.close();
      influxDB = null;
      System.out.println("Database connection is now closed");
      return true;
    }
    catch (Exception ex){
        System.out.println("Error when closing the database connection!");
        return false;
    }

  }

  /**
   *
   * @param json
   * @return
   */
  @Override
  public int insertData(JsonObject json) {
    if(batchPoints == null){
      batchPoints = createNewBatchPoints();
    }
    createNewMeasurementPoint(json);
    if(dataPointCounter >= BATCH_SIZE){
      writeToDB();
    }
    return 0;
  }

  /**
   *
   * @param request
   * @return
   */
  @Override
  public JsonArray requestData(JsonObject request) {
    try{
      System.out.println("Creating query to the database...");
      Query query = new Query("SELECT * FROM "+request.get("columnName"), dbAuth.getDbName());
      connect();
      QueryResult queryResult = influxDB.query(query);
      close();
      List<QueryResult.Result>  results = queryResult.getResults();
      List<String> columns = results.get(0).getSeries().get(0).getColumns();
      List<List<Object>> values = results.get(0).getSeries().get(0).getValues();
      JsonArray result = new JsonArray();
      for(int i = 0; i < values.size(); i++){
        List<Object> value = values.get(i);
        JsonObject json = new JsonObject();
        for (int j = 0; j < value.size(); j++){
          Object pointInfo = value.get(j);
          if (columns.get(j).equals("data")){
            json.addProperty(columns.get(j), Double.parseDouble(pointInfo.toString()));
          }else{
            json.addProperty(columns.get(j), JsonTools.removeQuotes(pointInfo.toString()));
          }
        }
        result.add(json);
      }
      System.out.println("Query done!");
      return result;
    }catch (Exception ex){
      System.out.println("Could not query the request");
      ex.printStackTrace();
      return null;
    }

  }
  // ####################################################################################


  // ####################################################################################
  // ### Private help methods for influx database
  // ####################################################################################
  private boolean writeToDB(){
    try{
      if (batchPoints != null || batchPoints.getPoints() != null) {
        connect();
        influxDB.write(batchPoints);
        close();
        System.out.println("Successful insert!");
        batchPoints = null;
        dataPointCounter = 0;
        return true;
      }
      else {
        System.out.println("No points to insert! Insert aborted");
        return false;
      }
    }catch (Exception ex){
      System.out.println("Could not insert data!");
      System.out.println(ex);
      return false;
    }
  }

  /**
   *
   * @return
   */
  private BatchPoints createNewBatchPoints(){
    return BatchPoints.database(dbAuth.getDbName()).build();
  }

  /**
   *
   * @param json
   * @return
   */
  private int createNewMeasurementPoint(JsonObject json){
    try{
      Point.Builder builder = Point.measurement(json.get("sensortype").getAsString());//.tag("engineModel", info.get("engineModel").getAsString())
      builder.time(json.get("timestamp").getAsLong(), TimeUnit.MILLISECONDS);
      builder.addField("data",json.get("data").getAsLong());
      builder.tag("id",json.get("id").getAsString());
      batchPoints.point(builder.build());
      dataPointCounter++;
      return 0;
    }
    catch (Exception ex){
      System.out.println("Could not create Measurement Point!");
      System.out.println(ex);
      return -1;
    }
  }
  // ####################################################################################
}
