package pt.upa.broker.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.Map;

import java.util.Timer;

import java.lang.StringBuilder;

import pt.upa.transporter.ws.JobView;
//pt.upa.transporter.ws
import pt.upa.transporter.ws.cli.TransporterClient;

@WebService(endpointInterface = "pt.upa.broker.ws.BrokerPortType")
public class BrokerPort implements BrokerPortType {        

	/* BrokerPort Backup
	 * timeouts
         * listas de conteudo a ser replicado
	 */

	private static final long IM_ALIVE_MESSAGE_PERIOD = 1000; //ms
	private static final long MAIN_BROKER_ALIVE_TIMEOUT = 1500; //ms
	

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
        
        
        /**
         * Represents broker's status as a backup of another one.
         * Is set to true if it is a backup.
         */
	private boolean _isBackup;

        /** */
        private List<TransporterClient> _transporters;
        /** */
        private LinkedList<TransportView> _transports;
        
	public BrokerPort()
        {
	    _isBackup = false;
	    
            _transporters = new LinkedList<TransporterClient>(); 
            _transports = new LinkedList<TransportView>();

            /**
             * Schedule sending "IM ALIVE" message to backup broker.
             * First execution after IM_ALIVE_MESSAGE_PERIOD ms.
             * Then execute every IM_ALIVE_MESSAGE_PERIOD ms.
             * 
             * @see https://docs.oracle.com/javase/7/docs/api/java/util/Timer.html#schedule%28java.util.TimerTask,%20long,%20long%29
             */
            new java.util.Timer().schedule( new java.util.TimerTask() {
	      @Override
	      public void run() {
	    	  //TODO
	    	  nameThread("main");
	    	  
	    	  this.notifyAll();
	    	  System.out.println("Main Broker is running...");
	    	  
	      }
	    },
	    IM_ALIVE_MESSAGE_PERIOD,
	    IM_ALIVE_MESSAGE_PERIOD
	    );
        }
	

        public BrokerPort(boolean backup)
        {
	    if(backup)
            {    
                _isBackup = true;  
                
                /**
                 * Schedule checking if received "IM ALIVE" message from main broker.
                 * First execution after MAIN_BROKER_ALIVE_TIMEOUT ms.
                 * Then execute every MAIN_BROKER_ALIVE_TIMEOUT ms.
                 * 
                 * @see https://docs.oracle.com/javase/7/docs/api/java/util/Timer.html#schedule%28java.util.TimerTask,%20long,%20long%29
                 */
                new java.util.Timer().schedule( new java.util.TimerTask() {
                    @Override
                    public void run() {
                        //TODO
                    	nameThread("backup");
                    	
                    	try {
							wait();
						} catch (InterruptedException e) {
							System.out.println("Still waiting for Main Broker...");
							e.printStackTrace();
						}
                    	
                    	// se nao receber mensagem de main broker dentro do tempo estipulado...
                    	
                   
                    	
                    	notifyAll();
                    	
                    	System.out.println("Backup Broker is running...");
                    	
                    }
                    },
                    MAIN_BROKER_ALIVE_TIMEOUT,
                    MAIN_BROKER_ALIVE_TIMEOUT
                );
            }
            else
            {
                _isBackup = false;
                
                /**
                 * Schedule sending "IM ALIVE" message to backup broker.
                 * First execution after IM_ALIVE_MESSAGE_PERIOD ms.
                 * Then execute every IM_ALIVE_MESSAGE_PERIOD ms.
                 * 
                 * @see https://docs.oracle.com/javase/7/docs/api/java/util/Timer.html#schedule%28java.util.TimerTask,%20long,%20long%29
                 */
                new java.util.Timer().schedule( new java.util.TimerTask() {
                    @Override
                    public void run() {
                        //TODO
                    	System.out.println("Main Broker is running...");
                    	
                    	notifyAll();
                    	
                    	
                    }
                    },
                    IM_ALIVE_MESSAGE_PERIOD,
                    IM_ALIVE_MESSAGE_PERIOD
                );
            }
                
            _transporters = new LinkedList<TransporterClient>(); 
            _transports = new LinkedList<TransportView>();
        }
        

        //=== Main/Backup management ===
        
