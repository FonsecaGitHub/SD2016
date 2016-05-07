package pt.upa.authserver.ws;

import javax.jws.WebService;

// provides helper methods to print byte[] 
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.parseInteger;

import java.security.PrivateKey;
import java.security.PublicKey;
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
 * Each service may request it's public key certificate to the CA.
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
        private static final String BROKER_KEYSTORE_PASSWORD= "broker_ks";
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
            KeyStore ks = readKeystore(RESOURCES_DIRECTORY_PATH + CA_KEYSTORE_FILENAME, CA_KEYSTORE_PASSWORD);
            
//             getBrokerPublicKey();
//         
//             System.out.println(printHexBinary(getPrivateKeyFromKeystore(ks, CA_KEYPAIR_ALIAS).getEncoded()));
            
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
	/**
	 * Reads a PrivateKey from a key-store
	 * 
	 * @return The private key.
	 * @throws Exception 
	 */
	private static PrivateKey getPrivateKeyFromKeystore(KeyStore keystore, String keyAlias) throws Exception 
	{
		PrivateKey key = (PrivateKey) keystore.getKey(keyAlias, keyAlias.toCharArray());

		return key;
	}
	
	/**
	 * Reads a KeyStore from a file
	 * 
	 * @return The read KeyStore
	 * @throws Exception
	 */
	private static KeyStore readKeystore(String file_path, String keystore_password) throws Exception
	{
            FileInputStream file_instream = null;
            
            try
            {
                file_instream = new FileInputStream(file_path);
            }
            catch(FileNotFoundException excep)
            {
                excep.printStackTrace();
                System.exit(-1);
            }
            
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            
            keystore.load(file_instream, keystore_password.toCharArray());     
	
            return keystore;
	}
    
	/**
	 * Reads a certificate from a file.
	 * 
	 * @param certificate_filepath filepath to the certificate
	 * @return Certificate object or null.
	 * @throws Exception
	 */
	 private static Certificate readCertificate(String certificate_filepath) throws Exception
	 {
            BufferedInputStream buffered_instream = null;
            
            Certificate result = null;
            CertificateFactory cert_factory = CertificateFactory.getInstance("X.509");
	 
            try
            {
                buffered_instream = new BufferedInputStream(new FileInputStream(certificate_filepath));
            }
            catch(FileNotFoundException excep)
            {
                excep.printStackTrace();
                System.exit(-1);
            }
            
            int num_bytes_available_to_be_read = buffered_instream.available();
            
            if(num_bytes_available_to_be_read > 0)
            {
                result = cert_factory.generateCertificate(buffered_instream);
            }
            
            buffered_instream.close();
            
            return result;
	 }
	
	
	private BigInteger getBrokerPublicKey() throws Exception
	{
            Certificate broker_certificate = readCertificate(RESOURCES_DIRECTORY_PATH + BROKER_CERTIFICATE_FILENAME);
            
            PublicKey broker_pkey = broker_certificate.getPublicKey();
            
            System.out.println("Retrieving public key of \"UpaBroker\" service:" );
            System.out.println(printHexBinary(broker_pkey.getEncoded()));
            
            return new BigInteger(printHexBinary(broker_pkey.getEncoded()), 16);
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
            
            Certificate transporter_certificate = readCertificate(RESOURCES_DIRECTORY_PATH + certificate_filename);

            PublicKey transporter_pkey = transporter_certificate.getPublicKey();
    
            System.out.println("Retrieving public key of \"UpaTransporter" + number + "\" service:" );
            System.out.println(printHexBinary(transporter_pkey.getEncoded()));
            
            return new BigInteger(printHexBinary(transporter_pkey.getEncoded()), 16);
	}
	
	
	private BigInteger getCAPublicKey() throws Exception
	{
            Certificate authserver_certificate = readCertificate(RESOURCES_DIRECTORY_PATH + CA_CERTIFICATE_FILENAME );
            
            PublicKey authserver_pkey = authserver_certificate.getPublicKey();
            
            System.out.println("Retrieving public key of \"UpaBroker\" service:" );
            System.out.println(printHexBinary(authserver_pkey.getEncoded()));
            
            return new BigInteger(printHexBinary(authserver_pkey.getEncoded()), 16);
	}

}
