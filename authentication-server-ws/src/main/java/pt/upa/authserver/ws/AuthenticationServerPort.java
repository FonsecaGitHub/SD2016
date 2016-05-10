package pt.upa.authserver.ws;

import pt.upa.utils.KeystoreReader;
import pt.upa.utils.CertificateReader;

import javax.jws.WebService;

// provides helper methods to print byte[] 
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.parseInteger;

import java.security.PrivateKey;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;

import java.math.BigInteger;

import java.util.Arrays;

/**
 * WebService that generates and distributes assymetric keys (public-private key pairs) to the broker 
 * and transporters.
 * Also gives public-key certificates to services that request it.
 *
 * ===============================================================
 * Key pair and keystore generation commands:
 * -----------------------------------------------------
 * [$] keytool -genkeypair -alias "authserver" -keyalg RSA -keysize 2048 -keypass "authserver" -validity 90 -storepass "authserver_ks" -keystore authserver_keystore.jks -dname "CN=Henrical, OU=DEI, O=IST, L=Lisbon, S=Lisbon, C=PT"
 *
 * [$] keytool -genkeypair -alias "broker" -keyalg RSA -keysize 2048 -keypass "broker" -validity 90 -storepass "broker_ks" -keystore broker_keystore.jks -dname "CN=Henrical, OU=DEI, O=IST, L=Lisbon, S=Lisbon, C=PT"
 *
 * [$] keytool -genkeypair -alias "transporter1" -keyalg RSA -keysize 2048 -keypass "transporter1" -validity 90 -storepass "transporter_ks" -keystore transporter_keystore.jks -dname "CN=Henrical, OU=DEI, O=IST, L=Lisbon, S=Lisbon, C=PT"
 *
 * [$] keytool -genkeypair -alias "transporter2" -keyalg RSA -keysize 2048 -keypass "transporter2" -validity 90 -storepass "transporter_ks" -keystore transporter_keystore.jks -dname "CN=Henrical, OU=DEI, O=IST, L=Lisbon, S=Lisbon, C=PT"
 * ===============================================================
 *
 * This generates three keystore: one for the CA (this service), one for the broker service, and one for the transporter service.
 * 
 * The first two keystore contain a single public-private keypair. The transporter keystore contains two pairs, one for each expected service.
 * 
 * The CA has access to all keystore and key pairs within them. The other services have access to their own keystore and may request public keys of other services from the CA.
 * 
 * ==============================================================
 * Generation of public key certificates for all participants:
 * -----------------------------------------------------
 * [$] keytool -export -keystore authserver_keystore.jks -alias CA -storepass "authserver_ks" -file authserver.cer
 * [$] keytool -export -keystore broker_keystore.jks -alias broker -storepass "broker_ks" -file broker.cer
 * [$] keytool -export -keystore transporter_keystore.jks -alias transporter1 -storepass "transporter_ks" -file transporter1.cer
 * [$] keytool -export -keystore transporter_keystore.jks -alias transporter2 -storepass "transporter_ks" -file transporter2.cer
 * ==============================================================
 * 
 * This generates a public certificate for each of the four participants.
 * 
 * Each service may request its public key certificate to the CA.
 */
@WebService(endpointInterface = "pt.upa.authserver.ws.AuthenticationServerPortType")
public class AuthenticationServerPort implements AuthenticationServerPortType
{
        private static final String CERTIFICATE_TYPE = "X.509";

        private static final String RESOURCES_DIRECTORY_PATH = "./src/main/resources/";
        
        private static final String CA_KEYSTORE_FILENAME = "authserver_keystore.jks";
        private static final String CA_KEYSTORE_PASSWORD = "authserver_ks";
        private static final String CA_KEYPAIR_ALIAS = "authserver";
        
        private static final String BROKER_KEYSTORE_FILENAME = "broker_keystore.jks";
        private static final String BROKER_KEYSTORE_PASSWORD = "broker_ks";
        private static final String BROKER_KEYPAIR_ALIAS = "broker";
        private static final String BROKER_NAME = "UpaBroker";
        
        private static final String TRANSPORTER_KEYSTORE_FILENAME = "transporter_keystore.jks";
        private static final String TRANSPORTER_KEYSTORE_PASSWORD= "transporter_ks";
        
        private static final String TRANSPORTER1_KEYPAIR_ALIAS = "transporter1";
        private static final String TRANSPORTER1_NAME = "UpaTransporter1";
        
        private static final String TRANSPORTER2_KEYPAIR_ALIAS = "transporter2";
        private static final String TRANSPORTER2_NAME = "UpaTransporter2";
        