        /**
         * Puts the thread to sleep for an ammount of time (seconds)
         * @param seconds
         */
        private void nap(int seconds) {
            try {
                System.out.printf("%s %s>%n    ", Thread.currentThread(), this);
                System.out.printf("Sleeping for %d seconds...%n", seconds);

                Thread.sleep(seconds*1000);

                System.out.printf("%s %s>%n    ", Thread.currentThread(), this);
                System.out.printf("Woke up!%n");

            } catch(InterruptedException e) {
                System.out.printf("%s %s>%n    ", Thread.currentThread(), this);
                System.out.printf("Caught exception: %s%n", e);
            }
        }
        
        /**
         * Changes the name of the current Thread 
         * for easier identification
         * @param name
         */
        private void nameThread(String name) {
        	Thread t = Thread.currentThread();
        	t.setName(name);
        }
        
        /**
         * print current thread and object instance executing the operation
         * @param name
         * @return
         */
        public String sayHello(String name) {
            // print current thread and object instance executing the operation
            System.out.printf("%s %s>%n    ", Thread.currentThread(), this);
            System.out.printf("sayHello(%s)%n", name);

            // sleep
            //TODO check value
            nap(5);
            // execute operation
            String result = "Hello " + name + "!";

            System.out.printf("%s %s>%n    ", Thread.currentThread(), this);
            System.out.printf("result=%s%n", result);
            return result;
        }
        
        
        
        //======= Local public methods ==================================================
       
