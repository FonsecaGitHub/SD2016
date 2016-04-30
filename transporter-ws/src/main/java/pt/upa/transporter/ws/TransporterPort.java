package pt.upa.transporter.ws;

import javax.jws.WebService;

import pt.upa.transporter.JobStateChangeSchedule;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.Random;

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
        
        //Maximum number of IDs that can be assigned to jobs. 
        //In other words, the maximum number oj jobs for a single transporter.
        private static final int BASE_ID_ARRAY_SIZE = 1024;
        
        /**
         *  These values are used to fill the job ID status list.
         *      @see TransporterPort#_jobIdList
         */
        private static final int ID_TAKEN = 1;
        private static final int ID_AVAILABLE = 0;
        
        //==================================== INSTANCE ATTRIBUTES ===========================================
        /**
         *  Array used to check if an ID is taken or not.
         *  The index of each position is the ID.
         *  If a position is taken, the ID is in use and can't be assigned to a new job.
         */
        private int[] _jobIdList;
        
        /**
         * Used to store active jobs.
         * Using a linked list makes element removal more efficient ( complexity O(1)).
         * 
         * @see java.util.LinkedList
         */
        private LinkedList<JobView> _jobs;
        
        /**
         * Stores a schedule for each job that is atleast in state ACCEPTED.
         * - After a job is assigned a schedule, we use the schedule to find the current state
         *   of the job (HEADING, ONGOIND, COMPLETED).
         * 
         * - Jobs mapped here must also be in the list of jobs.
         *   @see TransporterPort#_jobs
         */
        private HashMap<JobView, JobStateChangeSchedule> _schedules;
        
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
        
        /**
         *  Prefix that is added to jobIdentifier attributes.
         *  I.e. for UpaTransporter2, this is "T2".
         *  
         *  Job identifiers will look like "T2-32".
         *  The first half (plus the dash) is this prefix, i.e "T2-". 
         *  The second half is the unique identifier of the job.
         *  
         *  This is done so the broker can have a unique identifier for each job, for each transporter.
         */
        private final String _transporterIdPrefix; 
        
        //==================================== LOCAL PUBLIC METHODS ===========================================
        public void populate() {
        	//TODO add stuff here
        }
        
        /**
         * Constructor.
         */
        public TransporterPort(String transporter_name)
        {
            _jobs = new LinkedList<JobView>();
            _schedules = new HashMap<JobView, JobStateChangeSchedule>();
            _jobIdList = new int[BASE_ID_ARRAY_SIZE];
            
            _transporterName = transporter_name;
            
            //The number of the transport is the last character in the name.
            char number = _transporterName.charAt(_transporterName.length() - 1);
            _transporterNumber = Character.getNumericValue(number);
            
            _transporterIdPrefix = "T" + _transporterNumber + "-";
            
            System.out.println("Number: " + _transporterNumber + " of transport " + _transporterName);
            System.out.println("This transporter has prefix: " + _transporterIdPrefix);
            }
        
         

        
        
        // === REMOTE METHODS ==================================================================================
        /**
	 * Deletes all jobs.
	 * 
	 */
	public void clearJobs(){
            _jobs = new LinkedList<JobView>();
            _jobIdList = new int[BASE_ID_ARRAY_SIZE];
	}
	
	/**
	 * Returns a list of all jobs stored.
	 * 
	 * @return colletion of JobView objects representing all active jobs.
	 *     @see pt.upa.transporter.ws.JobView
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
            
            JobView result = null; 
            
            for(JobView job : _jobs)
            {
                if(job.getJobIdentifier().equals(id))
                {
                    
                    
                    if(_schedules.containsKey(job))
                    {
                        JobStateChangeSchedule job_schedule = _schedules.get(job);
                        
                        String job_updated_state_name = job_schedule.getCurrentState();
                    
                        System.out.println("**********************************************************************");
                        System.out.println("Received job state request for job \"" + job.getJobIdentifier() + "\": returning state \"" + job_updated_state_name + "\".");
                        System.out.println("**********************************************************************");
                        JobStateView job_updated_state = stringToJobState(job_updated_state_name);
                    
                        job.setJobState(job_updated_state);
                    
                    }
                    
                    result = job;
                }
            }
            
            return result;
	}
	
	/**
	 * Receives the clients decision for a proposed job. 
	 * If he accepted, then set job state to that one. 
	 * 
	 * @param id identifier of the job.
	 * @param accept is true if the client has accepted the proposal.
	 *        If he has, set job state to ACCEPTED. If he has rejected it, set job state to REJECTED.
	 *        
	 * @return JobView object representing the job with the new state (accepted, rejected).
	 *     @see pt.upa.transporter.ws.JobView
	 */
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception
	{
            boolean found_job_with_id = false; //set true if job is found
            JobView deciding_job = null; 
            int found_job_index = 0; //index of the job, if it is found.
            
            for(JobView job : _jobs)
            {
                if(job.getJobIdentifier().equals(id) && job.getJobState() == stringToJobState("PROPOSED"))
                {
                    found_job_with_id = true;
                    deciding_job = job;
                    break;
                }
                
                found_job_index++;
            }
	
            if(!found_job_with_id)
            {
                BadJobFault fault = new BadJobFault();
                fault.setId(id);
            
                throw new BadJobFault_Exception("Unable to find job with given id.", fault);
            }
            
            if(accept)
            {
                //set job state to accepted
                deciding_job.setJobState(stringToJobState("ACCEPTED"));
                _jobs.set(found_job_index, deciding_job); //replace old job with the new one
            
                System.out.println("============================================================================");
                System.out.println("Received job decision request. Returning job with state \"" + _jobs.get(found_job_index).getJobState().name() + "\".");
                System.out.println("----------------------------------------------------------------------------");
            }   
            else
            {
                //set job state to rejected
                deciding_job.setJobState(stringToJobState("REJECTED"));
                _jobs.set(found_job_index, deciding_job); //replace old job with the new one
            
                System.out.println("============================================================================");
                System.out.println("Received job decision request. Returning job with state \"" + _jobs.get(found_job_index).getJobState().name() + "\".");
                System.out.println("============================================================================");
            
                return deciding_job;
            }

            //if job was accepted by client we must now define timers for each state transition
            //i.e ACCEPTED -> HEADING, HEADING -> ONGOING, etc..
            System.out.println("Setting timers for state transitions...");
            
            JobStateChangeSchedule schedule = new JobStateChangeSchedule();
            _schedules.put(deciding_job, schedule);
            
            System.out.println("Generated delays:");
            System.out.println("ACCEPTED to HEADING: " + schedule.getAcceptedToHeadingDelay());
            System.out.println("HEADING to ONGOING: " + schedule.getHeadingToOngoingDelay());
            System.out.println("ONGOING to COMPLETED: " + schedule.getOngoingToCompletedDelay());
            
            System.out.println("============================================================================");
            
            return deciding_job;
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
            
            if(price<=10)
            {
                proposed_price = proposed_price * 0.6f;
            }
            else if(price > 100)
            {
                return null;
            }
            else
            {
                //transporter PAR
                if(_transporterNumber % 2 == 0)
                {
                    //preço IMPAR
                    if(price % 2 != 0)
                    {
                        //retorna preco acima do dado
                        proposed_price += proposed_price * 0.3f;
                    }
                    //preço PAR
                    else
                    {
                        //retorn preço abaixo do dado
                        proposed_price = proposed_price * 0.6f;
                    }
            
                }
                //transporter IMPAR
                else
                {
                    //preço IMPAR
                    if(price % 2 != 0)
                    {
                        //retorna preco abaixo do dado
                        proposed_price = proposed_price * 0.6f;
                    }
                    //preço PAR
                    else
                    {
                        //retorna preco acima do dado
                        proposed_price += proposed_price * 0.3f;
                    }
                }
            
            }
            
            JobView proposed_job = createProposedJob(origin, destination, Math.round(proposed_price));
            
            System.out.println("============================================================================");
            System.out.println("Received job request. Returning job id \"" + proposed_job.getJobIdentifier() + "\" with proposed price " + Math.round(proposed_price) + ".");
            System.out.println("============================================================================");
            
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
           
           int new_id = generateId();
           new_job.setJobIdentifier(_transporterIdPrefix + Integer.toString(new_id));

           //set state to the initial one.
           new_job.setJobState(stringToJobState("PROPOSED"));
           
           _jobs.add(new_job);
           
           return new_job;
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
            
            for(i = 1 ; i<_jobIdList.length; i++)
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
        
        /**
         *  Retrieves job state enumerator value.
         *  
         *  @param state_name name of the enumerator element.
         *  @return corresponding JobStateView enumerator element.
         *  
         *  Possible names are stored in a static array.
         *  @see TransporterPort#JOB_STATUS_LIST
         */
        private JobStateView stringToJobState(String state_name)
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
        
        /**
         *  Receives a job identifier and retrives the last digits.
         *  "T2-33" -> 33
         *  
         *  @param line a string containing a job id.
         *  @return an integer value for the last digits
         */
        private static int getTrailingInt(String line)
        {
            int offset = line.length();
            for (int i = line.length() - 1; i >= 0; i--)
            {
                char c = line.charAt(i);
                if (Character.isDigit(c))
                {
                    offset--;
                }
                else
                {
                    if (offset == line.length())
                    {
                        // No int at the end
                        return Integer.MIN_VALUE;
                    }
                return Integer.parseInt(line.substring(offset));
                }
            }
            
            return Integer.parseInt(line.substring(offset));
        }
        
	
}