        private static final String CA_CERTIFICATE_FILENAME = "authserver.cer";
        private static final String BROKER_CERTIFICATE_FILENAME = "broker.cer";
        private static final String TRANSPORTER2_CERTIFICATE_FILENAME = "transporter1.cer";
        private static final String TRANSPORTER1_CERTIFICATE_FILENAME = "transporter2.cer";
        
        
        public AuthenticationServerPort() throws Exception
        {   
        
//             KeystoreReader ks_reader = new KeystoreReader(RESOURCES_DIRECTORY_PATH + BROKER_KEYSTORE_FILENAME, BROKER_KEYSTORE_PASSWORD);
//             
//             System.out.println(ks_reader.getPrivateKey(BROKER_KEYPAIR_ALIAS));
            
            if(requestCertificate("UpaBroker").length > 10)
                System.out.println(true);
                
            if(requestCertificate("UpaTransporter1").length > 10)
                System.out.println(true);
                
            if(requestCertificate("UpaTrorter1") == null)
                System.out.println(true);
        }
        
        
        /**
         * WEBMETHOD: requestBrokerPublicKey
         *
         * @return BigInteger object containing broker's key.
         */
	public BigInteger requestBrokerPublicKey() 
	{
            try
            {
                return getBrokerPublicKey();
            }
            catch(Exception excep)
            {
                excep.printStackTrace();
                return null;
            }
	}
	
	
        /**
         * WEBMETHOD: requestTransporterPublicKey
         *
         * @param transporterNumber number of the transporter (1 or 2).
         * @return BigInteger object containing the transporter's key.
         */
	public BigInteger requestTransporterPublicKey(Integer transporterNumber)
	{
            System.out.println("Requested pkey of transp. number: " + transporterNumber);
	
            try
            {
                return getTransporterPublicKey(transporterNumber);
            }
            catch(Exception excep)
            {
                excep.printStackTrace();
                return null;
            }
	}
	
        
        /**
         * WEBMETHOD: requestCertificate
         *
         */
	public byte[] requestCertificate(String name)
	{
            Path certificate_path;
            
            switch(name)
            {
                case(BROKER_NAME):
                    certificate_path = Paths.get(RESOURCES_DIRECTORY_PATH + BROKER_CERTIFICATE_FILENAME);
                break;
                case(TRANSPORTER1_NAME):
                    certificate_path = Paths.get(RESOURCES_DIRECTORY_PATH + TRANSPORTER1_CERTIFICATE_FILENAME);
                break;
                case(TRANSPORTER2_NAME):
                    certificate_path = Paths.get(RESOURCES_DIRECTORY_PATH + TRANSPORTER2_CERTIFICATE_FILENAME);
                break;
                default:
                    return null;
            }
            
            byte[] result;
            
            try
            {
                result = Files.readAllBytes(certificate_path);
            }
            catch(IOException excep)
            {
                excep.printStackTrace();
                return null;
            }

            return result;
	}  
	
	//=============== PRIVATE METHODS =====================================================
	
	
	private BigInteger getBrokerPublicKey() throws Exception
	{
            CertificateReader cert_reader = new CertificateReader(RESOURCES_DIRECTORY_PATH + BROKER_CERTIFICATE_FILENAME);
            
            BigInteger key = cert_reader.getPublicKeyEncoded();
            
            System.out.println("Retrieving public key of \"UpaBroker\" service:" );
            System.out.println(key);
            
            return key;
	}
	
	
	private BigInteger getTransporterPublicKey(int number) throws Exception
	{
            String certificate_filename;
            System.out.println("Requested pkey of transp. number: " + number);
            if(number == 1)
            {
                certificate_filename = TRANSPORTER1_CERTIFICATE_FILENAME;
            }
            else if(number == 2)
            {
                certificate_filename = TRANSPORTER2_CERTIFICATE_FILENAME;
            }
            else
            {
                System.out.println("returning null");
                return null;
            }
            
            CertificateReader cert_reader = new CertificateReader(RESOURCES_DIRECTORY_PATH + certificate_filename);
        
            BigInteger key = cert_reader.getPublicKeyEncoded();
    
            System.out.println("Retrieving public key of \"UpaTransporter" + number + "\" service:" );
            System.out.println(key);
            
            return key;
	}
	
	
	private BigInteger getCAPublicKey() throws Exception
	{
            CertificateReader cert_reader = new CertificateReader(RESOURCES_DIRECTORY_PATH + CA_CERTIFICATE_FILENAME );
            
            BigInteger key = cert_reader.getPublicKeyEncoded();
            
            System.out.println("Retrieving public key of \"UpaBroker\" service:" );
            System.out.println(key);
            
            return key;
	}

}
