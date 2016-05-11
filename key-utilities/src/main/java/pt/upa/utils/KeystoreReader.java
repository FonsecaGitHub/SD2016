package pt.upa.utils;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.math.BigInteger;

public class KeystoreReader
{
        private KeyStore _keystore;

        public KeystoreReader(String path, String keystore_password) throws Exception
        {
            _keystore = readKeystore(path, keystore_password);
        }
        
        public KeystoreReader(File file, String keystore_password) throws Exception
        {
            _keystore = readKeystore(file, keystore_password);
        }
        
        /**
	 * Reads a private key from a key-store
	 * 
	 * @return The private key.
	 * @throws Exception 
	 */
	public BigInteger getPrivateKey(String keyAlias) throws Exception 
	{
		PrivateKey key = (PrivateKey) _keystore.getKey(keyAlias, keyAlias.toCharArray());

		return new BigInteger(printHexBinary(key.getEncoded()), 16);
	}
	
        //============= static private ========================================================
	/**
	 * Reads a KeyStore from a file path.
	 * 
	 * @return The read KeyStore.
	 * 
	 * @param file_path path to the keystore.
	 * @param keystore_password keystore's password.
	 *
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
	 * Reads a KeyStore from a file.
	 * 
	 * @return The read KeyStore.
	 * 
	 * @param file_path File of the opened keystore.
	 * @param keystore_password keystore's password.
	 *
	 * @throws Exception
	 */
	private static KeyStore readKeystore(File file, String keystore_password) throws Exception
	{
            FileInputStream file_instream = null;
            
            try
            {
                file_instream = new FileInputStream(file);
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
    
}
