package pt.upa.broker.ws;

import javax.jws.WebService;

import java.util.List;

@WebService(endpointInterface = "pt-upa.broker.ws.BrokerPortType")
public class BrokerPort implements BrokerPortType {

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
