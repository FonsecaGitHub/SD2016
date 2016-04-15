package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class RequestTransportTest extends BrokerPortTest {

    // static members
	private static final int INVALID_PRICE = -5;
	private static final String UNKNOWN_ORIGIN = "Rapture";
	private static final String UNKNOWN_DESTINATION = "RaccoonCity";
	
	private static final int VALID_PRICE = 5;
	private static final String KNOWN_ORIGIN = "Lisboa";
	private static final String KNOWN_DESTINATION = "Guarda";

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	server = super.populateTest();
    }

    @After
    public void tearDown() {
    	server = null;
    }


    // tests

    @Test
    public void sucess() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception { 
    	server.requestTransport( KNOWN_ORIGIN, KNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void originDoesNotExist() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	server.requestTransport(UNKNOWN_ORIGIN, KNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void destinationDoesNotExist() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	server.requestTransport(KNOWN_ORIGIN, UNKNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = InvalidPriceFault_Exception.class)
    public void invalidPrice() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	server.requestTransport(KNOWN_ORIGIN, KNOWN_DESTINATION, INVALID_PRICE);
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void nullOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	server.requestTransport(null, KNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void nullDestination() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	server.requestTransport(KNOWN_ORIGIN, null, VALID_PRICE);
    }    

}