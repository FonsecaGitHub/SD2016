package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class ViewTransportTest extends BrokerPortTest {

    // static members
	private static final String UNKNOWN_TRANSPORT = "blebleble";
	
	//TODO verificar se o transporte e valido
	private static final String KNOWN_TRANSPORT = "1";

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
    public void sucess() throws UnknownTransportFault_Exception {
    	server.viewTransport(KNOWN_TRANSPORT);
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void unknownTransport() throws UnknownTransportFault_Exception {
    	server.viewTransport(UNKNOWN_TRANSPORT);
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void nullTransport() throws UnknownTransportFault_Exception {
    	server.viewTransport(null);
    } 

}