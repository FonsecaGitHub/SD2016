package pt.upa.authserver.ws.it;

import pt.upa.authserver.ws.cli.AuthenticationServerClient;

import org.junit.*;
import static org.junit.Assert.*;

import java.math.BigInteger;

/**
 *  Authentication Server integration Tests.
 */
public class BasicIT {

    // static members
    private static final String BROKER_NAME = "UpaBroker";
    private static final String TRANSPORTER1_NAME = "UpaTransporter1";
    private static final String TRANSPORTER2_NAME = "UpaTransporter2";
    
    private static AuthenticationServerClient _client;
    
    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception 
    {
        _client = AuthenticationServerClient.getAuthenticationServerClient();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        
    }
    
    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // members
    
    //tests
    @Test
    public void basicSuccess() throws Exception
    {
        assertNotNull(_client);
        assertNotNull(_client.getBrokerPublicKey());
        assertNotNull(_client.getTransporterPublicKey(TRANSPORTER1_NAME));
        assertNotNull(_client.getTransporterPublicKey(TRANSPORTER2_NAME));
        assertNotNull(_client.getCertificate(BROKER_NAME));
    }
    
    @Test
    public void successGetBrokerPublicKey()
    {
        BigInteger first_key = _client.getBrokerPublicKey();
        BigInteger second_key = _client.getBrokerPublicKey();
    
        assertTrue(first_key.toString(16).length() > 10);
        assertEquals(0, first_key.compareTo(second_key));
    }
    
    @Test
    public void successGetTransporterPublicKey()
    {
        BigInteger first_key = _client.getTransporterPublicKey(TRANSPORTER1_NAME);
        BigInteger second_key = _client.getTransporterPublicKey(TRANSPORTER1_NAME);
        
        assertTrue(first_key.toString().length() > 10);
        assertEquals(0, first_key.compareTo(second_key));
    }
    
    @Test
    public void failureGetTransporterPublicKey()
    {
        BigInteger first_key = _client.getTransporterPublicKey("UpaTransporter3");
        
        assertNull(first_key);
    }
    
    @Test
    public void failureGetTransporterPublicKeyCompareKeys()
    {
        BigInteger first_key = _client.getTransporterPublicKey(TRANSPORTER1_NAME);
        BigInteger second_key = _client.getTransporterPublicKey(TRANSPORTER2_NAME);
        
        assertNotEquals(0, first_key.compareTo(second_key));
    }



}