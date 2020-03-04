package database;


import database.influxImplementation.InfluxDatabase;
import database.mongoImplementation.MongoDB;
import database.tools.PropertiesReader;

/**
 *
 */
public class DatabaseSelector {

  /**
   *
   * @param database
   * @return
   */
  public static IDatabase getDatabaseInstance(String database){
    IDatabase db;
    DatabaseAuthorisation auth = null;
    PropertiesReader propReader = new PropertiesReader();
    switch(database){
      case("-influx"):{
        auth = propReader.getDatabaseAuthorisationProperties("InfluxAuthorisation.properties");
        db = new InfluxDatabase(auth);
        System.out.println("Influx database instance was just created");
        break;
      }
      case ("-mongo"): {
        auth = propReader.getDatabaseAuthorisationProperties("MongoAuthorisation.properties");
        db = new MongoDB(auth);
        System.out.println("Mongo database instance was just created");
        break;
      }
      default:{
        System.out.println("No such database instance implemented!");
        db = null;
        break;
      }
    }
    return db;
  }
}
