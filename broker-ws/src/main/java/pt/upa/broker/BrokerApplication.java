package pt.upa.broker;

//pt.upa.transporter
import pt.upa.transporter.TransporterClientApplication;

//pt.upa.transporter.ws
import pt.upa.transporter.ws.TransporterPortType;

//java.util
import java.util.Map;

//jax
import javax.xml.ws.Endpoint;
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

//service
import pt.upa.broker.ws.BrokerPort;

/**
 * Broker server application (main).
 */
public class BrokerApplication {

	public static void main(String[] args) throws Exception {

		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
			return;
		}
                
                System.out.println("=======================================================================================");
                System.out.println("=========================== Starting up Broker server... ===========================");
                
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");

		String uddiURL = args[0]; //http://localhost:9090
		String name = args[1]; //UPABroker1
		String url = args[2]; //http://localhost:8081/broker-ws/endpoint

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		TransporterPortType port = null;

		try {
                        port = TransporterClientApplication.connect();
			
			endpoint = Endpoint.create(new BrokerPort());

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);

			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();
			

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", url);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					System.out.printf("Deleted '%s' from UDDI%n", name);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}

	}

}
