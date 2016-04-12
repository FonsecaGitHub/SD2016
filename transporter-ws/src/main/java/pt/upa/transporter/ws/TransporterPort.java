package pt.upa.transporter.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

@WebService(endpointInterface = "pt.upa.transporter.ws.TransporterPortType")
public class TransporterPort implements TransporterPortType {
        
        /**
         * Used to store active jobs.
         * Using a linked list makes element removal more efficient ( complexity O(1)).
         * 
         * @see java.util.LinkedList
         */
        private LinkedList<JobView> _jobs;

        /**
         * Constructor.
         */
        public TransporterPort()
        {
            _jobs = new LinkedList<JobView>();
            
            //valores de exemplo
            JobView job = new JobView();
            job.setCompanyName("TransporterOfDoom");
            
            _jobs.add(job);
            
            job = new JobView();
            job.setCompanyName("TransporterFromHell");
            
            _jobs.add(job);
        }
        
        /**
	 * Deletes all jobs.
	 * 
	 * {@link TransporterPort#_jobs}
	 */
	public void clearJobs(){
	
	}
	
	/**
	 * Returns a list of all jobs stored.
	 * 
	 * {@link TransporterPort#_jobs}
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
	
}
