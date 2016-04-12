package pt.upa.broker.ws;

import javax.jws.WebService;

//pt.upa.transporter.ws
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.List;
import java.util.LinkedList;

@WebService(endpointInterface = "pt.upa.broker.ws.BrokerPortType")
public class BrokerPort implements BrokerPortType {

        List<TransporterClient> _transporters;
        
        public BrokerPort()
        {
            _transporters = new LinkedList<TransporterClient>();
        }
        //======= Metodos genericos. ==================================================
        public void addTransporter()
        {
            
        }
        
        

        //==============================================================================
	//======= Invocaçoes remotas ===================================================
	
	public void clearTransports() {

	}

	public List<TransportView> listTransports() {
		return null;
	}

	public TransportView viewTransport(String id)
		throws UnknownTransportFault_Exception {
			return null;
	}

	public String requestTransport(String origin, String destination, int price)
		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
			return null;
	}

	public String ping(String name) {
		return "<<< Pinged by \"" + name + "\"! >>>";	
	}
}
