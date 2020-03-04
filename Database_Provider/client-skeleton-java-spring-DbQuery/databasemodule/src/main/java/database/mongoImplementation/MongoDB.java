package database.mongoImplementation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DatabaseAuthorisation;
import database.IDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 *
 */
public class MongoDB extends IDatabase {

  private MongoClient mongoClient = null;
  private MongoDatabase database = null;
  private MongoCollection<Document> insertCollection = null;
  private ArrayList<Document> documents = null;

  private int BATCH_SIZE = 10;

  // ####################################################################################
  // ### Constructor
  // ####################################################################################
  /**
   *
   * @param auth
   */
  public MongoDB(DatabaseAuthorisation auth){
    dbAuth = auth;
    connect();
  }
  // ####################################################################################


  // ####################################################################################
  // ### Implemented IDatabase methods
  // ####################################################################################
  /**
   * @return
   */
  @Override
  public boolean connect() {
    try{
      System.out.println("Connecting to " + dbAuth.getDbPath() + "...");
      mongoClient = MongoClients.create(dbAuth.getDbPath());
      database = mongoClient.getDatabase(dbAuth.getDbName());
      System.out.println("Connected");
      return true;
    }catch(Exception ex){
      System.out.println("Could not connect to database: " + dbAuth.getDbPath());
      return false;
    }
  }

  /**
   * @param json
   * @return
   */
  @Override
  public int insertData(JsonObject json) {
    if(documents == null){
      documents = new ArrayList<Document>();
      insertCollection = database.getCollection(json.get("sensortype").getAsString());
      System.out.println("New insert batch!");
    }
    Document doc = Document.parse(new Gson().toJson(json));
    documents.add(doc);
    if(documents.size() >= BATCH_SIZE){
      System.out.println("Writing to database...");
      insertCollection.insertMany(documents);
      documents = null;
      insertCollection = null;
      System.out.println("Writing to database was a Success");
    }
    return 0;
  }

  /**
   * @param requestInfo
   * @return
   */
  @Override
  public JsonArray requestData(JsonObject requestInfo) {
    connect();
    System.out.println("Query...");
    JsonArray result = new JsonArray();
    Consumer<Document> resultConsumer = document -> {
      result.add(new Gson().fromJson(document.toJson(),JsonObject.class));
    };
    MongoCollection<Document> queryCollection = database.getCollection(requestInfo.get("columnName").getAsString());
    queryCollection.find().forEach(resultConsumer);
    close();
    System.out.println("Query was a success!");
    return result;
  }

  /**
   * @return
   */
  @Override
  public boolean close() {
    System.out.println("Closing database connection...");
    try{
      mongoClient.close();
      System.out.println("Database connection is now closed");
      return true;
    }
    catch (Exception ex){
      System.out.println("Error when closing the database connection!");
      System.out.println(ex);
      return false;
    }
  }
  // ####################################################################################


  // ####################################################################################
  // ### Private help methods for this specific database
  // ####################################################################################

  // ####################################################################################
}
