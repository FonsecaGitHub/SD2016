package pt.upa.broker.ws;

import javax.jws.WebService;

import org.apache.commons.lang.ArrayUtils;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;


import pt.upa.transporter.ws.JobView;
//pt.upa.transporter.ws
import pt.upa.transporter.ws.cli.TransporterClient;

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
        
        private LinkedList<TransportView> _transports;
        
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
		
	}

  // Devolve o estado actual do Transport id
	public TransportView viewTransport(String id)
		throws UnknownTransportFault_Exception {
			return null;
	}

  // Tenta marcar um transporte de uma origem para um destido, com um preco maximo maior que 0
	public String requestTransport(String origin, String destination, int price)
		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

		if(price < 0) {
			//TODO throw new InvalidPriceFault_Exception(price);
		}
		
		if(!ArrayUtils.contains(LOCATIONS_NORTH_REGION, origin) &&
		   !ArrayUtils.contains(LOCATIONS_CENTER_REGION, origin) &&
		   !ArrayUtils.contains(LOCATIONS_SOUTH_REGION, origin)) {
			//TODO throw new UnknownLocationFault_Exception();
		}
		
		if(!ArrayUtils.contains(LOCATIONS_NORTH_REGION, destination) &&
		   !ArrayUtils.contains(LOCATIONS_CENTER_REGION, destination) &&
		   !ArrayUtils.contains(LOCATIONS_SOUTH_REGION, destination)) {
			//TODO throw new UnknownLocationFault_Exception();
		}
		
		for(TransportView transport : _transports) {
			if(transport.getOrigin().equals(origin) && transport.getDestination().equals(destination)) {
				if(transport.getPrice() <= price) {
					//TODO Adiciona o servico?
				} else {
					throw new UnavailableTransportPriceFault_Exception(destination, null); 
				}
			} else { 	
				throw new UnavailableTransportFault_Exception(destination, null);
			}
		}
		return "Transport Requested - " + "Origin: " + origin + "; Destination" + destination + "; Price: " + price;
	}
			
		
	

	public String ping(String name) {
		return "<<< Broker Pinged by \"" + name + "\"! >>>";	
	}
}
