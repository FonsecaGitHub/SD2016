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
        
        private static final int BASE_ID_ARRAY_SIZE = 1024;
        
        /**
         *  These values are used to fill the job ID status list.
         *      @see BrokerPort#_jobIdList
         */
        private static final int ID_TAKEN = 1;
        private static final int ID_AVAILABLE = 0;
        
        /**
         *  Array used to check if an id is taken or not.
         *  The index of each position is the ID.
         *  If a position is taken, the ID is in use and can't be assigned to a new job.
         */
        private int[] _jobIdList;
        
        /** */
        private List<TransporterClient> _transporters;
        /** */
        private LinkedList<TransportView> _transports;
        
        public BrokerPort()
        {
            _transporters = new LinkedList<TransporterClient>();              
            _jobIdList = new int[BASE_ID_ARRAY_SIZE];
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
		int index;
		
		//pede orçamentos aos transporters
		for(index = 0; index < num_transporters; index++)
		{
                        proposed_prices[index] = _transporters.get(index).requestJob(origin,destination,price);
                        
                        if(proposed_prices[index] == BAD_LOCATION_FAULT_VALUE)
                        {
                            UnavailableTransportFault fault = new UnavailableTransportFault();
                            fault.setOrigin(origin);
                            fault.setDestination(destination);
                        
                            throw new UnavailableTransportFault_Exception("",fault);
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
        
        /**
         *  Generate a new ID to assign a job.
         * 
         *  @return an available ID.
         */
        private int generateId()
        {
            int i;
            int result = 0;
            
            for(i = 0 ; i<_jobIdList.length; i++)
            {
                if(_jobIdList[i] == ID_AVAILABLE)
                {
                    result = i;
                    break;
                }
            }
            
            //this id is now taken
            _jobIdList[i] = ID_TAKEN;
            
            return result;
        }
}