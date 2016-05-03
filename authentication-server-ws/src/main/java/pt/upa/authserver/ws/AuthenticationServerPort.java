package pt.upa.authserver.ws;

import javax.jws.WebService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@WebService(endpointInterface = "pt.upa.authserver.ws.AuthenticationServerPortType")
public class AuthenticationServerPort implements AuthenticationServerPortType
{
        private final static int KEY_SIZE = 1024;
        
        private final static String KEYGEN_ALG = "RSA";
        
        private static final String KEYS_FILE_PATH = "./src/main/resources/keys.txt"; 
        
        public AuthenticationServerPort()
        {
        }
        
        /**
         * Generates a file in the resources folder of the project containing 
         * broker and transporters keys. 
         */
        public void generateKeysFile() throws NoSuchAlgorithmException
        {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEYGEN_ALG);
            keygen.initialize(KEY_SIZE);
            KeyPair keys = keygen.generateKeyPair();
            
            //TODO
        }

	public Long requestBrokerPublicKey()
	{
            return null;
	}
	
	public Long requestTransporterPublicKey(Integer transporterNumber)
	{
            return null;
	}
	
	public CertificateView requestCertificate(String name)
	{
            //FIX WSDL?
            return null;
	}  

}
