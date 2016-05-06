package pt.upa.broker.ws.cli;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;

import java.util.List;

public class BrokerClient
{

        BrokerPortType _broker;
        
        public BrokerClient(BrokerPortType broker)
        {
            _broker = broker;
        }

	public void clearTransports() {
            _broker.clearTransports();
	}

	public List<TransportView> listTransports() {
		return _broker.listTransports();
	}

	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
                
               return _broker.viewTransport(id);
	}

	public String requestTransport(String origin, String destination, int price)
		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
                
                return _broker.requestTransport(origin, destination, price);
	}

	public String ping(String name) {
		return _broker.ping(name);
	}
}
