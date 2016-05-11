package pt.upa.broker.ws.handler;

import pt.upa.authserver.ws.cli.AuthenticationServerClient;

import pt.upa.utils.KeystoreReader;
import pt.upa.utils.CertificateReader;

import java.io.File;

import java.security.PrivateKey;

import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *  This SOAPHandler shows how to set/get values from headers in
 *  inbound/outbound SOAP messages.
 *
 *  A header is created in an outbound message and is read on an
 *  inbound message.
 *
 *  The value that is read from the header
 *  is placed in a SOAP message context property
 *  that can be accessed by other handlers or by the application.
 */
public class BrokerHeaderHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String CONTEXT_PROPERTY = "my.property";
    public static final String MESSAGE_ID = "[BrokerHeaderHandler]";
    
    private static final String BROKER_NAME = "UpaBroker";
    
    private static final String LOCAL_KEYSTORE_DIRECTORY = "./src/main/resources/";
    private static final String LOCAL_KEYSTORE_NAME = "broker_keystore.jks";
    private static final String LOCAL_KEYSTORE_PASSWORD = "broker_ks";
    
    private static final String BROKER_KEYPAIR_ALIAS = "broker";
    
    /**
     * Authentication Server Client.
     * Used as an interface of communication between this and the authentication server.
     */
    private AuthenticationServerClient _authServerClient;
    
    /**
     * Keystore reader.
     * Used to read private keys from a key store.
     */
    private KeystoreReader _keyStoreReader;
    
    /**
     * Certificate reader.
     * Used to read public keys from a certificate.
     */
    private CertificateReader _certificateReader;
    
    /**
     * Broker's private key.
     * This is used to sign the message digest before sending.
     */
    private PrivateKey _brokerPrivateKey;
    
    /**
     * Constructor. 
     */
    public BrokerHeaderHandler() throws Exception
    {
        print("------------------------------------------------------------------");
    
        print("Setting up authentication server client...");
        
        _authServerClient = AuthenticationServerClient.getAuthenticationServerClient();
        
        
        if(_authServerClient != null)
            print("Authentication server client set up successfully.");
        else
            print("Unable to set up authentication server client.");
            
        File keystore_file = new File(LOCAL_KEYSTORE_DIRECTORY + LOCAL_KEYSTORE_NAME);    
            
        //set up keystore reader object
        _keyStoreReader = new KeystoreReader(keystore_file , LOCAL_KEYSTORE_PASSWORD);
        _certificateReader = null; //initialize when needed
        
        //get broker's private key
        _brokerPrivateKey = _keyStoreReader.getPrivateKey(BROKER_KEYPAIR_ALIAS);
    }
    
    /**
     * Print message with handler ID. 
     */
    private void print(String message)
    {
        System.out.println(MESSAGE_ID + " " + message);
    }
    
    /**
     * Request public key certificate from auth.server.
     */
    private void requestPublicKeyCertificate() throws Exception
    {
        print(":::::::::::::::::::::::::::::::::::::::::::::::::");
        print("Broker's certificate has not been requested yet.");
        print("Requesting broker's certificate to the authentication server...");
        byte[] certificate_bytes = _authServerClient.getCertificate(BROKER_NAME);
        
        if(certificate_bytes != null && certificate_bytes.length >= 1)
        {
            print("Broker's certificate obtained successfully");
            _certificateReader = new CertificateReader(certificate_bytes);
        }
        else
            print("Ups, something went terribly wrong... received byte array is invalid (null or empty)");
        
        print(":::::::::::::::::::::::::::::::::::::::::::::::::");
        return;    
    }
    
    //
    // Handler interface methods
    //
    public Set<QName> getHeaders() {
        return null;
    }

    /**
     * Handle message.
     * If message is outbound, add elements to header (signature, encrypted message digest, etc..)
     * If message is inbound, verify headers.
     */
    public boolean handleMessage(SOAPMessageContext smc)
    {
        System.out.println("--------------------------------------------------------------------------------------");
        print("Handling message...");

        try
        {
            if(_certificateReader == null)
                requestPublicKeyCertificate();
        }
        catch(Exception excep)
        {
            excep.printStackTrace();
            handleFault(smc);
        }
                
        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) 
            {
//                 System.out.println("Writing header in outbound SOAP message...");

                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                // add header element (name, namespace prefix, namespace)
                Name name = se.createName("myHeader", "d", "http://demo");
                SOAPHeaderElement element = sh.addHeaderElement(name);

                // add header element value
                int value = 22;
                String valueString = Integer.toString(value);
                element.addTextNode(valueString);

            } 
            else {
//                 System.out.println("Reading header in inbound SOAP message...");

                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
//                     System.out.println("Header not found.");
                    return true;
                }

                // get first header element
                Name name = se.createName("myHeader", "d", "http://demo");
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
//                     System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();
                int value = Integer.parseInt(valueString);

                // print received header
//                 System.out.println("Header value is " + value);

                // put header in a property context
                smc.put(CONTEXT_PROPERTY, value);
                // set property scope to application client/server class can access it
                smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);

            }
        } catch (Exception e) {
//             System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
//             System.out.println("Continue normal processing...");
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        print("Ignoring fault message...");
        return true;
    }

    public void close(MessageContext messageContext) {
    }

}