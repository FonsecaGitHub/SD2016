package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class DecideJobTest extends TransporterPortTest {

    // static members
	private static final String INVAVLID_JOB_ID = "blob";
	
	//Job ids sao do tipo TN-X, N e o numero da transportadora, X e um numero unico.
	//Ou seja aqui tem de haver uma transportadora 2 com um job com este id (T2-2)
	private static final String VALID_JOB_ID = "T2-2";

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

//     @Test
//     public void sucess() throws BadJobFault_Exception {
//     	server.decideJob(VALID_JOB_ID, true);
//     }
    
    @Test(expected = BadJobFault_Exception.class)
    public void invalidJobId() throws BadJobFault_Exception {
    	server.decideJob(INVAVLID_JOB_ID, true);
    }
    
    @Test(expected = BadJobFault_Exception.class)
    public void nullJobId() throws BadJobFault_Exception {
    	server.decideJob(null, true);
    }

}