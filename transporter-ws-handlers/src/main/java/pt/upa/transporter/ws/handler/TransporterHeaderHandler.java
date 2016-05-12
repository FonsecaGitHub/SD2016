package pt.upa.transporter.ws.handler;

import pt.upa.authserver.ws.cli.AuthenticationServerClient;

import pt.upa.utils.KeystoreReader;
import pt.upa.utils.CertificateReader;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;

import java.io.File;
import java.io.ByteArrayOutputStream;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.MessageDigest;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import javax.crypto.Cipher;

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
public class TransporterHeaderHandler implements SOAPHandler<SOAPMessageContext> 
{

    public static final String TRANSPORTER_NAME_PROPERTY = "transporter.name.property";
    
    public static final String MESSAGE_ID = "[TransporterHeaderHandler]";
    
    private static final String TRANSPORTER1_NAME = "UpaTransporter1";
    private static final String TRANSPORTER2_NAME = "UpaTransporter2";

    private static final String LOCAL_KEYSTORE_DIRECTORY = "./src/main/resources/";
    private static final String LOCAL_KEYSTORE_NAME = "transporter_keystore.jks";
    private static final String LOCAL_KEYSTORE_PASSWORD = "transporter_ks";
    
    private static final String TRANSPORTER1_KEYPAIR_ALIAS = "transporter1";
    private static final String TRANSPORTER2_KEYPAIR_ALIAS = "transporter2";
    
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
     * Transporters's public keys.
     * This is used to encrypt outgoing messages.
     */
    private PrivateKey _transporter1PrivateKey;
    private PrivateKey _transporter2PrivateKey;
    
