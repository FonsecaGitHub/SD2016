package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class RequestJobTest extends TransporterPortTest{

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
    public void sucess() throws BadLocationFault_Exception, BadPriceFault_Exception { 
    	server.requestJob( KNOWN_ORIGIN, KNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void originDoesNotExist() throws BadLocationFault_Exception, BadPriceFault_Exception {
    	server.requestJob(UNKNOWN_ORIGIN, KNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void destinationDoesNotExist() throws BadLocationFault_Exception, BadPriceFault_Exception {
    	server.requestJob(KNOWN_ORIGIN, UNKNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = BadPriceFault_Exception.class)
    public void invalidPrice() throws BadLocationFault_Exception, BadPriceFault_Exception  {
    	server.requestJob(KNOWN_ORIGIN, KNOWN_DESTINATION, INVALID_PRICE);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void nullOrigin() throws BadLocationFault_Exception, BadPriceFault_Exception  {
    	server.requestJob(null, KNOWN_DESTINATION, VALID_PRICE);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void nullDestination() throws BadLocationFault_Exception, BadPriceFault_Exception  {
    	server.requestJob(KNOWN_ORIGIN, null, VALID_PRICE);
    }   

}