       public void populate()
       {
       
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
	//======= Remote methods ===================================================
	
	public void clearTransports() {
            for(TransporterClient transporter : _transporters)
            {
                transporter.clearJobs();
            }
            
            _transporters = new LinkedList<TransporterClient>();
	}

	public List<TransportView> listTransports() {
            List<TransportView> list = new ArrayList<TransportView>();
            list.addAll(_transports);
		
            return list;
	}

        // Devolve o estado actual do Transport id
	public TransportView viewTransport(String id)
		throws UnknownTransportFault_Exception {

// 		for(TransportView transport : _transports) {
// 			if(transport.getId().equals(id)) {
// 				System.out.println("Transport: " + transport.getId() + " is in state: " + transport.getState());
// 				return transport;
// 			}
// 		}
            
                String updated_state = null;
                boolean found_job = false;
            
                
                for(TransporterClient client : _transporters)
                {
                    //remote invocation, returns state of job, if found
                    updated_state = client.jobStatus(id);
                
                    if(updated_state != null)
                    {
                        found_job = true;
                        break;
                    }
                    
                }
                
                if(!found_job)
                {
                    UnknownTransportFault fault = new UnknownTransportFault();
                    fault.setId(id);
                
                    throw new UnknownTransportFault_Exception("Transport with given ID was not found in any transporter.", fault);
                }
                
                
                TransportView result = null;
                boolean found_transport = false;
                
                for(TransportView transport : _transports)
                {
                    if(id.equals(transport.getId()))
                    {
                        result = transport;
                        found_transport = true;
                    }
                }
                
                if(!found_transport)
                {
                    UnknownTransportFault fault = new UnknownTransportFault();
                    fault.setId(id);
                
                    throw new UnknownTransportFault_Exception("Transport with given ID was found on a transporter, but not found in broker's list. WTF?", fault);
                }
                
                if(updated_state.equals("HEADING") || updated_state.equals("ONGOING") || updated_state.equals("COMPLETED"))
                    result.setState(stringToTransportState(updated_state));
                
		return result;
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
		String[] proposed_identifiers = new String[num_transporters];
		int transporter;
		
		//pede orçamentos aos transporters
		for(transporter = 0; transporter < num_transporters; transporter++)
		{
                        StringBuilder proposal_job_id = new StringBuilder();
		
                        proposed_prices[transporter] = _transporters.get(transporter).requestJob(origin,destination,price, proposal_job_id);
                        proposed_identifiers[transporter] = proposal_job_id.toString();
                        
//                         System.out.println(proposal_job_id.toString());
                        
//                         if(proposed_prices[transporter] == BAD_LOCATION_FAULT_VALUE)
//                         {
//                             UnavailableTransportFault fault = new UnavailableTransportFault();
//                             fault.setOrigin(origin);
//                             fault.setDestination(destination);
//                         
//                             throw new UnavailableTransportFault_Exception("",fault);
//                         }
                        
//                         if(proposed_prices[transporter] > 0 && proposed_prices[transporter] <= price)
//                         {
//                             createRequestedTransport(origin,destination, price, proposal_job_id.toString());
//                         }
                        
		}
		
		if(allProposalsRejected(proposed_prices))
		{
                    //TODO: tratar dos transportes requested?
		
                    UnavailableTransportFault fault = new UnavailableTransportFault();
                    fault.setOrigin(origin);
                    fault.setDestination(destination);
                        
                    System.out.println("============================================================================");
                    System.out.println("Rejected request for transport between \"" + origin + "\" and \"" + destination + "\".");
                    System.out.println("All transporters have rejected the offer with price limit " + price + ".");
                    for(int i = 0; i < proposed_prices.length; i++)
                    {
                        System.out.println(proposed_prices[i]);
                    }
                    System.out.println("============================================================================");    
                    
                    throw new UnavailableTransportFault_Exception("All transporters rejected request.",fault);
		}
		
		
		int lowest_price = Integer.MAX_VALUE;
		int lowest_price_index = 0;
		
		for(int i=0; i<proposed_prices.length; i++ )
                {     
                
                    if(proposed_prices[i] <= 0)
                        continue;
                      
                
                    if(proposed_prices[i] < lowest_price && proposed_prices[i] > 0)
                    {
                        lowest_price = proposed_prices[i];
                        lowest_price_index = i;
                    }
                    
                    if(proposed_prices[i] <= price)
                        createBudgetedTransport(origin,destination,proposed_prices[i], proposed_identifiers[i]);
                }
                
                if(lowest_price > price)
                {
                    //TODO: tratar dos transportes requested?
                
                    UnavailableTransportPriceFault fault = new UnavailableTransportPriceFault();
                    fault.setBestPriceFound(lowest_price);
                    
                    System.out.println("============================================================================");
                    System.out.println("Rejected request for transport between \"" + origin + "\" and \"" + destination + "\".");
                    System.out.println("All offers received have prices above limit (" + price + ").");
                    for(int i = 0; i < proposed_prices.length; i++)
                    {
                        System.out.println(proposed_prices[i]);
                    }
                    System.out.println("============================================================================");
                
                    throw new UnavailableTransportPriceFault_Exception("All offers received have prices above limit.", fault);
                }
                
                
                System.out.println("============================================================================");
                StringBuilder builder = new StringBuilder();
                
                for(int i = 0; i < proposed_prices.length; i++)
                {
                    builder.append(proposed_prices[i]);
                    if(proposed_prices[i] == -1)
                        builder.append(" (transporter rejected price above 100)");
                    else if(proposed_prices[i] == -2)
                        builder.append(" (does no operate in location)");
                    else if(proposed_prices[i] == -3)
                        builder.append(" (transporter rejected price below zero)");
                    
                    builder.append("\n");
                }
               
                System.out.println("Received request for transport between \"" + origin + "\" and \"" + destination + "\".");
                System.out.println("Responding to request with max price = " + price + ". Prices returned are:");
                System.out.println(builder.toString());
                
                builder = new StringBuilder();
                
                for(int i = 0; i < proposed_identifiers.length; i++)
                {
                    builder.append(proposed_identifiers[i] + "\n");
                }
                
                System.out.println("With identifiers:");
                System.out.println(builder.toString());
                System.out.println("============================================================================");
                
                TransporterClient lowest_price_transporter = _transporters.get(lowest_price_index);
                
                int transporter_response = lowest_price_transporter.decideJob(proposed_identifiers[lowest_price_index], true);
                
                //transporter enviou resposta positiva
                if(transporter_response == 0)
                {
                    createBookedTransport(origin, 
                                          destination, 
                                          proposed_prices[lowest_price_index], 
                                          proposed_identifiers[lowest_price_index]);
                }
                //transporter enviou resposta negativa
                else
                {
                    createFailedTransport(origin, 
                                          destination, 
                                          proposed_prices[lowest_price_index], 
                                          proposed_identifiers[lowest_price_index]);
                }
                
                printTransports();
                
                //retorna identificador do transporte com o preço mais baixo
                return proposed_identifiers[lowest_price_index];

	}
			
		
	

	public String ping(String name) {
		return "<<< Broker Pinged by \"" + name + "\"! >>>";	
	}
	
	// ========== Local private methods ========================================================
	
	/**
	 * Check if all transporters reject requests.
	 * Note: rejected requests have proposed prices below 0.
	 */
	private boolean allProposalsRejected(int[] prices)
	{    
            for(int price : prices)
            {
                if(price >= 0)
                    return false;
            }
            
            return true;
            
	}
	
	private TransportView createRequestedTransport(String origin, String destination, int price, String id)
	{
            TransportView new_transport = new TransportView();
            new_transport.setOrigin(origin);
            new_transport.setDestination(destination);
            
            new_transport.setPrice(price);
            new_transport.setId(id);
            
            new_transport.setState(stringToTransportState("REQUESTED"));
            
            int existing_transport_index = getIndexOfTransportWithIdentifier(id);
            
            if(existing_transport_index  == -1)
            {
                _transports.add(new_transport);
            }
            else
            {
                _transports.set(existing_transport_index, new_transport);
            }
            
            
            return new_transport;
	}
	
	private TransportView createBudgetedTransport(String origin, String destination, int price, String id)
	{
            TransportView new_transport = new TransportView();
            new_transport.setOrigin(origin);
            new_transport.setDestination(destination);
            
            new_transport.setPrice(price);
            new_transport.setId(id);
            
            new_transport.setState(stringToTransportState("BUDGETED"));
            
            int existing_transport_index = getIndexOfTransportWithIdentifier(id);
            
            if(existing_transport_index == -1)
            {
                _transports.add(new_transport);
            }
            else
            {
                _transports.set(existing_transport_index, new_transport);
            }
            
            return new_transport;
	}
	
	private TransportView createBookedTransport(String origin, String destination, int price, String id)
	{
            TransportView new_transport = new TransportView();
            new_transport.setOrigin(origin);
            new_transport.setDestination(destination);
            
            new_transport.setPrice(price);
            new_transport.setId(id);
            
            new_transport.setState(stringToTransportState("BOOKED"));
            
            int existing_transport_index = getIndexOfTransportWithIdentifier(id);
            
            if(existing_transport_index == -1)
            {
                _transports.add(new_transport);
            }
            else
            {
                _transports.set(existing_transport_index, new_transport);
            }
            
            return new_transport;
	}
	
	private TransportView createFailedTransport(String origin, String destination, int price, String id)
	{
            TransportView new_transport = new TransportView();
            new_transport.setOrigin(origin);
            new_transport.setDestination(destination);
            
            new_transport.setPrice(price);
            new_transport.setId(id);
            
            new_transport.setState(stringToTransportState("FAILED"));
            
            int existing_transport_index = getIndexOfTransportWithIdentifier(id);
            
            if(existing_transport_index == -1)
            {
                _transports.add(new_transport);
            }
            else
            {
                _transports.set(existing_transport_index, new_transport);
            }
            
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
        private TransportStateView stringToTransportState(String state_name)
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
        
        /**
         *  Returns name of transporter who set up a certain transport.
         *  If job identifier is "T1-N", then it was "UpaTransporter1" that set up the transport.
         */
        private String getTransporterNameFromJobId(String job_id)
        {
            String[] id_split = job_id.split("-");
            
            if(id_split[0].equals("T1"))
            {
                return "UpaTransporter1";
            }
            else if(id_split[0].equals("T2"))
            {
                return "UpaTransporter2";
            }
            else if(id_split[0].equals("T3"))
            {
                return "UpaTransporter3";
            }
            else if(id_split[0].equals("T4"))
            {
                return "UpaTransporter4";
            }
            else if(id_split[0].equals("T5"))
            {
                return "UpaTransporter5";
            }
            else if(id_split[0].equals("T6"))
            {
                return "UpaTransporter6";
            }
            else if(id_split[0].equals("T7"))
            {
                return "UpaTransporter7";
            }
            else if(id_split[0].equals("T8"))
            {
                return "UpaTransporter8";
            }
            else if(id_split[0].equals("T9"))
            {
                return "UpaTransporter2";
            }
            
            return null;
        }
        

        private int getIndexOfTransportWithIdentifier(String id)
        {
            int index = 0;
        
            for(TransportView transport : _transports)
            {
                if(transport.getId().equals(id))
                {
                    return index;
                }
                
                index++;
            }
            
            return -1;
        }
        
        private void printTransports()
        {
            System.out.println("_______________________________________________________________");
            System.out.println("_______________________________________________________________");
            System.out.println("Printing all transports:");
            for(TransportView transport : _transports)
            {
                System.out.println("Transport ID: \"" + transport.getId() + "\"." );
                System.out.println("Transport price: \"" + transport.getPrice() + "\"." );
                System.out.println("Transport state: \"" + transport.getState().value() + "\"." );
            }
            System.out.println("_______________________________________________________________");
            System.out.println("_______________________________________________________________");
        }
        
}