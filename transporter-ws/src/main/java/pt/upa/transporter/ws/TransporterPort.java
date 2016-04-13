package pt.upa.transporter.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

@WebService(endpointInterface = "pt.upa.transporter.ws.TransporterPortType")
public class TransporterPort implements TransporterPortType {
        
        private static final String[] JOB_STATUS_LIST= {"PROPOSED",
                                                        "REJECTED",
                                                        "ACCEPTED",
                                                        "HEADING",
                                                        "ONGOING",
                                                        "COMPLETED"
                                                       };
        
        private static final int BASE_ID_ARRAY_SIZE = 1024;
        
        /**
         *  These values are used to fill the job ID status list.
         *      @see TransporterPort#_jobIdStatusList
         */
        private static final int ID_TAKEN = 1;
        private static final int ID_AVAILABLE = 0;
        
        
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
         * Used to store active jobs.
         * Using a linked list makes element removal more efficient ( complexity O(1)).
         * 
         * @see java.util.LinkedList
         */
        private LinkedList<JobView> _jobs;

        /**
         *  Array used to check if an id is taken or not.
         *  The index of each position is the ID.
         *  If a position is taken, the ID is in use and can't be assigned to a new job.
         */
        private int[] _jobIdStatusList;
        
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
            _jobIdStatusList = new int[BASE_ID_ARRAY_SIZE];
            
            _transporterName = transporter_name;
            
            //The number of the transport is the last character in the name.
            char number = _transporterName.charAt(_transporterName.length() - 1);
            _transporterNumber = Character.getNumericValue(number);
            
            System.out.println("Number: " + _transporterNumber + " of transport " + _transporterName);
            
            //valores de exemplo
            JobView job = new JobView();
            job.setCompanyName("TransporterOfDoom");
            
            _jobs.add(job);
            
            job = new JobView();
            job.setCompanyName("TransporterFromHell");
            
            _jobs.add(job);
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
	 * [Insert description here]
	 * 
	 * @param origin text description of the origin of job.
	 * @param destination text description of the destination of job. 
	 * @return JobView object of the job.
	 *     @see pt.upa.transporter.ws.JobView
	 *     
	 * @throws pt.upa.transporter.ws.BadJobFault_Exception
	 * @throws pt.upa.transporter.ws.BadPriceFault_Exception
	 */
	public JobView requestJob(String origin, String destination, int price)
        throws BadLocationFault_Exception, BadPriceFault_Exception
	{
            //FIXME:check price>10 && price<100
            
            if(_transporterNumber % 2 == 0)
            {
                if(price % 2 != 0)
                {
                    //retorna preco acima do dado
                }
                else
                {
                    //retorn preço abaixo do dado
                }
            
            } else
            {
                if(price % 2 != 0)
                {
                    //retorna preco abaixo do dado
                }
                else
                {
                    //retorna preco acima do dado
                }
            }
	
            return null;
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
        private void createJob(String company_name, String origin, String destination, int price)
        {  
           JobView new_job = new JobView();
           
           new_job.setCompanyName(company_name);
           new_job.setJobOrigin(origin);
           new_job.setJobDestination(destination);
           new_job.setJobPrice(price);
           
           int new_id = generateId();
           
           new_job.setJobIdentifier(Integer.toString(new_id));
           
           //set state to the initial one.
           new_job.setJobState(getJobState(JOB_STATUS_LIST[0]));
           
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
            
            for(i = 0 ; i<_jobIdStatusList.length; i++)
            {
                if(_jobIdStatusList[i] == ID_AVAILABLE)
                {
                    result = i;
                    break;
                }
            }
            
            return result;
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
        
	
}
