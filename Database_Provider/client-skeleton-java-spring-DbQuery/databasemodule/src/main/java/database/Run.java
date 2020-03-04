package database;

import database.tools.PropertiesReader;

public class Run {

  public static void main(String[] args){
    System.out.println("Testing the influxDB");
    IDatabase db = DatabaseSelector.getDatabaseInstance("-influx");
    if(db != null){
      System.out.println("Database module");
      DataGenerator dg = new DataGenerator();
      //db.connect();
      dg.start(db);
      System.out.println("The test is done!");
      //db.close();
    }
  }
}
