package pt.upa.broker.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import pt.upa.transporter.ws.JobView;
//pt.upa.transporter.ws
import pt.upa.transporter.ws.cli.TransporterClient;

@WebService(endpointInterface = "pt.upa.broker.ws.BrokerPortType")
public class BrokerPort implements BrokerPortType {        
        
        private static final int BAD_LOCATION_FAULT_VALUE = -2;
        private static final int BAD_PRICE_FAULT_VALUE = -3;
        
        private static final String[] TRANSPORT_STATUS_LIST = { "REQUESTED",
                                                                "BUDGETED",
                                                                "FAILED",
                                                                "BOOKED",
                                                                "HEADING",
                                                                "ONGOING",
                                                                "COMPLETED"
                                                                };
                                                                
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
        
        
        /** */
        private List<TransporterClient> _transporters;
        /** */
        private LinkedList<TransportView> _transports;
        
        public BrokerPort()
        {
            _transporters = new LinkedList<TransporterClient>();              
        }
        
        //======= Local public methods ==================================================
       
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
	//======= Remote methods ===================================================
	
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

        // Devolve o estado actual do Transport id
	public TransportView viewTransport(String id)
		throws UnknownTransportFault_Exception {
			return null;
	}

        // Tenta marcar um transporte de uma origem para um destido, com um preco maximo maior que 0
	public String requestTransport(String origin, String destination, int price)
		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

		if(price < 0) {
			
			InvalidPriceFault fault = new InvalidPriceFault();
			fault.setPrice(price);
			
			throw new InvalidPriceFault_Exception("Given price is lower than zero.",fault);
		}
		
		if(!locationIsKnown(origin)) {
                        UnknownLocationFault fault = new UnknownLocationFault();
                        fault.setLocation(origin);
		   
			throw new UnknownLocationFault_Exception("Location is unknown",fault);
		}
		
		if(!locationIsKnown(destination)) {
			UnknownLocationFault fault = new UnknownLocationFault();
                        fault.setLocation(destination);
		   
			throw new UnknownLocationFault_Exception("Location is unknown",fault);
		}
		
		int num_transporters = _transporters.size();
		
		int[] proposed_prices = new int[num_transporters];
		int transporter;
		
		//pede orçamentos aos transporters
		for(transporter = 0; index < num_transporters; index++)
		{
                        String proposal_job_id = null;
		
                        proposed_prices[transporter] = _transporters.get(transporter).requestJob(origin,destination,price, proposal_job_id);
                        
                        if(proposed_prices[trasporter] == BAD_LOCATION_FAULT_VALUE)
                        {
                            UnavailableTransportFault fault = new UnavailableTransportFault();
                            fault.setOrigin(origin);
                            fault.setDestination(destination);
                        
                            throw new UnavailableTransportFault_Exception("",fault);
                        }
                        
                        if(proposed_prices[transporter] > 0)
                        {
                            createRequestedTransport(origin,destination, price, proposal_job_id);
                        }
                        
		}
		
		int lowest_price = Integer.MAX_VALUE;
		
		for(int prop_price : proposed_prices)
                {                
                    if(prop_price < lowest_price)
                        lowest_price = prop_price;
                }
                
                if(lowest_price > price)
                {
                    UnavailableTransportPriceFault fault = new UnavailableTransportPriceFault();
                    fault.setBestPriceFound(lowest_price);
                
                    throw new UnavailableTransportPriceFault_Exception("All offers received have prices above limit.", fault);
                }
		
// 		int num_bad_location_errors = 0;
// 		for(int i : proposed_prices)
// 		{
//                     
// 		}
		
// 		for(TransportView transport : _transports) {
// 			if(transport.getOrigin().equals(origin) && transport.getDestination().equals(destination)) {
// 				if(transport.getPrice() <= price) {
// 					//TODO Adiciona o servico?
// 				} else {
// 					throw new UnavailableTransportPriceFault_Exception(destination, null); 
// 				}
// 			} else { 	
// 				throw new UnavailableTransportFault_Exception(destination, null);
// 			}
// 		}
		return "Transport Requested - " + "Origin: " + origin + "; Destination" + destination + "; Price: " + price;
	}
			
		
	

	public String ping(String name) {
		return "<<< Broker Pinged by \"" + name + "\"! >>>";	
	}
	
	// ========== Local private methods ========================================================
	
	private TransportView createRequestedTransport(String origin, String destination, int price, String id)
	{
            TransportView new_transport = new TransportView();
            new_transport.setOrigin(origin);
            new_transport.setDestination(destination);
            
            new_transport.setPrice(price);
            new_transport.setId(id);
            
            new_transport.setState(getTransportState(TRANSPORT_STATUS_LIST[0]));
            
            _transports.add(new_transport);
            
            return new_transport;
	}
	
	/**
         *  Retrieves job state enumerator value.
         *  
         *  @param state_name name of the enumerator element.
         *  @return corresponding JobStateView enumerator element.
         *  
         *  Possible names are stored in a static array.
         *  @see TransporterPort#JOB_STATUS_LIST
         */
        private TransportStateView getTransportState(String state_name)
        {
            return TransportStateView.fromValue(state_name);
        }
	
	/**
         *  Checks whether a location is known or not.
         *  
         *  @return true if location is known, false otherwise.
         */
        private boolean locationIsKnown(String location)
        {
            boolean result = true;
        
            if(!Arrays.asList(LOCATIONS_NORTH_REGION).contains(location))
            {
                if(!Arrays.asList(LOCATIONS_CENTER_REGION).contains(location))
                {
                    if(!Arrays.asList(LOCATIONS_SOUTH_REGION).contains(location))
                    {
                        result = false;
                    }
                }
            }
            
            return result;
        }
        

}