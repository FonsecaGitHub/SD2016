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

//pt.upa.transporter.ws.cli
import pt.upa.transporter.ws.cli.TransporterClient;

//java.util
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class TransporterClientApplication {

	private static final String UDDI_URL = "http://localhost:9090";
	private static final String[] TRANSPORTER_NAME_LIST = { "UpaTransporter1",
                                                                "UpaTransporter2",
                                                                "UpaTransporter3",
                                                                "UpaTransporter4",
                                                                "UpaTransporter5",
                                                                "UpaTransporter6",
                                                                "UpaTransporter7",
                                                                "UpaTransporter8",
                                                                "UpaTransporter9"};
	
	public static void main(String[] args) throws Exception {
            getTransporterList();
	}
	
	public static TransporterClient[] getTransporterList() throws Exception
	{
            System.out.println("=======================================================================================");
            System.out.println("======================= Fetching all available transporters... ========================");
                
            // Check arguments
//             if (args.length < 1) {
//                     System.err.println("Argument(s) missing!");
//                     System.err.printf("Usage: java %s uddiURL name%n", TransporterClientApplication.class.getName());
//                     return;
//             }
            
            TransporterClient[] result;
            List<TransporterClient> clients_found = new ArrayList<TransporterClient>();
            
            String uddiURL = UDDI_URL;

            System.out.printf("Contacting UDDI at %s%n", uddiURL);
            UDDINaming uddiNaming = new UDDINaming(uddiURL);

            //Procura todos os transporters
            for(String name : TRANSPORTER_NAME_LIST)
            {
                String endpointAddress = uddiNaming.lookup(name);
                
                if (endpointAddress != null) {
                    TransporterService service = new TransporterService();
                    TransporterPortType port = service.getTransporterPort();
                    
                    BindingProvider bindingProvider = (BindingProvider) port;
                    Map<String, Object> requestContext = bindingProvider.getRequestContext();
                    requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
                    
                    clients_found.add(new TransporterClient(name, port));
                }
                
                
                
            }
                
            System.out.println("=============================== Transporters ready. ===================================");
            
                
            System.out.println("=======================================================================================\n");
            
            
            
            //converter array list dos transportes para array
            result = new TransporterClient[clients_found.size()];
            result = clients_found.toArray(result);
            
            return result;
	}
}
