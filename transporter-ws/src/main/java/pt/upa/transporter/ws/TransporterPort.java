package pt.upa.transporter.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;

import java.lang.Math;

@WebService(endpointInterface = "pt.upa.transporter.ws.TransporterPortType")
public class TransporterPort implements TransporterPortType {
        
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
        
        private static final String[] JOB_STATUS_LIST= {"PROPOSED",
                                                        "REJECTED",
                                                        "ACCEPTED",
                                                        "HEADING",
                                                        "ONGOING",
                                                        "COMPLETED"
                                                       };
        
        
        /**
         * Used to store active jobs.
         * Using a linked list makes element removal more efficient ( complexity O(1)).
         * 
         * @see java.util.LinkedList
         */
        private LinkedList<JobView> _jobs;
        
        /**
         *  Name assigned to this transporter.
         *  UpaTransporter1, UpaTransporter2, etc...
         */
        private String _transporterName;
        
        /**
         *  Number assigned to this transporter.
         *  UpaTransporter1 => _transporterNumber = 1
         */
        private int _transporterNumber;
        
        // === LOCAL PUBLIC METHODS ==========================================================================
        /**
         * Constructor.
         */
        public TransporterPort(String transporter_name)
        {
            _jobs = new LinkedList<JobView>();
            
            _transporterName = transporter_name;
            
            //The number of the transport is the last character in the name.
            char number = _transporterName.charAt(_transporterName.length() - 1);
            _transporterNumber = Character.getNumericValue(number);
            
            System.out.println("Number: " + _transporterNumber + " of transport " + _transporterName);
            
        }
        
         

        
        
        // === REMOTE METHODS ==================================================================================
        /**
	 * Deletes all jobs.
	 * 
	 * {@link TransporterPort#_jobs}
	 */
	public void clearJobs(){
            _jobs = new LinkedList<JobView>();
	}
	
	/**
	 * Returns a list of all jobs stored.
	 * 
	 * @see TransporterPort#_jobs
	 * @return colletion of JobView objects representing all active jobs.
	 *     @see pt.upa.transporter.ws.JobView
	 *     @see java.util.ArrayList
	 * 
	 */
	public List<JobView> listJobs(){
            List<JobView> list = new ArrayList<JobView>();
            list.addAll(_jobs);
	
            return list;
	}
	
	/**
	 * Returns the general information about a job.
	 * 
	 * @param id identifier of job.
	 * @return element of JobView enum.
	 *     @see pt.upa.transporter.ws.JobView
	 */
	public JobView jobStatus(String id){
            
            return null;
	}
	
	/**
	 * [Insert description here]
	 * 
	 * @param id identifier of job.
	 * @param accept
	 * @return JobView object.
	 *     @see pt.upa.transporter.ws.JobView
	 */
	public JobView decideJob(String id,boolean accept) throws BadJobFault_Exception
	{
            return null;
	}
	
	/**
	 * Receives a job proposal and responds with an offer.
	 * 
	 * @param origin text description of the origin of job.
	 * @param destination text description of the destination of job. 
	 * @return JobView object of the job.
	 *     @see pt.upa.transporter.ws.JobView
	 *     
	 * @throws pt.upa.transporter.ws.BadLocationFault_Exception
	 * @throws pt.upa.transporter.ws.BadPriceFault_Exception
	 */
	public JobView requestJob(String origin, String destination, int price)
        throws BadLocationFault_Exception, BadPriceFault_Exception
	{
            if(!locationIsKnown(origin))
            {
                BadLocationFault fault = new BadLocationFault();
                fault.setLocation(origin);
                    
                throw new BadLocationFault_Exception("Unknown origin location.", fault);
            }
            
            if(!locationIsKnown(destination))
            {
                BadLocationFault fault = new BadLocationFault();
                fault.setLocation(destination);
                    
                throw new BadLocationFault_Exception("Unknown destination location.", fault);
            }
            
            if(price < 0)
            {
                BadPriceFault fault = new BadPriceFault();
                fault.setPrice(price);
            
                throw new BadPriceFault_Exception("Price below zero.", fault);
            }
                
                
            //Check if transporter operates in region of origin and destination
            if(!transporterOperatesInLocation(origin))
            {
                BadLocationFault fault = new BadLocationFault();
                fault.setLocation(origin);
                    
                throw new BadLocationFault_Exception("Transporter \"" + _transporterName + "\" does not operate in given origin location.", fault);
            }
            
            if(!transporterOperatesInLocation(destination))
            {
                BadLocationFault fault = new BadLocationFault();
                fault.setLocation(destination);
                    
                throw new BadLocationFault_Exception("Transporter \"" + _transporterName + "\" does not operate in given destination location.", fault);
            }
            
            float proposed_price = (float)price; 
            
            if(price<10)
            {
                proposed_price = proposed_price * 0.7f;
            }
            else if(price > 100)
            {
                return null;
            }
            else
            {
                if(_transporterNumber % 2 == 0)
                {
                    if(price % 2 != 0)
                    {
                        //retorna preco acima do dado
                        proposed_price += proposed_price * 0.3f;
                    }
                    else
                    {
                        //retorn preço abaixo do dado
                        proposed_price = proposed_price * 0.7f;
                    }
            
                } else
                {
                    if(price % 2 != 0)
                    {
                        //retorna preco abaixo do dado
                        proposed_price = proposed_price * 0.7f;
                    }
                    else
                    {
                        //retorna preco acima do dado
                        proposed_price += proposed_price * 0.3f;
                    }
                }
            
            }
            
            JobView proposed_job = createProposedJob(origin, destination, Math.round(proposed_price));
            
	
            return proposed_job;
	}
	
	/**
	 * Basic operation to check if server is responsive.
	 * 
	 * @param name name of the sender.
	 * @return string containing the server response.
	 */
        public String ping(String name){
        
            return "<<< Pinged by \"" + name + "\"! >>>";
        }
        
        
        // === LOCAL PRIVATE METHODS ==================================================================================
        /**
         *  Creates a new job and adds it to the list of active jobs.
         */
        private JobView createProposedJob(String origin, String destination, int price)
        {  
           JobView new_job = new JobView();
           
           new_job.setCompanyName("");
           new_job.setJobOrigin(origin);
           new_job.setJobDestination(destination);
           new_job.setJobPrice(price);
           
           new_job.setJobIdentifier("");
           
           //set state to the initial one.
           new_job.setJobState(getJobState(JOB_STATUS_LIST[0]));
           
           _jobs.add(new_job);
           
           return new_job;
        }
        
        
        /**
         *  Retrieves enumerator value.
         *  
         *  @param state_name name of the enumerator element.
         *  @return corresponding JobStateView enumerator element.
         *  
         *  Possible names are stored in a static array.
         *  @see TransporterPort#JOB_STATUS_LIST
         */
        private JobStateView getJobState(String state_name)
        {
            return JobStateView.fromValue(state_name);
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
         *  Checks whether this transporter operates in given location.
         *  
         *  @return True if transporter does operate in the location. False otherwise.
         */
        private boolean transporterOperatesInLocation(String location)
        {
            boolean result = true;
        
            //case transporter number is even (par)
            if(_transporterNumber % 2 == 0)
            {  
                if(!Arrays.asList(LOCATIONS_NORTH_REGION).contains(location))
                {
                    if(!Arrays.asList(LOCATIONS_CENTER_REGION).contains(location))
                    {
                        result = false;
                    }
                }
            }
            //case transporter number is odd (impar)
            else
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