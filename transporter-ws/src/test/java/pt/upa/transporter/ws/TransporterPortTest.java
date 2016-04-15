package pt.upa.transporter.ws;

public class TransporterPortTest {
	protected TransporterPort server;
	
	public TransporterPort populateTest() {
		String transporterName = "transporterTest";
		
		TransporterPort server = new TransporterPort(transporterName);
		server.populate();
		return server;
	}
}
