package eu.arrowhead.client.skeleton.provider;

//import database.tools.PropertiesReader;
import eu.arrowhead.client.skeleton.provider.tools.PropertiesReader;

public class Run {

  public static void test(){
    System.out.println("Testing the influxDB");
    IDatabase db = DatabaseSelector.getDatabaseInstance("-influx");
    if(db != null){
      System.out.println("Database module");
      DataGenerator dg = new DataGenerator();
      db.connect();
      dg.start(db);
      System.out.println("The test is done!");
      db.close();
    }
  }
}
