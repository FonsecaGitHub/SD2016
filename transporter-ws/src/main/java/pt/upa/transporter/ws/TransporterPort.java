package pt.upa.transporter.ws;

import javax.jws.WebService;

import java.util.List;

@WebService(endpointInterface = "pt.upa.transporter.ws.TransporterPortType")
public class TransporterPort implements TransporterPortType {

	// TODO

	public void clearJobs(){
	
	}
	
	public List<JobView> listJobs(){
	
            return null;
	}
	
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
        
            return null;
        }
	
}
