package pt.upa.authserver.ws;

import javax.jws.WebService;

// provides helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;

import java.math.BigInteger;


@WebService(endpointInterface = "pt.upa.authserver.ws.AuthenticationServerPortType")
public class AuthenticationServerPort implements AuthenticationServerPortType
{
        private final static int KEY_SIZE = 1024;
        
        private final static String KEYGEN_ALG = "RSA";
        
        private static final String KEYS_FILE_PATH = "./src/main/resources/keys.txt"; 
        
        public AuthenticationServerPort() throws NoSuchAlgorithmException, IOException
        {
                System.out.println(requestTransporterPublicKey(1).toString(16));
                System.out.println(requestTransporterPublicKey(2).toString(16));
                
                System.out.println(requestTransporterPublicKey(1).toString(16).equals(requestTransporterPublicKey(2).toString(16)));
//             File keys_file = new File(KEYS_FILE_PATH);
//             
//             BufferedReader f_reader = new BufferedReader(new FileReader(keys_file));
//             
//             String broker_public_key_string = f_reader.readLine();
//             System.out.println(broker_public_key_string);

        }
        
        /**
         * Generates a file in the resources folder of the project containing 
         * broker and transporters keys. 
         * 
         */
        private void generateKeysFile() throws NoSuchAlgorithmException, IOException
        {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEYGEN_ALG);
            keygen.initialize(KEY_SIZE);
            KeyPair keys[] = { keygen.generateKeyPair(), //broker pub,priv keys
                               keygen.generateKeyPair(), //trans1 pub,priv keys
                               keygen.generateKeyPair(), //trans2 pub,priv keys
                               keygen.generateKeyPair()  //CA pub,priv keys
                             };
            
            File keys_file = new File(KEYS_FILE_PATH);
            
            if(!keys_file.exists())
                keys_file.createNewFile();
                
            FileOutputStream f_out = new FileOutputStream(keys_file);
            BufferedWriter f_in = new BufferedWriter(new OutputStreamWriter(f_out));
                
//             writer = new FileWriter(keys_file, false); //false = don't append
//                 
            for(KeyPair kpair : keys)
            {
                String private_key = printHexBinary(kpair.getPrivate().getEncoded());
                String public_key = printHexBinary(kpair.getPublic().getEncoded());
                
                f_in.write(public_key + "\n");
                f_in.write(private_key + "\n");
            }
            
            f_in.close();
        }
        
        /**
         * WEBSERVICE: requestBrokerPublicKey
         *
         * @return BigInteger object containing broker's key.
         */
	public BigInteger requestBrokerPublicKey() 
	{
            return getBrokerPublicKey();
	}
	
	
        /**
         * WEBSERVICE: requestTransporterPublicKey
         *
         * @param transporterNumber number of the transporter (1 or 2).
         * @return BigInteger object containing the transporter's key.
         */
	public BigInteger requestTransporterPublicKey(Integer transporterNumber)
	{
            return getTransporterPublicKey(transporterNumber);
	}
	
        
        /**
         * WEBSERVICE: requestCertificate
         *
         */
	public CertificateView requestCertificate(String name)
	{
            //FIX WSDL?
            return null;
	}  
	
	//=============== PRIVATE METHODS =====================================================
	private BigInteger getBrokerPublicKey()
	{
            try
            {
                File keys_file = new File(KEYS_FILE_PATH);
            
                BufferedReader f_reader = new BufferedReader(new FileReader(keys_file));
            
                String broker_public_key_string = f_reader.readLine();
            
                return new BigInteger(broker_public_key_string, 16);
            }
            catch(Exception excep)
            {
                excep.printStackTrace();
                return null;
            }
	}
	
	private BigInteger getTransporterPublicKey(int number)
	{
            try
            {
                File keys_file = new File(KEYS_FILE_PATH);
            
                BufferedReader f_reader = new BufferedReader(new FileReader(keys_file));
            
                int lines_ignored;
                int requested_key_line = 2*number;
            
                for(lines_ignored = 0; lines_ignored < requested_key_line; lines_ignored++)
                {
                    f_reader.readLine();
                }
            
                String broker_public_key_string = f_reader.readLine();
            
                return new BigInteger(broker_public_key_string, 16);
            }
            catch(Exception excep)
            {
                excep.printStackTrace();
                return null;
            }
	}

}
