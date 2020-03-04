package eu.arrowhead.client.skeleton.consumer;

public class DBProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "eu.arrowhead";
	
	public static final String GET_SENSOR_SERVICE_DEFINITION = "get-car";
	public static final String INTERFACE_SECURE = "HTTPS-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String SENSOR_URI = "/car";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private DBProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
