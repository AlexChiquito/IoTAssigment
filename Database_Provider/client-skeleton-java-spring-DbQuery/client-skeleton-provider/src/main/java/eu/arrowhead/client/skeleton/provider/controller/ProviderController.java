package eu.arrowhead.client.skeleton.provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import database.influxImplementation.InfluxDatabase;

import com.google.gson.JsonArray;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.arrowhead.client.skeleton.provider.IDatabase;
import eu.arrowhead.client.skeleton.provider.DatabaseSelector;
import eu.arrowhead.Database.DatabaseResponse;
import eu.arrowhead.Database.DatabaseRequest;

import javax.ws.rs.core.Response;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.client.skeleton.provider.DBProviderConstants;

@RestController
public class ProviderController {
	
	//=================================================================================================
	// members

	//TODO: add your variables here

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}

	//@GetMapping(path = "/GetLastPoints/{nPoints}")
	@GetMapping(path = "/GetLastPoints", produces = "application/json")
	//@ResponseBody public JsonArray GetLastPoints(@PathVariable(name = "nPoints") final String n){
	@ResponseBody public String GetLastPoints(@RequestParam(name = "nPoints", required = false) final String n){
		//return "Ill try";
		////
		if (n == null){
			System.out.println("No param given");
		}else{
			System.out.println("Printing: " + n + " points");
		}
		DatabaseRequest request = new DatabaseRequest(n,"EngineRPM","-influx");
		Gson gson = new Gson();
		String jsonText = gson.toJson(request);
		JsonObject queryJson = gson.fromJson(jsonText, JsonObject.class);
		IDatabase db = DatabaseSelector.getDatabaseInstance(queryJson.get("databaseType").getAsString());
		queryJson.addProperty("columnName", "EngineRPM");
		System.out.println("Query: " + queryJson.toString());
		JsonArray result = db.requestData(queryJson);
		System.out.println("Hello");
		System.out.println(Utilities.toPrettyJson(gson.toJson(result))); 
		return Utilities.toPrettyJson(gson.toJson(result));
		//return Response.status(200).entity(new DatabaseResponse(System.currentTimeMillis(),Utilities.toPrettyJson(gson.toJson(result)),0)).build();

    }
	////


	@GetMapping(path = "/")
	public String home(){
		return "home page";
	}

	@GetMapping(path = DBProviderConstants.GETDATA_URI)
	public JsonArray getDataFromDB(){
		DatabaseRequest databaseRequest = new DatabaseRequest();
		return null;
	}


	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST end points
}
