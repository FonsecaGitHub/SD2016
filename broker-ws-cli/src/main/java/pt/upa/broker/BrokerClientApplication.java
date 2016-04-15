package pt.upa.broker;

//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

//javax.xml.ws
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

//pt.upa.broker.ws
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.BrokerPortType;

//pt.upa.broker.ws.cli
import pt.upa.broker.ws.cli.BrokerClient;

import java.util.Map;

public class BrokerClientApplication {

        private static final String UDDI_URL = "http://localhost:9090";
        private static final String WS_NAME = "UpaBroker1";
        
	public static void main(String[] args) throws Exception {
                //Check Arguments
//                 if (args.length < 2) {
// 			System.err.println("Argument(s) missingimport static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;!");
// 			System.err.printf("Usage: java %s uddiURL name%n", BrokerClientApplication.class.getName());
// 			return;
// 		}
		
		BrokerClient client = getBrokerClient();

		System.out.println(client.requestTransport("Guarda","Viseu", 49));
		
	}
	
	public static BrokerClient getBrokerClient() throws Exception
	{
                System.out.println("=======================================================================================");
                System.out.println("========================== Starting up Broker client... ==========================");
	
                String uddiURL = UDDI_URL; 
                String name = WS_NAME;
	
		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

		System.out.printf("Looking for '%s'%n", name);
		String endpointAddress = uddiNaming.lookup(name);

		if (endpointAddress == null) {
			System.out.println("Unable to get endpoint address of service\"" + name + "\" at \"" + uddiURL + "\"");
			return null;
		} else {
			System.out.println("Found endpoint address \"" + endpointAddress + "\" for name \"" + name + "\".");
		}

		System.out.println("Creating stub ...");
		BrokerService service = new BrokerService();
		BrokerPortType port = service.getBrokerPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

		BrokerClient client = new BrokerClient(port);
// 		System.out.println(client.ping("asds"));
		
		System.out.println("=======================================================================================");
                
                return client;
	}

}
