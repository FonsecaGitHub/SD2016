package pt.upa.transporter.ws.cli;

import java.util.List;

import java.lang.StringBuilder;

import pt.upa.authserver.ws.cli.AuthenticationServerClient;

import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;

public class TransporterClient
{
        private String _name; //i.e "UpaTransporter1"
        private TransporterPortType _port;
        private AuthenticationServerClient _authServerClient;
        
        public TransporterClient(String name, TransporterPortType port){
            _port = port;
            _name = name;
            try
            {
                _authServerClient = AuthenticationServerClient.getAuthenticationServerClient();
            }
            catch(Exception excep)
            {
                excep.printStackTrace();
                return;
            }
        }
        
        public String getName()
        {
            return _name;
        }
        
        public String ping(String name)
        {
            return _port.ping(name);
        }
        
        public void clearJobs(){
            _port.clearJobs();
	}
	
	public List<JobView> listJobs(){
            
            return null;
	}
	
	public String jobStatus(String id){
            
            JobView requested_job = _port.jobStatus(id);
            
            if(requested_job == null)
                return null;
            
            return requested_job.getJobState().value();
	}
	
	
	public int decideJob(String id,boolean accept)
	{
            JobView job_response = null;
	
            try {
                job_response = _port.decideJob(id,accept);
            } 
            catch(BadJobFault_Exception excep)
            {
                return -1;
            }
            
            if(job_response.getJobState().value().equals("ACCEPTED"))
            {
                return 0;
            }
            else
            {
                return 1;
            }
            
	}

	/**
	 * Requests transport from the transporter.
	 * 
	 * @return the price proposed by the transporter. 
	 * @param proposed_job_identifier will be altered to contain the id of the job
	 */
        public int requestJob(String origin, String destination, int price, StringBuilder proposed_job_identifier) 
	{
            
            JobView proposal;
            int proposed_price;
            
            try {
                proposal = _port.requestJob(origin, destination, price);
                
                if(proposal == null)
                {  
                    //proposta rejeitada
                    proposed_price = -1;
                    return proposed_price;
                }
                else
                {
                    proposed_job_identifier.append(proposal.getJobIdentifier());
                    
                    proposed_price = proposal.getJobPrice();
                }
                
                return proposed_price;
            }
            catch(BadLocationFault_Exception excep)
            {
                //nao existe transporte
                return -2;
            }
            catch(BadPriceFault_Exception excep)
            {
                //preço menor que zero
                return -3;
            }
            
            
	}

}
