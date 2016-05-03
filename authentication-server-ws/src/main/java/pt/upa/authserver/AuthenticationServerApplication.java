package pt.upa.authserver;

//ws
import pt.upa.authserver.ws.AuthenticationServerPort;

//javax
import javax.xml.ws.Endpoint;
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class AuthenticationServerApplication {

	public static void main(String[] args) throws Exception {
		
            //Verifica argumentos:
                //args[0]: http://localhost:9090
                //args[1]: AuthenticationServer 
                //args[2]: http://localhost:8070/authentication-server-ws/endpoint
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name url%n", AuthenticationServerApplication.class.getName());
			return;
		}
		
		System.out.println(AuthenticationServerApplication.class.getName() + " starting...");
		
		String uddiURL = args[0]; //uddi url
		String name = args[1]; //AuthenticationServer 
		String url = args[2]; //http://localhost:8070/authentication-server-ws/endpoint

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;

		try {
                        AuthenticationServerPort port = new AuthenticationServerPort();
		
                        //criar endpoint com a classe 
			endpoint = Endpoint.create(port);

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

