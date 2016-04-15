package pt.upa.broker.ws;

public class BrokerPortTest {
	protected BrokerPort server;
	
	public BrokerPort populateTest() {
		BrokerPort server = new BrokerPort();
		server.populate();
		return server;
	}
}
