package pt.upa.broker.ws;

import javax.jws.WebService;

//pt.upa.transporter.ws
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.List;
import java.util.LinkedList;

@WebService(endpointInterface = "pt.upa.broker.ws.BrokerPortType")
public class BrokerPort implements BrokerPortType {

        private static final String[] LOCATIONS_NORTH_REGION =  { "Porto",
                                                                  "Braga",
                                                                  "Viana do Castelo",
                                                                  "Vila Real",
                                                                  "Bragança"
                                                                };
        private static final String[] LOCATIONS_CENTER_REGION = { "Lisboa",
                                                                  "Leiria",
                                                                  "Santarem",
                                                                  "Castelo Branco",
                                                                  "Coimbra",
                                                                  "Aveiro",
                                                                  "Viseu",
                                                                  "Guarda"
                                                                };
        private static final String[] LOCATIONS_SOUTH_REGION =  { "Setubal",
                                                                  "Evora",
                                                                  "Portalegre",
                                                                  "Beja",
                                                                  "Faro"
                                                                };
        
        
        private List<TransporterClient> _transporters;
        
        public BrokerPort()
        {
            _transporters = new LinkedList<TransporterClient>();
        }
        
        //======= Metodos genericos. ==================================================
        public void addTransporter(TransporterClient new_transport)
        {
            _transporters.add(new_transport);
        }
        
        public void setTransporters(TransporterClient[] transporters)
        {
            _transporters = new LinkedList<TransporterClient>();
            
            for(TransporterClient transporter : transporters)
            {
                _transporters.add(transporter);
            }
            
            TransporterClient[] transporter_array = new TransporterClient[_transporters.size()];
            transporter_array = _transporters.toArray(transporter_array);
            
            System.out.println("_______________________________________");
            System.out.println("---------------------------------------");
            System.out.println("[INFO] Setting list of transporters: ");
            for(TransporterClient trans : transporter_array)
            {
                System.out.println("      -----> " + trans.getName());
            }
            System.out.println("_______________________________________");
            System.out.println("---------------------------------------");
        }
        
        

        //==============================================================================
	//======= Invocaçoes remotas ===================================================
	
	public void clearTransports() {
            for(TransporterClient transporter : _transporters)
            {
                transporter.clearJobs();
            }
            
            _transporters = new LinkedList<TransporterClient>();
	}

	public List<TransportView> listTransports() {
		return null;
	}

	public TransportView viewTransport(String id)
		throws UnknownTransportFault_Exception {
			return null;
	}

	public String requestTransport(String origin, String destination, int price)
		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
			
		
		
                return null;
	}

	public String ping(String name) {
		return "<<< Broker Pinged by \"" + name + "\"! >>>";	
	}
}
