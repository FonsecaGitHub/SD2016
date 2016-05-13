package pt.upa.broker;

//pt.upa.transporter
import pt.upa.transporter.TransporterClientApplication;

//pt.upa.transporter.ws
import pt.upa.transporter.ws.cli.TransporterClient;

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
public class BrokerApplication 
{

	public static void main(String[] args) throws Exception {

		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL backupBool bakcupURL%n", BrokerApplication.class.getName());
			return;
		}

		System.out.println("#######################################################################################");
		System.out.println("############################ Starting up Broker server... #############################");

		System.out.println(BrokerApplication.class.getSimpleName() + " starting...\n");

		String uddiURL = args[0]; //http://localhost:9090
		String name = args[1]; //UpaBroker1
		String url = args[2]; //http://localhost:8091/broker-ws/endpoint
		boolean backup = Boolean.parseBoolean(args[3]);
		String backupURL = args[4]; //http://localhost:8092/broker-ws/endpoint

		if(backup) {
                        System.out.println("Lauching backup Broker server...");
			launchBackupBroker(uddiURL, name, backupURL);
		}

		launchMainBroker(uddiURL, name, url);

		//TODO remove this code, becomes obsolete
		/*	
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;


		try {
            TransporterClient[] clients = TransporterClientApplication.getTransporterList();

			BrokerPort broker = new BrokerPort();
			broker.setTransporters(clients);

			endpoint = Endpoint.create(broker);

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
		}*/

	}

	/**
	 * Starts the Main Broker Server
	 * @param uddiURL
	 * @param name
	 * @param url
	 */
	private static void launchMainBroker(String uddiURL, String name, String url) {
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;


		try {
			TransporterClient[] clients = TransporterClientApplication.getTransporterList();

			BrokerPort mainBroker = new BrokerPort();
			mainBroker.setTransporters(clients);

			endpoint = Endpoint.create(mainBroker);

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

	/**
	 * Starts the Backup Broker Server
	 * @param uddiURL
	 * @param name
	 * @param url
	 */
	private static void launchBackupBroker(String uddiURL, String name, String backupURL) {
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;


		try {
			TransporterClient[] clients = TransporterClientApplication.getTransporterList();

			BrokerPort backupBroker = new BrokerPort();
			backupBroker.setTransporters(clients);

			endpoint = Endpoint.create(backupBroker);

			// publish endpoint
			System.out.printf("Starting %s%n", backupURL);
			endpoint.publish(backupURL);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, backupURL);

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
					System.out.printf("Stopped %s%n", backupURL);
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
