package pt.upa.broker;

//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

//javax.xml.ws
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

//pt.upa.broker.ws
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.BrokerPortType;

//pt.upa.broker.ws.cli
import pt.upa.broker.ws.cli.BrokerClient;

import java.util.Map;

public class BrokerClientApplication {

	private static final String UDDI_URL = "http://localhost:9090";
	private static final String WS_NAME = "UpaBroker1";

	public static void main(String[] args) throws Exception {
		System.out.println("=======================================================================================");
        System.out.println("========================== Starting up Broker client... ==========================");

        //Check Arguments
	}

}
