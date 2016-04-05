package pt.upa.transporter;

//java.util
import java.util.Map;

//jax
import javax.xml.ws.Endpoint;
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;


//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

//serviço
//a classe que vinha vazia no projecto inicialmente
import pt.upa.transporter.ws.TransporterPort; //implements TransporterPortType

/**
 * Transporter server application (main).
 */
public class TransporterApplication {

	public static void main(String[] args) throws Exception {
                
                //Verifica argumentos:
                //args[0]: http://localhost:9090
                //args[1]: UpaTransporter{N}  
                //args[2]: http://localhost:808{N}/transporter-ws/endpoint
                //
                //N e um inteiro >= 1. Ou seja UpaTransporter1 vai para o porto 8081, UpaTransporter2 
                //vai para 8082, etc..
                //Por agora so existe o UpaTransporter1
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", TransporterApplication.class.getName());
			return;
		}
		
		System.out.println(TransporterApplication.class.getName() + " starting...");
		
		String uddiURL = args[0]; //http://localhost:9090
		String name = args[1]; //UpaTransporter1 
		String url = args[2]; //http://localhost:8081/transporter-ws/endpoint

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;

		try {
                        //criar endpoint com a class ..Port.java que vinha no proj inicialmente
			endpoint = Endpoint.create(new TransporterPort());

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
                                        System.out.printf("Deleting objects from name server...");
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



