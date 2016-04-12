package pt.upa.transporter.ws;

import javax.jws.WebService;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

@WebService(endpointInterface = "pt.upa.transporter.ws.TransporterPortType")
public class TransporterPort implements TransporterPortType {
        
        //Lista de jobs.
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
        
        //limpar todos os jobs.
	public void clearJobs(){
	
	}
	
	//Copia lista para ArrayList antes de enviar.
	public List<JobView> listJobs(){
            List<JobView> list = new ArrayList<JobView>();
            list.addAll(_jobs);
	
            return list;
	}
	
	//retornar status do job
	public JobView jobStatus(String id){
            
            return null;
	}
	
	public JobView decideJob(String id,boolean accept) throws BadJobFault_Exception
	{
            return null;
	}
	
	public JobView requestJob(String origin, String destination, int price)
        throws BadLocationFault_Exception, BadPriceFault_Exception
	{
            return null;
	}
	
        public String ping(String name){
        
            return "<<< Pinged by \"" + name + "\"! >>>";
        }
	
}
