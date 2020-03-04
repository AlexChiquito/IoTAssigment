package eu.arrowhead.client.skeleton.consumer;

import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
/*
import database.DatabaseAuthorisation;
import database.DatabaseSelector;
import database.IDatabase;
import database.tools.PropertiesReader;
*/

import eu.arrowhead.client.skeleton.consumer.DatabaseAuthorisation;
import eu.arrowhead.client.skeleton.consumer.DatabaseSelector;
import eu.arrowhead.client.skeleton.consumer.IDatabase;
import eu.arrowhead.client.skeleton.consumer.tools.PropertiesReader;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, DBProviderConstants.BASE_PACKAGE}) //TODO: add custom packages if any
public class ConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
	private final Logger logger = LogManager.getLogger( ConsumerMain.class );
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(ConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
		//SIMPLE EXAMPLE OF INITIATING AN ORCHESTRATION
		logger.info("Starting...");
    	logger.info("Orchestration request for " + DBProviderConstants.GET_SENSOR_SERVICE_DEFINITION + " service:");
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
    	
    	final ServiceQueryFormDTO requestedService = new ServiceQueryFormDTO();
    	requestedService.setServiceDefinitionRequirement(DBProviderConstants.GET_SENSOR_SERVICE_DEFINITION);
    	
    	orchestrationFormBuilder.requestedService(requestedService)
    							.flag(Flag.MATCHMAKING, false) //When this flag is false or not specified, then the orchestration response cloud contain more proper provider. Otherwise only one will be chosen if there is any proper.
    							.flag(Flag.OVERRIDE_STORE, true) //When this flag is false or not specified, then a Store Orchestration will be proceeded. Otherwise a Dynamic Orchestration will be proceeded.
    							.flag(Flag.TRIGGER_INTER_CLOUD, false); //When this flag is false or not specified, then orchestration will not look for providers in the neighbor clouds, when there is no proper provider in the local cloud. Otherwise it will. 
    	
    	final OrchestrationFormRequestDTO orchestrationRequest = orchestrationFormBuilder.build();
    	printOut(orchestrationRequest);
    	OrchestrationResponseDTO response = null;
    	try {
			response = arrowheadService.proceedOrchestration(orchestrationRequest);		
			System.out.println("Si hubo respuesta...");	
		} catch (final ArrowheadException ex) {
			//Handle the unsuccessful request as you wish!
			System.out.println("No se pudo :c");
		}
		
		printOut(response);

    	//EXAMPLE OF CONSUMING THE SERVICE FROM A CHOSEN PROVIDER
    	
    	if (response == null || response.getResponse().isEmpty()) {
    		//If no proper providers found during the orchestration process, then the response list will be empty. Handle the case as you wish!
    		logger.info("Orchestration response is empty");
    		return;
		}else{
			System.out.println("Y la respuesta no esta vacia...");
		}
    	
    	final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
    	
    	final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
    	final String address = result.getProvider().getAddress();
    	final int port = result.getProvider().getPort();
    	final String serviceUri = result.getServiceUri();
    	final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
    	String token = null;
    	if (result.getAuthorizationTokens() != null) {
    		token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
		}
    	final Object payload = null; //Can be null if not specified in the description of the service.
		
		sensorData rSensorData = new sensorData();

		//Run.test();

		IDatabase db = DatabaseSelector.getDatabaseInstance("-influx");

		if(db != null){
			System.out.println("Database module");
			db.connect();
			//dg.start(db);
			//System.out.println("The test is done!");
			//db.close();
		}
		System.out.println("About to ask the service...");
		for (int i = 0; i<1000; i++){
			rSensorData = arrowheadService.consumeServiceHTTP(sensorData.class, httpMethod, address, port, serviceUri, interfaceName, token, null, new String[0]);
			printOut(rSensorData);
			db.insertData(JsonNize(rSensorData));
		}
		try{
			db.close();
		}catch(Exception ex){
			System.out.println("Connection already closed");
		}
		/*
		while (true){
			rSensorData = arrowheadService.consumeServiceHTTP(sensorData.class, httpMethod, address, port, serviceUri, interfaceName, token, null, new String[0]);
			printOut(rSensorData);

		}*/
		
	}

	private JsonObject JsonNize(final sensorData object){
		JsonObject json = new JsonObject();
        json.addProperty("sensorType",object.getSensorType());
        json.addProperty("timeStamp", object.getTimeStamp());
		json.addProperty("data",object.getData());
		return json;
	}

	private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
	}
	
	public void SaveDB(String DBSelector, JSONObject Data){

	}
}
