package pt.upa.transporter;

//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

//javax.xml.ws
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

//pt.upa.transporter.ws
import pt.upa.transporter.ws.TransporterService;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.JobView;

import java.util.Map;
import java.util.List;

public class TransporterClientApplication {

	private static final String WS_NAME = "UpaTransporter2";

	public static void main(String[] args) throws Exception {
		System.out.println("=======================================================================================");
                System.out.println("========================== Starting up Transporter client... ==========================");
                
                // Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", TransporterClientApplication.class.getName());
			return;
		}
		
		String uddiURL = args[0];
		String name = WS_NAME;

		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

		System.out.printf("Looking for '%s'%n", name);
		String endpointAddress = uddiNaming.lookup(name);
		
		if (endpointAddress == null) {
			System.out.println("Unable to get endpoint address of service\"" + name + "\" at \"" + uddiURL + "\"");
			return;
		} else {
			System.out.println("Found endpoint address \"" + endpointAddress + "\" for name \"" + name + "\".");
		}
		
		System.out.println("Creating stub ...");
		TransporterService service = new TransporterService();
		TransporterPortType port = service.getTransporterPort();

                System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
                
                System.out.println("============================= Ready for remote calls... ===============================");
                
                String result = port.ping("henrique");
                System.out.println(result);
                
                List<JobView> jobs = port.listJobs();
                System.out.println(jobs.get(0).getCompanyName());
                System.out.println(jobs.get(1).getCompanyName());
                
                System.out.println("=======================================================================================");
	}
}