    /**
     * Broker's public key.
     * This is used to: fixme
     */
    private PublicKey _brokerPublicKey;
    
    
    /**
     * Constructor. 
     */
    public TransporterHeaderHandler() throws Exception
    {
        print("------------------------------------------------------------------");
    
        print("Setting up authentication server client...");
        
        _authServerClient = AuthenticationServerClient.getAuthenticationServerClient();
        
        _brokerPublicKey = null; //FIXME
        
        if(_authServerClient != null)
            print("Authentication server client set up successfully.");
        else
            print("Unable to set up authentication server client.");
            
        File keystore_file = new File(LOCAL_KEYSTORE_DIRECTORY + LOCAL_KEYSTORE_NAME);    
            
        //set up keystore reader object
        _keyStoreReader = new KeystoreReader(keystore_file , LOCAL_KEYSTORE_PASSWORD);
        _certificateReader = null; //initialize when needed
        
        //get broker's private key
        _transporter1PrivateKey = _keyStoreReader.getPrivateKey(TRANSPORTER1_KEYPAIR_ALIAS);
        _transporter2PrivateKey = _keyStoreReader.getPrivateKey(TRANSPORTER2_KEYPAIR_ALIAS);
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
        print("Transporter's certificate has not been requested yet.");
        print("Requesting transporters's certificate to the authentication server...");
        byte[] certificate_bytes = _authServerClient.getCertificate(TRANSPORTER1_NAME);
        
        if(certificate_bytes != null && certificate_bytes.length >= 1)
        {
            print("Transporter's certificate obtained successfully");
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
                
        String propertyValue = (String) smc.get(TRANSPORTER_NAME_PROPERTY);
        
                
        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) 
            {
                print(TRANSPORTER_NAME_PROPERTY + " is set to \"" + propertyValue + "\".");
                print("Writing header in outbound SOAP message...");
                try
                {
                    if(_certificateReader == null)
                        requestPublicKeyCertificate();
                    
//                     appendIdentityToOutboundMsg(smc);
//                     byte[] ciphered_digest = generateCipheredMessageDigest(smc);
//                     appendCipheredDigestToOutboundMsg(smc, ciphered_digest);
//                     appendCertificateToOutboundMsg(smc);
                }
                catch(Exception excep)
                {
                    excep.printStackTrace();
                    handleFault(smc);
                }
                
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
//                 smc.put(CONTEXT_PROPERTY, value);
                // set property scope to application client/server class can access it
//                 smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);

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
    
    /**
     * Append the transporters's identity to an outbound message.
     */ 
    private void appendIdentityToOutboundMsg(SOAPMessageContext smc) throws Exception
    {
        // get SOAP envelope
        SOAPMessage msg = smc.getMessage();
        
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();

        // add header
        SOAPHeader sh = se.getHeader();
        if (sh == null)
            sh = se.addHeader();

        // add header element (name, namespace prefix, namespace)
        Name name = se.createName("senderName", "broker-ws", "http://localhost:8091/broker-ws/endpoint");
        SOAPHeaderElement element = sh.addHeaderElement(name);

        // add header element value
        String valueString = TRANSPORTER1_NAME;
        element.addTextNode(valueString);
    }
//     
//     /**
//      * Generates a digest of an outbound message and ciphers it with brokers private key.
//      */
//     private byte[] generateCipheredMessageDigest(SOAPMessageContext smc) throws Exception
//     {
//         ByteArrayOutputStream msg_out = new ByteArrayOutputStream();
//     
//         SOAPMessage msg = smc.getMessage();
//     
//         msg.writeTo(msg_out);
//         byte[] msg_bytes = msg_out.toByteArray();
//         
//         MessageDigest digest = MessageDigest.getInstance("SHA-1");
//         
//         digest.update(msg_bytes);
//         byte[] digest_bytes = digest.digest();
//         
//         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//         
//         cipher.init(Cipher.ENCRYPT_MODE, _brokerPrivateKey);
//         
//         byte[] ciphered_digest = cipher.doFinal(digest_bytes);
//         
//         return ciphered_digest;
//     }
//     
//     /**
//      * Appends broker's certificate to an outbount message.
//      */
//      private void appendCertificateToOutboundMsg(SOAPMessageContext smc) throws Exception
//      {
//         SOAPMessage msg = smc.getMessage();
//         
//         SOAPPart msg_soap_part = msg.getSOAPPart();
//         SOAPEnvelope msg_soap_envelope = msg_soap_part.getEnvelope();
// 
//         // add header
//         SOAPHeader msg_soap_header = msg_soap_envelope.getHeader();
//         if (msg_soap_header == null)
//             msg_soap_header = msg_soap_envelope.addHeader();
// 
//         // add header element (name, namespace prefix, namespace)
//         Name name = msg_soap_envelope.createName("certificateBytes", "broker-ws", "http://localhost:8091/broker-ws/endpoint");
//         SOAPHeaderElement element = msg_soap_header.addHeaderElement(name);
// 
//         // add header element value
//         String valueString = printHexBinary(_certificateReader.getCertificateBytes());
//         element.addTextNode(valueString);
//      }
//     
//     /**
//      * Appends the ciphered message digest to the SOAP message as a new header element.
//      */
//     private void appendCipheredDigestToOutboundMsg(SOAPMessageContext smc, byte[] ciphered_digest) throws Exception
//     {
//         // get SOAP envelope
//         SOAPMessage msg = smc.getMessage();
//         
//         SOAPPart msg_soap_part = msg.getSOAPPart();
//         SOAPEnvelope msg_soap_envelope = msg_soap_part.getEnvelope();
// 
//         // add header
//         SOAPHeader msg_soap_header = msg_soap_envelope.getHeader();
//         if (msg_soap_header == null)
//             msg_soap_header = msg_soap_envelope.addHeader();
// 
//         // add header element (name, namespace prefix, namespace)
//         Name name = msg_soap_envelope.createName("cipheredDigest", "broker-ws", "http://localhost:8091/broker-ws/endpoint");
//         SOAPHeaderElement element = msg_soap_header.addHeaderElement(name);
// 
//         // add header element value
//         String valueString = printHexBinary(ciphered_digest);
//         element.addTextNode(valueString);
//     }

    
    public void close(MessageContext messageContext) 
    {
    }

}