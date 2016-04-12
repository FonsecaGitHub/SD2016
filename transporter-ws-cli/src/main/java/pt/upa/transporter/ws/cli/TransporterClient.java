package pt.upa.transporter.ws.cli;

import java.util.List;

import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;

//NOTA: e suposto usar estas classes para chamar os metodos remotos.
//usei os prototipos dos metodos como estao no TransporterPort, mas e suposto alterar isso.
public class TransporterClient
{
        private String _name; //i.e "UpaTransporter1"
        private TransporterPortType _port;
        
        public TransporterClient(String name, TransporterPortType port){
            _port = port;
            _name = name;
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
	
	public JobView jobStatus(String id){
            
            return null;
	}
	
	public JobView decideJob(String id,boolean accept) throws BadJobFault_Exception
	{
            return null;
	}
	
        public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception
	{
//             if()
	
            return null;
	}

}
