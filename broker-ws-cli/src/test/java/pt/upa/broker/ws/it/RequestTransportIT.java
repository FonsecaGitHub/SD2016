package pt.upa.broker.ws.it;

import pt.upa.broker.ws.cli.BrokerClient;

import pt.upa.broker.BrokerClientApplication;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *  Integration Test
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 *
 * Before using, set up:
 * - UpaTransporter1
 * - UpaTransporter2
 * - UpaBroker1
 */
public class RequestTransportIT {

    // static members
    private static BrokerClient _client;
    
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        _client = BrokerClientApplication.getBrokerClient();
    }

    @Test
    public void basicSuccessTest() throws Exception
    {   
        String identifier = _client.requestTransport("Guarda","Viseu", 50);
        assertEquals(identifier, "T2-1");
        
        identifier = _client.requestTransport("Guarda","Viseu",49);
        assertEquals(identifier, "T1-2");
    }
    
    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


}