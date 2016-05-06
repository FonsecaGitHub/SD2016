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

import java.util.Map;

import java.math.BigInteger;

public class AuthenticationServerClient
{
    private static final String WS_NAME = "AuthenticationServer";
    private static final String UDDI_URL = "http://localhost:9090";

    private AuthenticationServerPortType _port;
    
    public static AuthenticationServerClient getAuthenticationServerClient() throws Exception
    {
        System.out.println("=======================================================================================");
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
		
        System.out.println("=======================================================================================");
                
        return client;
    }
    
    private AuthenticationServerClient(AuthenticationServerPortType port)
    {
        _port = port;
    }
    
    public Certificate getCertificate(String name)
    {
        //todo
        return null;
    }
    
    public BigInteger getPublicKey(String name)
    {
        //todo
        return null;
    }
    
}