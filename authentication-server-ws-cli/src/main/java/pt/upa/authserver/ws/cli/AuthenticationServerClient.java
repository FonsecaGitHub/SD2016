package pt.upa.authserver.ws.cli;

import pt.upa.authserver.ws.AuthenticationServerPort;
import pt.upa.authserver.ws.AuthenticationServerService;
import pt.upa.authserver.ws.AuthenticationServerPortType;

//uddi naming
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

//javax.xml.ws
import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;

import java.util.Map;

import java.math.BigInteger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AuthenticationServerClient
{
    private static final String WS_NAME = "AuthenticationServer";
    private static final String UDDI_URL = "http://localhost:9090";
    
    private static final String TRANSPORTER1_NAME = "UpaTransporter1";
    private static final String TRANSPORTER2_NAME = "UpaTransporter2";
    
    private AuthenticationServerPortType _port;
    
    /**
     * Private constructor.
     * Class instance can be obtained only through getAuthenticationServerClient().
     * 
     * @param port 
     */
    private AuthenticationServerClient(AuthenticationServerPortType port)
    {
        _port = port;
    }
    
    public static AuthenticationServerClient getAuthenticationServerClient() throws Exception
    {
//         System.out.println("=======================================================================================");
        System.out.println("Retrieving Athentication server client...");
        String uddiURL = UDDI_URL; 
        String name = WS_NAME;
	
	System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);

        if (endpointAddress == null) {
            System.out.println("Unable to get endpoint address of service\"" + name + "\" at \"" + uddiURL + "\"");
            return null;
        } else {
            System.out.println("Found endpoint address \"" + endpointAddress + "\" for name \"" + name + "\".");
            }

        System.out.println("Creating stub ...");
        AuthenticationServerService service = new AuthenticationServerService();
        AuthenticationServerPortType port = service.getAuthenticationServerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

        AuthenticationServerClient client = new AuthenticationServerClient(port);
		
//         System.out.println("=======================================================================================");
                
        return client;
    }
    
    public Certificate getCertificate(String name) throws CertificateException, IOException
    {
        byte[] certificate_bytes = _port.requestCertificate(name);
        
        Certificate result = null;
        CertificateFactory cert_factory = CertificateFactory.getInstance("X.509");
        
        BufferedInputStream bytes_instream = new BufferedInputStream(new ByteArrayInputStream(certificate_bytes));
        
        int num_bytes_available_to_be_read = bytes_instream.available();
            
        if(num_bytes_available_to_be_read > 0)
        {
            result = cert_factory.generateCertificate(bytes_instream);
        }
        
        bytes_instream.close();
        
        return result;
    }
    
    public BigInteger getBrokerPublicKey()
    {
        return _port.requestBrokerPublicKey();
    }
    
    public BigInteger getTransporterPublicKey(String name)
    {
        int transporter_number;
    
        if(name.equals(TRANSPORTER1_NAME))
            transporter_number = 1;
        else if(name.equals(TRANSPORTER2_NAME))
            transporter_number = 2;
        else 
            return null;
        
        BigInteger result = _port.requestTransporterPublicKey(transporter_number);
        
        return result;
//         return _port.requestTransporterPublicKey(name);
    }
    
}